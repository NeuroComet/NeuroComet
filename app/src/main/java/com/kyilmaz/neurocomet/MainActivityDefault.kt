package com.kyilmaz.neurocomet

/**
 * Stable explicit entry point for IDE/debug launches.
 *
 * The launcher icon system swaps activity-alias components on and off. Some run
 * configurations can still try to launch `MainActivityDefault` explicitly, so we
 * keep this real activity class available regardless of which launcher alias is
 * currently enabled.
 */
class MainActivityDefault : MainActivity()

