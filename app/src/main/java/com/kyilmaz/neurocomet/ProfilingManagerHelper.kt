package com.kyilmaz.neurocomet

import android.content.Context
import android.os.Build
import android.util.Log
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Helper for Android 17 (API 37+) ProfilingManager system triggers.
 * Uses reflection to avoid compile-time dependency on preview SDK.
 */
object ProfilingManagerHelper {
    private const val TAG = "ProfilingManagerHelper"
    
    private val profilingExecutor: Executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    /**
     * Register system triggers for profiling on Android 17+.
     * Includes COLD_START, OOM, and KILL_EXCESSIVE_CPU_USAGE.
     */
    fun registerTracerTriggers(context: Context) {
        if (Build.VERSION.SDK_INT >= 37) {
            try {
                val pmClass = Class.forName("android.os.ProfilingManager")
                val resultClass = Class.forName("android.os.ProfilingResult")
                val triggerClass = Class.forName("android.os.ProfilingTrigger")
                val builderClass = Class.forName("android.os.ProfilingTrigger\$Builder")

                val profilingManager = context.getSystemService(pmClass) ?: return

                // Register for all profiling results
                val registerMethod = pmClass.getMethod(
                    "registerForAllProfilingResults",
                    Executor::class.java,
                    java.util.function.Consumer::class.java
                )
                val errorNone = resultClass.getField("ERROR_NONE").getInt(null)
                val resultCallback = java.util.function.Consumer<Any> { result ->
                    val errorCode = resultClass.getMethod("getErrorCode").invoke(result) as Int
                    if (errorCode == errorNone) {
                        val path = resultClass.getMethod("getResultFilePath").invoke(result)
                        Log.d(TAG, "Profiling captured: $path")
                    } else {
                        val msg = resultClass.getMethod("getErrorMessage").invoke(result)
                        Log.e(TAG, "Profiling failed: $errorCode - $msg")
                    }
                }
                registerMethod.invoke(profilingManager, profilingExecutor, resultCallback)

                // Build triggers via reflection
                val triggers = mutableListOf<Any>()
                fun addTrigger(typeFieldName: String, rateHours: Int) {
                    val type = triggerClass.getField(typeFieldName).getInt(null)
                    val builder = builderClass.getConstructor(Int::class.javaPrimitiveType).newInstance(type)
                    builderClass.getMethod("setRateLimitingPeriodHours", Int::class.javaPrimitiveType)
                        .invoke(builder, rateHours)
                    triggers.add(builderClass.getMethod("build").invoke(builder)!!)
                }

                addTrigger("TRIGGER_TYPE_COLD_START", 24)
                addTrigger("TRIGGER_TYPE_OOM", 1)
                addTrigger("TRIGGER_TYPE_KILL_EXCESSIVE_CPU_USAGE", 1)

                pmClass.getMethod("addProfilingTriggers", List::class.java)
                    .invoke(profilingManager, triggers)
                Log.d(TAG, "ProfilingManager triggers registered for system events")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to register ProfilingManager triggers", e)
            }
        }
    }
}
