package com.kyilmaz.neurocomet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * A calming, neurodivergent-friendly splash screen that adapts to the user's NeuroState.
 *
 * Design principles:
 * - Gentle, non-jarring animations
 * - Soothing color palette adapted to user's needs
 * - Affirmative, welcoming messaging personalized by state
 * - Predictable timing (longer for overwhelm, shorter for focus)
 * - No flashing or rapid movements
 *
 * @param onFinished Callback when splash is complete
 * @param neuroState The user's current NeuroState for personalization
 * @param minDurationMs Fallback minimum time (overridden by state config)
 */
@Composable
fun NeuroSplashScreen(
    onFinished: () -> Unit,
    neuroState: NeuroState = NeuroState.DEFAULT,
    animationSettings: AnimationSettings = AnimationSettings(),
    minDurationMs: Long = 2000L
) {
    val context = LocalContext.current
    val config = remember(neuroState) { getSplashConfigForState(neuroState) }
    val effectiveDuration = maxOf(config.durationMs, minDurationMs)
    val resolvedMessages = remember(config, context) { config.getMessages(context) }
    val messages = remember(resolvedMessages, effectiveDuration) {
        when {
            effectiveDuration <= 1500L -> listOf(resolvedMessages.first())
            effectiveDuration <= 2200L && resolvedMessages.size > 1 -> listOf(
                resolvedMessages.first(),
                resolvedMessages.last()
            )
            else -> resolvedMessages
        }
    }
    val tagline = remember(config, context) { config.getTagline(context) }
    val allowLogoMotion = animationSettings.shouldAnimate(AnimationType.LOGO)
    val allowLoadingMotion = animationSettings.shouldAnimate(AnimationType.LOADING)
    val allowMessageTransitions = animationSettings.shouldAnimate(AnimationType.TRANSITION)

    var visible by remember { mutableStateOf(true) }
    var messageIndex by remember { mutableIntStateOf(0) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 520,
            easing = FastOutSlowInEasing
        ),
        label = "splashAlpha"
    )

    LaunchedEffect(messages, effectiveDuration) {
        if (messages.size <= 1) {
            delay((effectiveDuration - 420L).coerceAtLeast(900L))
        } else {
            val messageDelay = ((effectiveDuration - 900L) / messages.size.coerceAtLeast(1)).coerceAtLeast(650L)
            messages.indices.forEach { index ->
                messageIndex = index
                delay(messageDelay)
            }
        }

        visible = false
        delay(520)
        onFinished()
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val accentColor = when (config.animationStyle) {
        SplashAnimationStyle.ENERGY_BURST,
        SplashAnimationStyle.CREATIVE_SWIRL,
        SplashAnimationStyle.SENSORY_SPARKLE,
        SplashAnimationStyle.RAINBOW_SPARKLE -> tertiaryColor
        SplashAnimationStyle.ROUTINE_GRID,
        SplashAnimationStyle.FOCUS_PULSE,
        SplashAnimationStyle.CONTRAST_RINGS,
        SplashAnimationStyle.PATTERN_SHAPES -> secondaryColor
        else -> primaryColor
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BrandPill(text = stringResource(R.string.loading))

                    NeuroCometBrandMark(
                        modifier = Modifier.size(100.dp),
                        haloColor = primaryColor,
                        accentColor = accentColor,
                        motionEnabled = allowLogoMotion
                    )

                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    // Messages — no glass/translucent panel
                    if (allowMessageTransitions) {
                        AnimatedContent(
                            targetState = messageIndex,
                            transitionSpec = {
                                fadeIn(animationSpec = androidx.compose.animation.core.tween(260, easing = FastOutSlowInEasing)) togetherWith
                                    fadeOut(animationSpec = androidx.compose.animation.core.tween(220, easing = FastOutSlowInEasing))
                            },
                            label = "splashMessage"
                        ) { index ->
                            Text(
                                text = messages.getOrElse(index) { messages.firstOrNull().orEmpty() },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Text(
                            text = messages.getOrElse(messageIndex) { messages.firstOrNull().orEmpty() },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }

                    SplashMessageIndicators(
                        count = messages.size,
                        selectedIndex = messageIndex,
                        accentColor = accentColor
                    )

                    if (allowLoadingMotion) {
                        NeuroLoadingAnimation(
                            modifier = Modifier.size(56.dp),
                            color = accentColor
                        )
                    } else {
                        StaticLoadingIndicator(color = accentColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun SplashMessageIndicators(
    count: Int,
    selectedIndex: Int,
    accentColor: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count.coerceAtLeast(1)) { index ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (selected) 26.dp else 10.dp)
                    .background(
                        color = if (selected) accentColor else accentColor.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(999.dp)
                    )
            )
        }
    }
}

@Composable
private fun StaticLoadingIndicator(color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == 1) 10.dp else 8.dp)
                    .background(
                        color = color.copy(alpha = 0.45f + (index * 0.12f)),
                        shape = RoundedCornerShape(999.dp)
                    )
            )
        }
    }
}
