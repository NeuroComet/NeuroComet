package com.kyilmaz.neurocomet

/**
 * Stable explicit entry point for IDE/debug launches that target
 * `MainActivityDefaultLauncherAlias` directly.
 *
 * The default launcher icon is handled by a separate activity-alias so this
 * concrete activity can stay available even when the launcher alias is toggled
 * off during dynamic icon switching.
 */
class MainActivityDefaultLauncherAlias : MainActivity()

