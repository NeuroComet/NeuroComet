package com.kyilmaz.neuronetworkingtitle

import android.app.Application

/**
 * Application class used to provide an application Context to ViewModels that need
 * local persistence (e.g., parental controls, DM privacy lists).
 */
class NeuroNetApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationProvider.app = this

        // Touch dev options once so prefs are created; keeps behavior predictable in debug/testing.
        runCatching { DevOptionsSettings.get(this) }
    }
}

/**
 * Small global holder to avoid threading Context through every layer in this demo app.
 *
 * In production, prefer DI (Hilt/Koin) or repositories injected with @ApplicationContext.
 */
object ApplicationProvider {
    @Volatile
    var app: Application? = null
}
