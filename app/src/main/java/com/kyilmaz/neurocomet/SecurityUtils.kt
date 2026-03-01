package com.kyilmaz.neurocomet

import android.util.Base64

/**
 * Utility for basic string obfuscation to prevent simple string searches in the APK.
 * Note: This is not "unbreakable" encryption, but it's much better than plain text
 * for discouraging casual bad actors.
 */
object SecurityUtils {

    // Simple XOR key - should be different from the one used in build.gradle.kts if possible,
    // but for simplicity we'll keep them consistent.
    private const val XOR_KEY = "neurocomet_internal_security_key_2025"

    /**
     * De-obfuscates a string that was obfuscated with XOR and Base64 encoded.
     */
    fun decrypt(obfuscated: String?): String {
        if (obfuscated == null || obfuscated.isEmpty() || obfuscated == "null" || obfuscated == "\"\"") {
            return ""
        }

        return try {
            val decoded = Base64.decode(obfuscated, Base64.DEFAULT)
            val result = StringBuilder()
            for (i in decoded.indices) {
                result.append((decoded[i].toInt() xor XOR_KEY[i % XOR_KEY.length].toInt()).toChar())
            }
            result.toString()
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Obfuscates a string using XOR and Base64 encoding.
     * Use this to generate obfuscated strings for your local.properties if you want
     * an extra layer of security, though build.gradle.kts will handle it automatically now.
     */
    fun encrypt(plain: String): String {
        val bytes = plain.toByteArray(Charsets.UTF_8)
        val obfuscated = ByteArray(bytes.size)
        for (i in bytes.indices) {
            obfuscated[i] = (bytes[i].toInt() xor XOR_KEY[i % XOR_KEY.length].toInt()).toByte()
        }
        return Base64.encodeToString(obfuscated, Base64.NO_WRAP)
    }
}
