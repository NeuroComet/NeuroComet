package com.kyilmaz.neurocomet

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.sin

private fun renderDrawableResourceBitmap(
    context: android.content.Context,
    resourceId: Int,
    size: Int
): android.graphics.Bitmap? {
    val drawable = ContextCompat.getDrawable(context, resourceId) ?: return null
    return android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888).also { bitmap ->
        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
    }
}



@Composable
fun NeuroCometAmbientBackground(
    modifier: Modifier = Modifier,
    primary: Color,
    secondary: Color,
    tertiary: Color,
    motionEnabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "brandBackdrop")
    val drift = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (motionEnabled) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 26000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift"
    ).value

    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colors = listOf(
                    lerp(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface, 0.14f),
                    MaterialTheme.colorScheme.background,
                    lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant, 0.30f).copy(alpha = 0.96f)
                ),
                start = Offset.Zero,
                end = Offset.Infinite
            )
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val angle = Math.toRadians(drift.toDouble())
            val offsetA = Offset(
                x = width * (0.18f + 0.08f * cos(angle).toFloat()),
                y = height * (0.2f + 0.06f * sin(angle).toFloat())
            )
            val offsetB = Offset(
                x = width * (0.82f + 0.05f * cos(angle + 1.8).toFloat()),
                y = height * (0.18f + 0.07f * sin(angle + 1.8).toFloat())
            )
            val offsetC = Offset(
                x = width * (0.52f + 0.04f * cos(angle + 3.1).toFloat()),
                y = height * (0.82f + 0.05f * sin(angle + 3.1).toFloat())
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primary.copy(alpha = 0.18f), Color.Transparent),
                    center = offsetA,
                    radius = width * 0.42f
                ),
                radius = width * 0.42f,
                center = offsetA
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(secondary.copy(alpha = 0.14f), Color.Transparent),
                    center = offsetB,
                    radius = width * 0.36f
                ),
                radius = width * 0.36f,
                center = offsetB
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(tertiary.copy(alpha = 0.12f), Color.Transparent),
                    center = offsetC,
                    radius = width * 0.46f
                ),
                radius = width * 0.46f,
                center = offsetC
            )
        }
        content()
    }
}

@Composable
fun NeuroCometBrandMark(
    modifier: Modifier = Modifier,
    haloColor: Color,
    accentColor: Color,
    motionEnabled: Boolean = true
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "brandMark")
    val glowScale = infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = if (motionEnabled) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    ).value

    val isTablet = LocalCanonicalLayout.current.deviceFamily == CanonicalDeviceFamily.TABLET
    // Use current icon preference
    val iconResourceId = getIconResourceId(getSelectedIconStyle(context))
    val iconBitmap = remember(context, iconResourceId) {
        renderDrawableResourceBitmap(context, iconResourceId, 512)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(if (motionEnabled) glowScale else 1f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            haloColor.copy(alpha = 0.34f),
                            accentColor.copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    )
                )
                .then(if (isTablet) Modifier else Modifier.blur(28.dp))
        )

        Surface(
            modifier = Modifier
                .matchParentSize()
                .padding(10.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                iconBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(if (motionEnabled) glowScale else 1f)
                    )
                }
            }
        }
    }
}

@Composable
fun NeuroGlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    val isTablet = LocalCanonicalLayout.current.deviceFamily == CanonicalDeviceFamily.TABLET
    
    Surface(
        modifier = modifier,
        shape = shape,
        color = if (isTablet) MaterialTheme.colorScheme.surface.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surface,
        tonalElevation = if (isTablet) 0.dp else 4.dp,
        shadowElevation = if (isTablet) 0.dp else 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (isTablet) Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape) else Modifier),
            content = content
        )
    }
}

@Composable
fun BrandPill(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    val shape = RoundedCornerShape(999.dp)

    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        contentColor = contentColor
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
