package com.kyilmaz.neuronetworkingtitle

/**
 * App-level safety audience.
 *
 * - UNDER_13: strongest protections. Intended for kids accounts.
 * - TEEN: mild protections (optional future).
 * - ADULT: standard experience.
 */
enum class Audience {
    UNDER_13,
    TEEN,
    ADULT
}

/**
 * Controls the strength of filtering in UNDER_13 mode.
 */
enum class KidsFilterLevel {
    STRICT, // hide/sanitize more aggressively
    MODERATE
}

data class SafetyState(
    val audience: Audience = Audience.ADULT,
    val kidsFilterLevel: KidsFilterLevel = KidsFilterLevel.STRICT,
    val isParentalPinSet: Boolean = false
) {
    val isKidsMode: Boolean get() = audience == Audience.UNDER_13
}

