import java.util.Properties
import org.gradle.api.GradleException

plugins {
    id("com.android.application")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
    id("com.google.gms.google-services")
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

// Load properties from multiple potential secret files
val combinedProperties = Properties()
listOf(
    rootProject.file("local.properties"),
    rootProject.file("../local.properties"),
    rootProject.file("../../local.properties"),
    rootProject.file("secrets.properties"),
    rootProject.file("../secrets.properties"),
    rootProject.file("../../secrets.properties")
).forEach { file ->
    if (file.exists()) {
        file.inputStream().use { combinedProperties.load(it) }
    }
}

val requestedTaskNames = gradle.startParameter.taskNames
val isBundleTaskRequested = requestedTaskNames.any { taskName ->
    taskName.contains("bundle", ignoreCase = true)
}
val isReleaseStyleBuildRequested = requestedTaskNames.any { taskName ->
    taskName.contains("release", ignoreCase = true) ||
        taskName.contains("bundle", ignoreCase = true) ||
        taskName.contains("publish", ignoreCase = true)
}

val configuredObfuscationKey = combinedProperties.getProperty("OBFUSCATION_KEY")?.trim().orEmpty()
if (isReleaseStyleBuildRequested && configuredObfuscationKey.isBlank()) {
    throw GradleException(
        "OBFUSCATION_KEY is required for release-style builds. Add it to local.properties or secrets.properties before building."
    )
}
val obfuscationKey: String = configuredObfuscationKey.ifBlank {
    "debug-local-obfuscation-key"
}

fun obfuscate(value: String?): String {
    if (value.isNullOrEmpty()) return ""
    val bytes = value.toByteArray(Charsets.UTF_8)
    val result = StringBuilder()
    for (i in bytes.indices) {
        val obfuscatedByte = bytes[i].toInt() xor obfuscationKey[i % obfuscationKey.length].code
        result.append(String.format("%02x", obfuscatedByte and 0xFF))
    }
    return result.toString()
}

android {
    namespace = "com.kyilmaz.neurocomet"
    compileSdk = 36
    ndkVersion = "28.2.13676358"  // Required by integration_test

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.kyilmaz.neurocomet"
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = 26
        targetSdk = 36
        versionCode = flutter.versionCode
        versionName = flutter.versionName

        // Enable multidex for large apps
        multiDexEnabled = true

        // Load credentials from combined properties
        val supabaseUrl = combinedProperties.getProperty("SUPABASE_URL") ?: ""
        val supabaseKey = combinedProperties.getProperty("SUPABASE_KEY") ?: ""
        val devHash = combinedProperties.getProperty("DEVELOPER_DEVICE_HASH") ?: "4d18ac796abdb71814159e41a7e5fdd5b63b4ba659d3a5be66cea9ee8dcef1b3"
        val geminiApiKey = combinedProperties.getProperty("GEMINI_API_KEY") ?: ""
        val revenueCatKey = combinedProperties.getProperty("REVENUECAT_API_KEY") ?: ""
        val admobAppId = combinedProperties.getProperty("ADMOB_APP_ID") ?: "ca-app-pub-3940256099942544~3347511713"
        val turnUrl = combinedProperties.getProperty("TURN_URL") ?: ""
        val turnUsername = combinedProperties.getProperty("TURN_USERNAME") ?: ""
        val turnPassword = combinedProperties.getProperty("TURN_PASSWORD") ?: ""

        // AdMob Unit IDs
        val bannerAdId = combinedProperties.getProperty("ADMOB_BANNER_ID") ?: "ca-app-pub-3940256099942544/6300978111"
        val interstitialAdId = combinedProperties.getProperty("ADMOB_INTERSTITIAL_ID") ?: "ca-app-pub-3940256099942544/1033173712"
        val rewardedAdId = combinedProperties.getProperty("ADMOB_REWARDED_ID") ?: "ca-app-pub-3940256099942544/5224354917"

        val internalSignature = combinedProperties.getProperty("INTERNAL_SIGNATURE_HASH") ?: ""

        // Inject obfuscated keys into BuildConfig
        buildConfigField("String", "SUPABASE_URL", "\"${obfuscate(supabaseUrl)}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${obfuscate(supabaseKey)}\"")
        buildConfigField("String", "DEVELOPER_DEVICE_HASH", "\"$devHash\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"${obfuscate(geminiApiKey)}\"")
        buildConfigField("String", "REVENUECAT_API_KEY", "\"${obfuscate(revenueCatKey)}\"")
        buildConfigField("String", "ADMOB_BANNER_ID", "\"${obfuscate(bannerAdId)}\"")
        buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"${obfuscate(interstitialAdId)}\"")
        buildConfigField("String", "ADMOB_REWARDED_ID", "\"${obfuscate(rewardedAdId)}\"")
        buildConfigField("String", "TURN_URL", "\"${obfuscate(turnUrl)}\"")
        buildConfigField("String", "TURN_USERNAME", "\"${obfuscate(turnUsername)}\"")
        buildConfigField("String", "TURN_PASSWORD", "\"${obfuscate(turnPassword)}\"")

        // Non-secret but device-specific config
        buildConfigField("String", "DEVELOPER_DEVICE_HASH", "\"$devHash\"")
        buildConfigField("String", "INTERNAL_SIGNATURE_HASH", "\"$internalSignature\"")
        buildConfigField("Boolean", "ALLOW_GUEST_ACCESS", "false")
        buildConfigField("String", "ADMOB_APP_ID", "\"${obfuscate(admobAppId)}\"")

        // Obfuscation key — injected so SecurityUtils can decrypt at runtime
        buildConfigField("String", "OBFUSCATION_KEY", "\"$obfuscationKey\"")

        // Add AdMob App ID as a manifest placeholder
        manifestPlaceholders["admobAppId"] = admobAppId
    }

    signingConfigs {
        create("release") {
            val storeFilePath = combinedProperties.getProperty("RELEASE_STORE_FILE") ?: ""
            if (storeFilePath.isNotBlank()) {
                val paths = listOf(
                    file(storeFilePath),
                    file("../$storeFilePath"),
                    file("../../$storeFilePath"),
                    file("../../../$storeFilePath"),
                    file("../../../../$storeFilePath"),
                    file("../../../../app/$storeFilePath")
                )
                val keystoreFile = paths.find { it.exists() } ?: file(storeFilePath)
                storeFile = keystoreFile
                storePassword = combinedProperties.getProperty("RELEASE_STORE_PASSWORD") ?: ""
                keyAlias = combinedProperties.getProperty("RELEASE_KEY_ALIAS") ?: ""
                keyPassword = combinedProperties.getProperty("RELEASE_KEY_PASSWORD") ?: ""
            }
        }
    }

    buildTypes {
        release {
            val storeFilePath = combinedProperties.getProperty("RELEASE_STORE_FILE") ?: ""
            if (storeFilePath.isNotBlank()) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }

            // Enable code shrinking and obfuscation for release
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation("com.google.android.play:core:1.10.3")
}

flutter {
    source = "../.."
}
