package com.kyilmaz.neuronetworkingtitle

import android.app.Application
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DevOptionsViewModel : ViewModel() {
    private val _options = MutableStateFlow(DevOptions())
    val options: StateFlow<DevOptions> = _options

    fun refresh(app: Application) {
        _options.value = DevOptionsSettings.get(app)
    }

    fun setDevMenuEnabled(app: Application, enabled: Boolean) {
        DevOptionsSettings.setDevMenuEnabled(app, enabled)
        refresh(app)
    }

    fun setShowDmDebugOverlay(app: Application, enabled: Boolean) {
        DevOptionsSettings.setShowDmDebugOverlay(app, enabled)
        refresh(app)
    }

    fun setDmForceSendFailure(app: Application, enabled: Boolean) {
        DevOptionsSettings.setDmForceSendFailure(app, enabled)
        refresh(app)
    }

    fun setDmSendDelayMs(app: Application, delayMs: Long) {
        DevOptionsSettings.setDmSendDelayMs(app, delayMs)
        refresh(app)
    }

    fun setDmDisableRateLimit(app: Application, enabled: Boolean) {
        DevOptionsSettings.setDmDisableRateLimit(app, enabled)
        refresh(app)
    }

    fun setDmMinIntervalOverrideMs(app: Application, overrideMs: Long?) {
        DevOptionsSettings.setDmMinIntervalOverrideMs(app, overrideMs)
        refresh(app)
    }

    fun setModerationOverride(app: Application, override: DevModerationOverride) {
        DevOptionsSettings.setModerationOverride(app, override)
        refresh(app)
    }

    fun setForceAudience(app: Application, audience: Audience?) {
        DevOptionsSettings.setForceAudience(app, audience)
        refresh(app)
    }

    fun setForceKidsFilterLevel(app: Application, level: KidsFilterLevel?) {
        DevOptionsSettings.setForceKidsFilterLevel(app, level)
        refresh(app)
    }

    fun setForcePinSet(app: Application, enabled: Boolean) {
        DevOptionsSettings.setForcePinSet(app, enabled)
        refresh(app)
    }

    fun setForcePinVerifySuccess(app: Application, enabled: Boolean) {
        DevOptionsSettings.setForcePinVerifySuccess(app, enabled)
        refresh(app)
    }

    fun resetAll(app: Application) {
        DevOptionsSettings.resetAll(app)
        refresh(app)
    }
}

