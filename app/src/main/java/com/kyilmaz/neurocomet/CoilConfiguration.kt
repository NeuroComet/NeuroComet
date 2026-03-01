package com.kyilmaz.neurocomet

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Optimized ImageLoader configuration for NeuroComet.
 *
 * Features:
 * - Memory caching for fast repeated image loads
 * - Disk caching for offline access and reduced network usage
 * - Hardware bitmap support on Android O+ for reduced memory
 * - Crossfade animations disabled in performance mode
 * - Shared OkHttp client for connection pooling
 * - Optimized timeouts for faster failure recovery
 */
object CoilConfiguration {

    private const val MEMORY_CACHE_PERCENTAGE = 0.30 // 30% of app's memory (increased from 25%)
    private const val DISK_CACHE_SIZE = 150L * 1024 * 1024 // 150 MB (increased from 100 MB)

    // Shared OkHttpClient for connection reuse across all image requests
    private val sharedOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Create an optimized ImageLoader for the application.
     * Call this in Application.onCreate() or provide via Coil.setImageLoader()
     */
    fun createImageLoader(
        context: Context,
        isDebug: Boolean = BuildConfig.DEBUG
    ): ImageLoader {
        // Disable crossfade on emulators to reduce animation jank
        val enableCrossfade = !PerformanceOptimizations.isEmulator()

        return ImageLoader.Builder(context)
            // Use shared OkHttp client for connection pooling
            .okHttpClient(sharedOkHttpClient)
            // Memory cache - stores decoded images
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(MEMORY_CACHE_PERCENTAGE)
                    .strongReferencesEnabled(true)
                    .build()
            }
            // Disk cache - stores encoded images
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache"))
                    .maxSizeBytes(DISK_CACHE_SIZE)
                    .build()
            }
            // Use hardware bitmaps for reduced memory (always enabled since minSdk >= 26)
            .allowHardware(true)
            // Allow RGB_565 for opaque images to halve memory usage
            .allowRgb565(true)
            // Disable crossfade on emulators for smoother scrolling
            .crossfade(enableCrossfade)
            // Respect cache headers from server
            .respectCacheHeaders(true)
            // Memory cache policy
            .memoryCachePolicy(CachePolicy.ENABLED)
            // Disk cache policy
            .diskCachePolicy(CachePolicy.ENABLED)
            // Network cache policy
            .networkCachePolicy(CachePolicy.ENABLED)
            // Debug logging in debug builds only
            .apply {
                if (isDebug) {
                    logger(DebugLogger())
                }
            }
            .build()
    }

    /**
     * Create a performance-optimized ImageLoader for list views.
     * Disables crossfade for smoother scrolling.
     */
    fun createListOptimizedImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient(sharedOkHttpClient)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(MEMORY_CACHE_PERCENTAGE)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache"))
                    .maxSizeBytes(DISK_CACHE_SIZE)
                    .build()
            }
            .allowHardware(true)
            .allowRgb565(true)
            .crossfade(false) // Disable for smoother scrolling
            .respectCacheHeaders(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }
}

