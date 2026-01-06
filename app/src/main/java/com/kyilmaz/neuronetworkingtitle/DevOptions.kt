package com.kyilmaz.neuronetworkingtitle

/**
 * Developer options are intended for DEBUG/testing only.
 *
 * They let you deterministically test DM moderation, delivery failures, throttling,
 * and safety/kids-mode gating.
 */
data class DevOptions(
    // Global
    val devMenuEnabled: Boolean = false,
    val showDmDebugOverlay: Boolean = false,

    // DM delivery simulation
    val dmForceSendFailure: Boolean = false,
    val dmArtificialSendDelayMs: Long = 450L,

    // DM throttling
    val dmDisableRateLimit: Boolean = false,
    val dmMinIntervalOverrideMs: Long? = null,

    // Moderation override
    val moderationOverride: DevModerationOverride = DevModerationOverride.OFF,

    // Safety overrides
    val forceAudience: Audience? = null,
    val forceKidsFilterLevel: KidsFilterLevel? = null,
    val forcePinSet: Boolean = false,
    val forcePinVerifySuccess: Boolean = false
)

enum class DevModerationOverride {
    OFF,
    CLEAN,
    FLAGGED,
    BLOCKED
}

