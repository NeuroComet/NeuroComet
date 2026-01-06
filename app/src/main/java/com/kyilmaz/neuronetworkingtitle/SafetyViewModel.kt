@file:Suppress("unused", "UNUSED", "Unused")

package com.kyilmaz.neuronetworkingtitle

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds the current safety settings (kids mode + parental controls) in memory.
 *
 * Note: This is a pure in-memory ViewModel. Persistence is handled by [SafetySettings]
 * from call sites that have a Context/Application.
 */
class SafetyViewModel : ViewModel() {

    private val _state = MutableStateFlow(SafetyState())
    val state: StateFlow<SafetyState> = _state

    /** Refresh state from persistence. */
    fun refresh(app: android.app.Application) {
        val base = SafetySettings.getSafetyState(app)
        val dev = DevOptionsSettings.get(app)

        val audience = dev.forceAudience ?: base.audience
        val kidsLevel = dev.forceKidsFilterLevel ?: base.kidsFilterLevel
        val pinSet = if (dev.forcePinSet) true else base.isParentalPinSet

        _state.value = base.copy(audience = audience, kidsFilterLevel = kidsLevel, isParentalPinSet = pinSet)
    }

    fun setAudience(app: android.app.Application, audience: Audience) {
        SafetySettings.setAudience(app, audience)
        refresh(app)
    }

    fun setKidsFilterLevel(app: android.app.Application, level: KidsFilterLevel) {
        SafetySettings.setKidsFilterLevel(app, level)
        refresh(app)
    }

    fun setPin(app: android.app.Application, pin: String) {
        SafetySettings.setPin(app, pin)
        refresh(app)
    }

    fun clearPin(app: android.app.Application) {
        SafetySettings.clearPin(app)
        refresh(app)
    }

    fun verifyPin(app: android.app.Application, pinAttempt: String): Boolean {
        val dev = DevOptionsSettings.get(app)
        if (dev.forcePinVerifySuccess) return true
        return SafetySettings.verifyPin(app, pinAttempt)
    }
}
