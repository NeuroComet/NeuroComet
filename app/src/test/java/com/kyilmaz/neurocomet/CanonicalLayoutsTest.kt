package com.kyilmaz.neurocomet

import android.content.res.Configuration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CanonicalLayoutsTest {

    @Test
    fun compactPortraitPhone_usesBottomBarAndSinglePane() {
        val spec = canonicalLayoutForDp(
            widthDp = 411,
            heightDp = 891,
            orientation = Configuration.ORIENTATION_PORTRAIT
        )

        assertEquals(CanonicalWidthClass.COMPACT, spec.widthClass)
        assertEquals(CanonicalDeviceFamily.PHONE, spec.deviceFamily)
        assertEquals(CanonicalNavigationChrome.BOTTOM_BAR, spec.navigationChrome)
        assertEquals(CanonicalPaneLayout.SINGLE, spec.paneLayout)
        assertFalse(spec.supportsMultiPane)
    }

    @Test
    fun mediumLandscape_usesRailAndDualPane() {
        val spec = canonicalLayoutForDp(
            widthDp = 700,
            heightDp = 500,
            orientation = Configuration.ORIENTATION_LANDSCAPE
        )

        assertEquals(CanonicalWidthClass.MEDIUM, spec.widthClass)
        assertEquals(CanonicalNavigationChrome.NAVIGATION_RAIL, spec.navigationChrome)
        assertEquals(CanonicalPaneLayout.DUAL, spec.paneLayout)
        assertTrue(spec.supportsMultiPane)
        assertTrue(spec.isLandscape)
    }

    @Test
    fun expandedTablet_usesPermanentDrawerAndDualPane() {
        val spec = canonicalLayoutForDp(
            widthDp = 900,
            heightDp = 1000,
            orientation = Configuration.ORIENTATION_PORTRAIT
        )

        assertEquals(CanonicalWidthClass.EXPANDED, spec.widthClass)
        assertEquals(CanonicalDeviceFamily.TABLET, spec.deviceFamily)
        assertEquals(CanonicalNavigationChrome.PERMANENT_DRAWER, spec.navigationChrome)
        assertEquals(CanonicalPaneLayout.DUAL, spec.paneLayout)
        assertEquals(CanonicalAuthLayout.SPLIT, spec.authLayout)
    }

    @Test
    fun largeDesktop_usesTriplePaneAndDesktopFamily() {
        val spec = canonicalLayoutForDp(
            widthDp = 1440,
            heightDp = 960,
            orientation = Configuration.ORIENTATION_LANDSCAPE
        )

        assertEquals(CanonicalWidthClass.LARGE, spec.widthClass)
        assertEquals(CanonicalDeviceFamily.DESKTOP, spec.deviceFamily)
        assertEquals(CanonicalNavigationChrome.PERMANENT_DRAWER, spec.navigationChrome)
        assertEquals(CanonicalPaneLayout.TRIPLE, spec.paneLayout)
        assertEquals(1440, spec.maxContentWidthDp)
    }

    @Test
    fun xrLikeWideCompactLayout_usesRailAndSinglePane() {
        val spec = canonicalLayoutForDp(
            widthDp = 1280,
            heightDp = 400,
            orientation = Configuration.ORIENTATION_LANDSCAPE
        )

        assertEquals(CanonicalWidthClass.LARGE, spec.widthClass)
        assertEquals(CanonicalHeightClass.COMPACT, spec.heightClass)
        assertEquals(CanonicalDeviceFamily.TABLET, spec.deviceFamily)
        assertEquals(CanonicalNavigationChrome.NAVIGATION_RAIL, spec.navigationChrome)
        assertEquals(CanonicalPaneLayout.SINGLE, spec.paneLayout)
        assertEquals(CanonicalAuthLayout.STACKED, spec.authLayout)
        assertTrue(spec.isWideCompact)
        assertFalse(spec.supportsMultiPane)
        assertEquals(960, spec.maxContentWidthDp)
    }
}
