package com.kyilmaz.neurocomet

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NeuroLoadingAnimation(
    modifier: Modifier = Modifier,
    color: Color? = null,
) {
    val resolvedColor = color ?: MaterialTheme.colorScheme.primary
    val infiniteTransition = rememberInfiniteTransition(label = "neuroLoading")
    
    // Smooth, predictable breathing rhythm (calming for sensory processing)
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheScale"
    )

    val fadeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fadeAlpha"
    )

    Box(
        modifier = modifier
            .sizeIn(minWidth = 100.dp, minHeight = 100.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension * 0.35f

            // Outer soft halo
            drawCircle(
                color = resolvedColor.copy(alpha = fadeAlpha * 0.3f),
                radius = baseRadius * breatheScale * 1.4f,
                center = center
            )

            // Middle soft halo
            drawCircle(
                color = resolvedColor.copy(alpha = fadeAlpha * 0.6f),
                radius = baseRadius * breatheScale * 1.15f,
                center = center
            )

            // Inner solid but soft core
            drawCircle(
                color = resolvedColor.copy(alpha = fadeAlpha),
                radius = baseRadius * breatheScale * 0.9f,
                center = center
            )
        }
    }
}
