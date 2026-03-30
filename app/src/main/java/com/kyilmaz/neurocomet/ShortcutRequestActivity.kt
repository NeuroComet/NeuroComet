package com.kyilmaz.neurocomet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

/**
 * Transparent activity that requests a pinned shortcut.
 * This activity is needed because pinned shortcut requests work better
 * when initiated from an Activity context rather than from within a dialog.
 */
class ShortcutRequestActivity : Activity() {

    companion object {
        private const val TAG = "ShortcutRequestActivity"
        const val EXTRA_ICON_STYLE = "icon_style"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Transparent utility Activity — never eligible for cross-device handoff
        HandoffManager.setHandoffEnabled(this, false)

        val iconStyleName = intent.getStringExtra(EXTRA_ICON_STYLE) ?: AppIconStyle.DEFAULT.name
        val iconStyle = try {
            AppIconStyle.valueOf(iconStyleName)
        } catch (e: Exception) {
            AppIconStyle.DEFAULT
        }

        Log.d(TAG, "Requesting shortcut for: $iconStyleName")

        requestPinnedShortcut(iconStyle)
    }

    private fun requestPinnedShortcut(iconStyle: AppIconStyle) {
        try {
            // Check if pinned shortcuts are supported
            val isPinSupported = ShortcutManagerCompat.isRequestPinShortcutSupported(this)
            Log.d(TAG, "isPinSupported: $isPinSupported")

            if (!isPinSupported) {
                Toast.makeText(this, "Shortcuts not supported by your launcher", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            // Create the launch intent
            val launchIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                setPackage(packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            // Create the icon bitmap using the same rendering path as the picker preview
            val iconBitmap = renderDepthEnhancedIconBitmap(this, iconStyle, 432, previewOptimized = true)
                ?: createFallbackShortcutBitmap(iconStyle)
            // Use adaptive bitmap - launcher will apply its icon shape mask
            val iconCompat = IconCompat.createWithAdaptiveBitmap(iconBitmap)

            // Create unique shortcut ID
            val shortcutId = "neurocomet_${iconStyle.name.lowercase()}_${System.currentTimeMillis()}"

            // Build shortcut info
            val shortcutInfo = ShortcutInfoCompat.Builder(this, shortcutId)
                .setShortLabel(getString(R.string.app_name))
                .setLongLabel("NeuroComet - ${getString(iconStyle.titleRes)}")
                .setIcon(iconCompat)
                .setIntent(launchIntent)
                .build()

            // Also add as dynamic shortcut
            try {
                ShortcutManagerCompat.pushDynamicShortcut(this, shortcutInfo)
                Log.d(TAG, "Dynamic shortcut added")
            } catch (e: Exception) {
                Log.w(TAG, "Could not add dynamic shortcut: ${e.message}")
            }

            // Request pinned shortcut - this should show the system dialog
            val result = ShortcutManagerCompat.requestPinShortcut(this, shortcutInfo, null)
            Log.d(TAG, "requestPinShortcut result: $result")

            if (result) {
                Toast.makeText(this, "Look for 'Add' button at the bottom!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Shortcut added to long-press menu", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to request shortcut", e)
            Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // Finish after a short delay to allow the dialog to appear
        window.decorView.postDelayed({
            finish()
        }, 300)
    }

    /**
     * Creates a bitmap for the shortcut icon that matches the app icon preview.
     * Uses adaptive bitmap sizing so the launcher applies proper icon masking.
     */
    private fun createFallbackShortcutBitmap(iconStyle: AppIconStyle): android.graphics.Bitmap {
        // Use 432px (108dp at xxxhdpi) for adaptive icon sizing
        val size = 432
        val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        val adaptiveDrawable = getAdaptiveIconDrawable(this, iconStyle)
            ?: androidx.core.content.ContextCompat.getDrawable(this, getIconResourceId(iconStyle))

        adaptiveDrawable?.setBounds(0, 0, size, size)
        adaptiveDrawable?.draw(canvas)

        return bitmap
    }
}
