package com.kyilmaz.neurocomet

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun canonicalSettingsPaneMaxWidth(): Dp {
    val canonicalLayout = LocalCanonicalLayout.current
    return minOf(
        (canonicalLayout.maxContentWidthDp ?: canonicalLayout.widthDp).dp,
        560.dp
    )
}

