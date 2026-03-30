package com.kyilmaz.neurocomet

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Explicit imports for symbols needed from other files in the package
import com.kyilmaz.neurocomet.DevOptionsSettings

// --- Data Models (Consolidated from what was likely in SafetyModels.kt) ---

/**
 * App-level safety audience.
 */
enum class Audience {
    UNDER_13,
    TEEN,
    ADULT
}

/**
 * Controls the strength of filtering in UNDER_13 mode.
 */
enum class KidsFilterLevel {
    STRICT, // hide/sanitize more aggressively
    MODERATE
}

data class SafetyState(
    val audience: Audience = Audience.ADULT,
    val kidsFilterLevel: KidsFilterLevel = KidsFilterLevel.STRICT,
    val isParentalPinSet: Boolean = false
) {
    val isKidsMode: Boolean get() = audience == Audience.UNDER_13
}

// --- Audience Persistence ---

/**
 * Persists the verified audience to SharedPreferences so the age gate
 * survives app restarts.  A missing value means the user has never
 * completed age verification.
 */
object AudiencePrefs {
    private const val PREFS = "audience_prefs"
    private const val KEY_AUDIENCE = "verified_audience"

    fun save(context: Context, audience: Audience) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_AUDIENCE, audience.name)
            .apply()
    }

    fun load(context: Context): Audience? {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_AUDIENCE, null) ?: return null
        return runCatching { Audience.valueOf(raw) }.getOrNull()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_AUDIENCE)
            .apply()
    }

    /** Returns true when the user has never completed age verification. */
    fun needsVerification(context: Context): Boolean = load(context) == null
}

// --- ViewModel Implementation ---

class SafetyViewModel : ViewModel() {
    private val _state = MutableStateFlow(SafetyState())
    val state: StateFlow<SafetyState> = _state.asStateFlow()

    fun refresh(application: Application) {
        viewModelScope.launch {
            val devOptions = DevOptionsSettings.get(application)
            val parentalState = ParentalControlsSettings.getState(application)
            val persistedAudience = AudiencePrefs.load(application)

            _state.update { currentState ->
                currentState.copy(
                    // Priority: dev override > persisted audience > current in-memory value
                    audience = devOptions.forceAudience
                        ?: persistedAudience
                        ?: currentState.audience,
                    kidsFilterLevel = devOptions.forceKidsFilterLevel ?: currentState.kidsFilterLevel,
                    isParentalPinSet = devOptions.forcePinSet || parentalState.isPinSet
                )
            }
        }
    }

    // Mutator function used by SettingsScreen
    fun setAudience(audience: Audience, application: Application) {
        AudiencePrefs.save(application, audience)
        _state.update { it.copy(
            audience = audience,
            kidsFilterLevel = if (audience == Audience.UNDER_13) KidsFilterLevel.STRICT else KidsFilterLevel.MODERATE)
        }
    }

    /**
     * Set audience directly — used during sign-up / sign-in age verification.
     * Persists the choice so it survives app restarts.
     */
    fun setAudienceDirect(audience: Audience, context: Context) {
        AudiencePrefs.save(context, audience)
        _state.update { it.copy(audience = audience, kidsFilterLevel = if (audience == Audience.UNDER_13) KidsFilterLevel.STRICT else KidsFilterLevel.MODERATE) }
    }

    /**
     * Internal setter that skips persistence — only used by dev-options force overrides.
     */
    internal fun setAudienceTransient(audience: Audience) {
        _state.update { it.copy(audience = audience, kidsFilterLevel = if (audience == Audience.UNDER_13) KidsFilterLevel.STRICT else KidsFilterLevel.MODERATE) }
    }
}
