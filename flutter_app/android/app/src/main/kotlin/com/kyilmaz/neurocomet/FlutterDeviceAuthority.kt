package com.kyilmaz.neurocomet

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.security.MessageDigest

/**
 * Device-level authorization gate for debug/dev functionality in the Flutter host.
 *
 * Mirrors the native Android app's committed household whitelist and local-only
 * developer device hashes from build configurations.
 */
object FlutterDeviceAuthority {

    private const val TAG = "DeviceAuthority"

    private val HOUSEHOLD_DEVICE_HASHES: Set<String> = setOf(
        // ── bkyil — Pixel 10 Pro (Android 17 / CP21.260306.017) ─
        "f9f1daddf36b9338c062cf2fd763cd4955511be1a01663d6483f7b25f3f94c46",

        // ── Betul's device — Pixel 9 (Android 17 / CP21.260306.017)
        "4d18ac796abdb71814159e41a7e5fdd5b63b4ba659d3a5be66cea9ee8dcef1b3",
    )

    @SuppressLint("HardwareIds")
    fun computeDeviceHash(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"

        val raw = buildString {
            append(Build.FINGERPRINT); append("|")
            append(Build.BOARD);       append("|")
            append(Build.BRAND);       append("|")
            append(Build.MODEL);       append("|")
            append(androidId)
        }

        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(raw.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun isHouseholdAuthorizedDevice(context: Context): Boolean {
        val hash = computeDeviceHash(context)
        return HOUSEHOLD_DEVICE_HASHES.contains(hash)
    }

    fun isAuthorizedDevice(context: Context): Boolean {
        val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        // Detect Play Console Internal version via signature
        val internalSignature = BuildConfig.INTERNAL_SIGNATURE_HASH
        val currentSignature = getAppSignatureHash(context)
        val isInternalBuild = internalSignature.isNotBlank() && currentSignature == internalSignature
        val hash = computeDeviceHash(context)
        val isHouseholdDevice = HOUSEHOLD_DEVICE_HASHES.contains(hash)

        val localDeveloperHashes = BuildConfig.DEVELOPER_DEVICE_HASH
            .split(',')
            .map { it.trim() }
            .filter { it.isNotBlank() }
        val authorizedHashes = HOUSEHOLD_DEVICE_HASHES + localDeveloperHashes
        val isAuthorizedHash = authorizedHashes.contains(hash)

        val result = isHouseholdDevice || ((isDebug || isInternalBuild) && isAuthorizedHash)

        if (!result && (isDebug || isInternalBuild)) {
            Log.w(TAG, "DEVICE NOT AUTHORIZED: Hash '$hash' is not in the allowed list.")
            Log.w(TAG, "Diagnostic Info: Debug=$isDebug, Internal=${currentSignature == internalSignature}")
        }

        return result
    }

    fun canUseDeveloperTools(context: Context): Boolean = isAuthorizedDevice(context)

    fun canSkipAuth(context: Context): Boolean = canUseDeveloperTools(context)

    fun requireDeveloperToolsAccess(context: Context, operation: String) {
        if (canUseDeveloperTools(context)) return

        logDevAccessInfo(context)
        Log.wtf(TAG, "Unauthorized developer-tools access attempt: $operation")
        throw SecurityException(
            "Developer tools are restricted. Unauthorized attempt to $operation."
        )
    }

    fun logDevAccessInfo(context: Context) {
        try {
            val hash = computeDeviceHash(context)
            val currentSignature = getAppSignatureHash(context)
            
            Log.i(TAG, "--- DEV ACCESS DIAGNOSTIC INFO ---")
            Log.i(TAG, "DEVICE HASH: $hash")
            Log.i(TAG, "APP SIGNATURE: $currentSignature")
            Log.i(TAG, "To authorize, add to local.properties and rebuild:")
            Log.i(TAG, "DEVELOPER_DEVICE_HASH=$hash")
            Log.i(TAG, "INTERNAL_SIGNATURE_HASH=$currentSignature")
            Log.i(TAG, "----------------------------------")
        } catch (e: Exception) {
            Log.e(TAG, "Error logging dev access info", e)
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    fun getAppSignatureHash(context: Context): String {
        return try {
            val pm = context.packageManager
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pm.getPackageInfo(
                    context.packageName,
                    android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(
                    context.packageName,
                    android.content.pm.PackageManager.GET_SIGNATURES
                )
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            if (signatures.isNullOrEmpty()) return ""

            val digest = MessageDigest.getInstance("SHA-256")
            val firstSignature = signatures[0] ?: return ""
            val signatureBytes = firstSignature.toByteArray()
            val hashBytes = digest.digest(signatureBytes)
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            ""
        }
    }
}
