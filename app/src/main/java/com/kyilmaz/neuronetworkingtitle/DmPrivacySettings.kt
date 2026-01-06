package com.kyilmaz.neuronetworkingtitle

import android.content.Context
import androidx.core.content.edit

/**
 * Local-only DM privacy controls.
 *
 * Notes:
 * - This is NOT a security boundary. In production, enforce in backend too.
 * - Still useful for UX + safety: block/mute lists, persisted.
 */
object DmPrivacySettings {
    private const val PREFS = "dm_privacy_settings"
    private const val KEY_BLOCKED = "blocked_ids_csv"
    private const val KEY_MUTED = "muted_ids_csv"

    fun getBlocked(context: Context): Set<String> =
        readCsvSet(context, KEY_BLOCKED)

    fun getMuted(context: Context): Set<String> =
        readCsvSet(context, KEY_MUTED)

    fun isBlocked(context: Context, userId: String): Boolean =
        getBlocked(context).contains(userId)

    fun isMuted(context: Context, userId: String): Boolean =
        getMuted(context).contains(userId)

    fun block(context: Context, userId: String) {
        val updated = getBlocked(context).toMutableSet().apply { add(userId) }
        writeCsvSet(context, KEY_BLOCKED, updated)
        // If you block someone, muting becomes redundant.
        unmute(context, userId)
    }

    fun unblock(context: Context, userId: String) {
        val updated = getBlocked(context).toMutableSet().apply { remove(userId) }
        writeCsvSet(context, KEY_BLOCKED, updated)
    }

    fun mute(context: Context, userId: String) {
        val updated = getMuted(context).toMutableSet().apply { add(userId) }
        writeCsvSet(context, KEY_MUTED, updated)
    }

    fun unmute(context: Context, userId: String) {
        val updated = getMuted(context).toMutableSet().apply { remove(userId) }
        writeCsvSet(context, KEY_MUTED, updated)
    }

    private fun readCsvSet(context: Context, key: String): Set<String> {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = p.getString(key, "").orEmpty().trim()
        if (raw.isBlank()) return emptySet()
        return raw.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toSet()
    }

    private fun writeCsvSet(context: Context, key: String, values: Set<String>) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        p.edit {
            putString(key, values.sorted().joinToString(","))
        }
    }
}

