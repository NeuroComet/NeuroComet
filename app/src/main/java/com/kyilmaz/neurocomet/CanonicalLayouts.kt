package com.kyilmaz.neurocomet

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration

enum class CanonicalWidthClass {
    COMPACT,
    MEDIUM,
    EXPANDED,
    LARGE
}

enum class CanonicalHeightClass {
    COMPACT,
    MEDIUM,
    EXPANDED
}

enum class CanonicalDeviceFamily {
    PHONE,
    TABLET,
    DESKTOP
}

enum class CanonicalNavigationChrome {
    BOTTOM_BAR,
    NAVIGATION_RAIL,
    PERMANENT_DRAWER
}

enum class CanonicalPaneLayout(val paneCount: Int) {
    SINGLE(1),
    DUAL(2),
    TRIPLE(3)
}

enum class CanonicalAuthLayout {
    STACKED,
    BALANCED,
    SPLIT
}

@Stable
data class CanonicalLayoutSpec(
    val widthDp: Int,
    val heightDp: Int,
    val smallestWidthDp: Int,
    val widthClass: CanonicalWidthClass,
    val heightClass: CanonicalHeightClass,
    val deviceFamily: CanonicalDeviceFamily,
    val isLandscape: Boolean,
    val navigationChrome: CanonicalNavigationChrome,
    val paneLayout: CanonicalPaneLayout,
    val authLayout: CanonicalAuthLayout,
    val maxContentWidthDp: Int?
) {
    val supportsMultiPane: Boolean = paneLayout != CanonicalPaneLayout.SINGLE
    val isCompactHeight: Boolean = heightClass == CanonicalHeightClass.COMPACT
    val isLargeScreen: Boolean = widthClass >= CanonicalWidthClass.EXPANDED
    val isWideCompact: Boolean = widthClass >= CanonicalWidthClass.EXPANDED && isCompactHeight
}

val LocalCanonicalLayout = staticCompositionLocalOf {
    canonicalLayoutForDp(widthDp = 411, heightDp = 891)
}

fun canonicalLayoutForDp(
    widthDp: Int,
    heightDp: Int,
    orientation: Int = if (widthDp >= heightDp) {
        Configuration.ORIENTATION_LANDSCAPE
    } else {
        Configuration.ORIENTATION_PORTRAIT
    }
): CanonicalLayoutSpec {
    val safeWidth = widthDp.coerceAtLeast(1)
    val safeHeight = heightDp.coerceAtLeast(1)
    val smallestWidth = minOf(safeWidth, safeHeight)
    val widthClass = when {
        safeWidth >= 1200 -> CanonicalWidthClass.LARGE
        safeWidth >= 840 -> CanonicalWidthClass.EXPANDED
        safeWidth >= 600 -> CanonicalWidthClass.MEDIUM
        else -> CanonicalWidthClass.COMPACT
    }
    val heightClass = when {
        safeHeight >= 900 -> CanonicalHeightClass.EXPANDED
        safeHeight >= 480 -> CanonicalHeightClass.MEDIUM
        else -> CanonicalHeightClass.COMPACT
    }
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE || safeWidth > safeHeight
    val isWideCompact = widthClass >= CanonicalWidthClass.EXPANDED && heightClass == CanonicalHeightClass.COMPACT
    val deviceFamily = when {
        widthClass == CanonicalWidthClass.LARGE && !isWideCompact -> CanonicalDeviceFamily.DESKTOP
        isWideCompact -> CanonicalDeviceFamily.TABLET
        smallestWidth >= 600 -> CanonicalDeviceFamily.TABLET
        else -> CanonicalDeviceFamily.PHONE
    }
    val navigationChrome = when {
        isWideCompact -> CanonicalNavigationChrome.NAVIGATION_RAIL
        widthClass == CanonicalWidthClass.LARGE -> CanonicalNavigationChrome.PERMANENT_DRAWER
        widthClass == CanonicalWidthClass.EXPANDED && heightClass != CanonicalHeightClass.COMPACT -> CanonicalNavigationChrome.PERMANENT_DRAWER
        widthClass == CanonicalWidthClass.COMPACT && !isLandscape -> CanonicalNavigationChrome.BOTTOM_BAR
        else -> CanonicalNavigationChrome.NAVIGATION_RAIL
    }
    val paneLayout = when {
        isWideCompact -> CanonicalPaneLayout.SINGLE
        widthClass == CanonicalWidthClass.LARGE -> CanonicalPaneLayout.TRIPLE
        widthClass == CanonicalWidthClass.EXPANDED -> CanonicalPaneLayout.DUAL
        widthClass == CanonicalWidthClass.MEDIUM && heightClass != CanonicalHeightClass.COMPACT -> CanonicalPaneLayout.DUAL
        else -> CanonicalPaneLayout.SINGLE
    }
    val authLayout = when {
        paneLayout == CanonicalPaneLayout.TRIPLE -> CanonicalAuthLayout.SPLIT
        paneLayout == CanonicalPaneLayout.DUAL && heightClass != CanonicalHeightClass.COMPACT -> CanonicalAuthLayout.SPLIT
        heightClass == CanonicalHeightClass.COMPACT -> CanonicalAuthLayout.STACKED
        else -> CanonicalAuthLayout.BALANCED
    }
    val maxContentWidthDp = when {
        isWideCompact -> 960
        widthClass == CanonicalWidthClass.LARGE -> 1440
        widthClass == CanonicalWidthClass.EXPANDED -> 1200
        widthClass == CanonicalWidthClass.MEDIUM -> 960
        else -> null
    }

    return CanonicalLayoutSpec(
        widthDp = safeWidth,
        heightDp = safeHeight,
        smallestWidthDp = smallestWidth,
        widthClass = widthClass,
        heightClass = heightClass,
        deviceFamily = deviceFamily,
        isLandscape = isLandscape,
        navigationChrome = navigationChrome,
        paneLayout = paneLayout,
        authLayout = authLayout,
        maxContentWidthDp = maxContentWidthDp
    )
}

@Composable
fun rememberCanonicalLayout(): CanonicalLayoutSpec {
    val configuration = LocalConfiguration.current
    return remember(
        configuration.screenWidthDp,
        configuration.screenHeightDp,
        configuration.orientation
    ) {
        canonicalLayoutForDp(
            widthDp = configuration.screenWidthDp,
            heightDp = configuration.screenHeightDp,
            orientation = configuration.orientation
        )
    }
}

@Composable
fun ProvideCanonicalLayout(
    content: @Composable () -> Unit
) {
    val layout = rememberCanonicalLayout()
    CompositionLocalProvider(LocalCanonicalLayout provides layout) {
        content()
    }
}

