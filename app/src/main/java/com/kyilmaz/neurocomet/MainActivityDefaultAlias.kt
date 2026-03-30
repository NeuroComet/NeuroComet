package com.kyilmaz.neurocomet

/**
 * Stable explicit entry point for IDE/debug launches that target
 * `MainActivityDefaultAlias` directly.
 *
 * The default launcher icon now uses a separate activity-alias so this concrete
 * activity can always remain launchable even when the default icon alias is
 * disabled during icon switching.
 */
class MainActivityDefaultAlias : MainActivity()

