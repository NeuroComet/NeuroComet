package com.kyilmaz.neuronetworkingtitle

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Minimal local-only parental controls store.
 *
 * This is a UI/logic gate only (not a hardened security boundary). It prevents casual access.
 */
object SafetySettings {
    private const val PREFS = "safety_settings"

    private const val KEY_AUDIENCE = "audience" // ADULT | TEEN | UNDER_13
    private const val KEY_KIDS_FILTER_LEVEL = "kids_filter_level" // STRICT | MODERATE

    private const val KEY_PIN_SALT = "pin_salt"
    private const val KEY_PIN_HASH = "pin_hash"

    fun getSafetyState(context: Context): SafetyState {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        val audience = runCatching {
            Audience.valueOf(p.getString(KEY_AUDIENCE, Audience.ADULT.name) ?: Audience.ADULT.name)
        }.getOrDefault(Audience.ADULT)

        val level = runCatching {
            KidsFilterLevel.valueOf(p.getString(KEY_KIDS_FILTER_LEVEL, KidsFilterLevel.STRICT.name) ?: KidsFilterLevel.STRICT.name)
        }.getOrDefault(KidsFilterLevel.STRICT)

        val hasPin = !p.getString(KEY_PIN_HASH, null).isNullOrBlank() && !p.getString(KEY_PIN_SALT, null).isNullOrBlank()

        return SafetyState(audience = audience, kidsFilterLevel = level, isParentalPinSet = hasPin)
    }

    fun setAudience(context: Context, audience: Audience) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putString(KEY_AUDIENCE, audience.name)
        }
    }

    fun setKidsFilterLevel(context: Context, level: KidsFilterLevel) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putString(KEY_KIDS_FILTER_LEVEL, level.name)
        }
    }

    fun setPin(context: Context, pin: String) {
        require(pin.length in 4..8) { "PIN must be 4-8 digits" }
        require(pin.all { it.isDigit() }) { "PIN must be numeric" }

        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = hashPin(pin, salt)

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putString(KEY_PIN_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            putString(KEY_PIN_HASH, Base64.encodeToString(hash, Base64.NO_WRAP))
        }
    }

    fun clearPin(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            remove(KEY_PIN_SALT)
            remove(KEY_PIN_HASH)
        }
    }

    fun verifyPin(context: Context, pinAttempt: String): Boolean {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val saltB64 = p.getString(KEY_PIN_SALT, null) ?: return false
        val hashB64 = p.getString(KEY_PIN_HASH, null) ?: return false

        val salt = Base64.decode(saltB64, Base64.DEFAULT)
        val expected = Base64.decode(hashB64, Base64.DEFAULT)
        val actual = hashPin(pinAttempt, salt)

        return MessageDigest.isEqual(expected, actual)
    }

    private fun hashPin(pin: String, salt: ByteArray): ByteArray {
        // Simple SHA-256(salt || pin). Good enough for local gate. Not meant as high-security storage.
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        md.update(pin.toByteArray(Charsets.UTF_8))
        return md.digest()
    }
}

