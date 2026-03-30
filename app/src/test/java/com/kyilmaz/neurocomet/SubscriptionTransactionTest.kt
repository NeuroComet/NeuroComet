package com.kyilmaz.neurocomet

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for the transaction status "banking card" feature added in 2.0.0-beta03.
 *
 * The three possible card states are driven entirely by [SubscriptionManager.SubscriptionState]:
 *   • **SUCCESS**  – `purchaseSuccess = true`, `isPremium = true`, `error = null`
 *   • **DECLINED** – `purchaseSuccess = false`, `error != null`
 *   • **TIMED_OUT** – `isLoading = true` persists with neither success nor error
 *
 * These tests verify the state model, its copy-based transitions, and the edge
 * cases that the TransactionStatusCard composable depends on.
 *
 * Note: The tests run against the [SubscriptionManager.SubscriptionState] data class
 * directly (a plain Kotlin class) rather than the singleton, so they execute in a
 * pure JVM environment without Android framework stubs.
 */
class SubscriptionTransactionTest {

    /** Convenience alias */
    private fun state(
        isLoading: Boolean = false,
        isPremium: Boolean = false,
        purchaseSuccess: Boolean = false,
        purchaseType: String? = null,
        error: String? = null
    ) = SubscriptionManager.SubscriptionState(
        isLoading = isLoading,
        isPremium = isPremium,
        purchaseSuccess = purchaseSuccess,
        purchaseType = purchaseType,
        error = error
    )

    // ═══════════════════════════════════════════════════════════════
    // 1. DEFAULT / IDLE STATE
    // ═══════════════════════════════════════════════════════════════

    @Test
    fun defaultState_representsNoTransaction() {
        val s = SubscriptionManager.SubscriptionState()
        assertFalse("Default: not loading", s.isLoading)
        assertFalse("Default: not premium", s.isPremium)
        assertFalse("Default: no purchase success", s.purchaseSuccess)
        assertNull("Default: no error", s.error)
        assertNull("Default: no purchase type", s.purchaseType)
        assertNull("Default: no offerings", s.offerings)
        assertNull("Default: no monthly package", s.monthlyPackage)
        assertNull("Default: no lifetime package", s.lifetimePackage)
    }

    // ═══════════════════════════════════════════════════════════════
    // 2. SUCCESS STATE  –  Card shows: ✅ Payment Successful
    // ═══════════════════════════════════════════════════════════════

    @Test
    fun successState_hasPurchaseSuccessTrue_andPremiumTrue_andNoError() {
        val s = state(isPremium = true, purchaseSuccess = true, purchaseType = "monthly")
        assertTrue("Success: purchaseSuccess must be true", s.purchaseSuccess)
        assertTrue("Success: isPremium must be true", s.isPremium)
        assertFalse("Success: isLoading must be false", s.isLoading)
        assertNull("Success: error must be null", s.error)
        assertEquals("Success: purchaseType recorded", "monthly", s.purchaseType)
    }

    @Test
    fun successState_lifetimePurchaseType_isTrackedCorrectly() {
        val s = state(isPremium = true, purchaseSuccess = true, purchaseType = "lifetime")
        assertTrue(s.purchaseSuccess)
        assertTrue(s.isPremium)
        assertEquals("lifetime", s.purchaseType)
    }

    @Test
    fun successState_restoredPurchaseType_isTrackedCorrectly() {
        val s = state(isPremium = true, purchaseSuccess = true, purchaseType = "restored")
        assertTrue(s.purchaseSuccess)
        assertTrue(s.isPremium)
        assertEquals("restored", s.purchaseType)
    }

    // ═══════════════════════════════════════════════════════════════
    // 3. DECLINED STATE  –  Card shows: ❌ Payment Declined
    // ═══════════════════════════════════════════════════════════════

    @Test
    fun declinedState_hasError_andPurchaseSuccessFalse() {
        val s = state(error = "Card declined by issuer")
        assertFalse("Declined: purchaseSuccess must be false", s.purchaseSuccess)
        assertFalse("Declined: isPremium must be false", s.isPremium)
        assertFalse("Declined: isLoading must be false", s.isLoading)
        assertNotNull("Declined: error must be present", s.error)
        assertEquals("Card declined by issuer", s.error)
    }

    @Test
    fun declinedState_differentErrorMessages_areDistinguishable() {
        val a = state(error = "Insufficient funds")
        val b = state(error = "Network error")
        assertNotEquals(a.error, b.error)
        // Both should be treated as declined by the card
        assertNotNull(a.error)
        assertFalse(a.purchaseSuccess)
        assertNotNull(b.error)
        assertFalse(b.purchaseSuccess)
    }

    @Test
    fun declinedState_billingUnavailable_containsExpectedMessage() {
        val s = state(error = "Purchases are temporarily unavailable. Please try again later.")
        assertFalse(s.purchaseSuccess)
        assertFalse(s.isPremium)
        assertTrue(
            "Error message should mention unavailability",
            s.error!!.contains("unavailable", ignoreCase = true)
        )
    }

    // ═══════════════════════════════════════════════════════════════
    // 4. TIMED-OUT STATE  –  Card shows: ⏱ Transaction Timed Out
    //    Identified by: isLoading = true with no success or error
    // ═══════════════════════════════════════════════════════════════

    @Test
    fun timedOutCondition_isLoadingTrue_withNeitherSuccessNorError() {
        val s = state(isLoading = true)
        assertTrue("Timeout-eligible: isLoading must be true", s.isLoading)
        assertFalse("Timeout-eligible: purchaseSuccess must be false", s.purchaseSuccess)
        assertNull("Timeout-eligible: error must be null", s.error)
        assertFalse("Timeout-eligible: isPremium must be false", s.isPremium)
    }

    @Test
    fun timedOutCondition_isDistinctFromSuccess() {
        val timedOut = state(isLoading = true)
        val success = state(isPremium = true, purchaseSuccess = true)

        assertTrue(timedOut.isLoading)
        assertFalse(timedOut.purchaseSuccess)

        assertFalse(success.isLoading)
        assertTrue(success.purchaseSuccess)
    }

    @Test
    fun timedOutCondition_isDistinctFromDeclined() {
        val timedOut = state(isLoading = true)
        val declined = state(error = "Payment declined")

        assertTrue(timedOut.isLoading)
        assertNull(timedOut.error)

        assertFalse(declined.isLoading)
        assertNotNull(declined.error)
    }

    // ═══════════════════════════════════════════════════════════════
    // 5. STATE COPY TRANSITIONS (simulating helper method behaviour)
    // ═══════════════════════════════════════════════════════════════

    @Test
    fun clearPurchaseSuccess_resetsFlagsViaStateCopy() {
        // Start with a successful purchase
        val before = state(isPremium = true, purchaseSuccess = true, purchaseType = "monthly")
        assertTrue(before.purchaseSuccess)
        assertEquals("monthly", before.purchaseType)

        // This is exactly what SubscriptionManager.clearPurchaseSuccess() does
        val after = before.copy(purchaseSuccess = false, purchaseType = null)
        assertFalse("After clear: purchaseSuccess must be false", after.purchaseSuccess)
        assertNull("After clear: purchaseType must be null", after.purchaseType)
        // Premium status persists (feature was already granted)
        assertTrue("After clear: isPremium should still be true", after.isPremium)
    }

    @Test
    fun clearError_removesErrorMessageViaStateCopy() {
        val before = state(error = "Something failed")
        assertNotNull(before.error)

        // This is exactly what SubscriptionManager.clearError() does
        val after = before.copy(error = null)
        assertNull("After clearError: error must be null", after.error)
    }

    @Test
    fun resetTestPurchase_restoresFullDefaultStateViaStateCopy() {
        val dirtyState = state(
            isLoading = true,
            isPremium = true,
            purchaseSuccess = true,
            purchaseType = "lifetime",
            error = "leftover"
        )
        // This is exactly what SubscriptionManager.resetTestPurchase() does
        val reset = SubscriptionManager.SubscriptionState()
        assertFalse(reset.isLoading)
        assertFalse(reset.isPremium)
        assertFalse(reset.purchaseSuccess)
        assertNull(reset.error)
        assertNull(reset.purchaseType)
        assertNotEquals("Reset state differs from dirty state", dirtyState, reset)
    }

    // ═══════════════════════════════════════════════════════════════
    // 6. EDGE CASES — mutual exclusivity and sequential flows
    // ═══════════════════════════════════════════════════════════════

    @Test
    fun successAndError_canCoexistButIsAnomalous() {
        // The card checks purchaseSuccess first, so error would be shadowed
        val anomaly = state(purchaseSuccess = true, isPremium = true, error = "leftover")
        assertTrue(
            "Anomalous: both success and error set simultaneously",
            anomaly.purchaseSuccess && anomaly.error != null
        )
    }

    @Test
    fun userCancelledPurchase_triggersNoCardState() {
        // RevenueCat sets error = null on user cancel (not a failure)
        val cancelled = state()
        assertFalse("Cancelled: no success", cancelled.purchaseSuccess)
        assertNull("Cancelled: no error", cancelled.error)
        assertFalse("Cancelled: not loading", cancelled.isLoading)
        // All three card-trigger conditions are false → no card rendered
    }

    @Test
    fun consecutivePurchaseAttempts_stateResetsCorrectlyBetweenAttempts() {
        // Attempt 1: declined
        val attempt1 = state(error = "Declined")
        assertNotNull(attempt1.error)
        assertFalse(attempt1.purchaseSuccess)

        // User taps Retry → loading with cleared error
        val retryInFlight = attempt1.copy(isLoading = true, error = null)
        assertTrue("Retry: loading", retryInFlight.isLoading)
        assertNull("Retry: error cleared", retryInFlight.error)

        // Attempt 2: success
        val attempt2 = retryInFlight.copy(
            isLoading = false,
            isPremium = true,
            purchaseSuccess = true,
            purchaseType = "monthly"
        )
        assertTrue("Attempt 2: success", attempt2.purchaseSuccess)
        assertTrue("Attempt 2: premium", attempt2.isPremium)
        assertNull("Attempt 2: no error", attempt2.error)
        assertFalse("Attempt 2: not loading", attempt2.isLoading)
    }

    @Test
    fun loadingToTimeout_thenRetryToSuccess_fullLifecycle() {
        // Phase 1: purchase initiated → loading
        val loading = state(isLoading = true)
        assertTrue(loading.isLoading)
        assertFalse(loading.purchaseSuccess)
        assertNull(loading.error)

        // Phase 2: 30s passes, UI fires timeout → card shows TIMED_OUT
        // (The UI sets purchaseInFlight=false and transactionResult=TIMED_OUT.
        //  The SubscriptionState itself stays isLoading=true until cleared.)
        // We verify the condition the UI checks:
        assertTrue("Still loading (server never responded)", loading.isLoading)
        assertFalse("No success yet", loading.purchaseSuccess)
        assertNull("No error yet", loading.error)

        // Phase 3: user taps Retry → new attempt succeeds
        val retrySuccess = loading.copy(
            isLoading = false,
            isPremium = true,
            purchaseSuccess = true,
            purchaseType = "lifetime"
        )
        assertFalse(retrySuccess.isLoading)
        assertTrue(retrySuccess.purchaseSuccess)
        assertTrue(retrySuccess.isPremium)
        assertEquals("lifetime", retrySuccess.purchaseType)
    }

    @Test
    fun loadingToDeclined_thenRetryTimesOut_fullLifecycle() {
        // Phase 1: purchase initiated → loading
        val loading = state(isLoading = true)

        // Phase 2: server responds with error → DECLINED
        val declined = loading.copy(isLoading = false, error = "Insufficient funds")
        assertFalse(declined.isLoading)
        assertNotNull(declined.error)
        assertFalse(declined.purchaseSuccess)

        // Phase 3: user taps Retry → loading again
        val retry = declined.copy(isLoading = true, error = null)
        assertTrue(retry.isLoading)
        assertNull(retry.error)
        assertFalse(retry.purchaseSuccess)

        // Phase 4: 30s passes with no response → TIMED_OUT condition
        // (retry.isLoading still true, no success, no error)
        assertTrue("Timeout condition: loading", retry.isLoading)
        assertFalse("Timeout condition: no success", retry.purchaseSuccess)
        assertNull("Timeout condition: no error", retry.error)
    }

    // ═══════════════════════════════════════════════════════════════
    // 7. BILLING CONFIGURATION EDGE CASES
    // ═══════════════════════════════════════════════════════════════

    @Test
    fun billingUnavailable_stateReflectsDefaultErrorMessage() {
        val s = state(error = "Purchases are temporarily unavailable. Please try again later.")
        assertFalse(s.isPremium)
        assertFalse(s.purchaseSuccess)
        assertTrue(s.error!!.contains("unavailable"))
    }

    @Test
    fun billingConfigured_noEffect_onExistingPremium() {
        // If someone is already premium, flipping billing configured flag
        // should not reset that (only the manager guards the actual call).
        val premiumState = state(isPremium = true, purchaseSuccess = true, purchaseType = "lifetime")
        assertTrue(premiumState.isPremium)
        // Simulate what happens when billing becomes unconfigured:
        // The state itself doesn't change — the manager controls transitions.
        val unconfigured = premiumState.copy(error = "Billing unavailable")
        assertTrue("Premium flag not touched by error addition", unconfigured.isPremium)
        assertNotNull(unconfigured.error)
    }

    // ═══════════════════════════════════════════════════════════════
    // 8. PURCHASE TYPE TRACKING
    // ═══════════════════════════════════════════════════════════════

    @Test
    fun purchaseType_monthly_lifetime_restored_areAllDistinct() {
        val monthly = state(isPremium = true, purchaseSuccess = true, purchaseType = "monthly")
        val lifetime = state(isPremium = true, purchaseSuccess = true, purchaseType = "lifetime")
        val restored = state(isPremium = true, purchaseSuccess = true, purchaseType = "restored")

        assertNotEquals(monthly.purchaseType, lifetime.purchaseType)
        assertNotEquals(lifetime.purchaseType, restored.purchaseType)
        assertNotEquals(monthly.purchaseType, restored.purchaseType)
    }

    @Test
    fun purchaseType_null_meansNoTransaction() {
        val idle = state()
        assertNull(idle.purchaseType)
        assertFalse(idle.purchaseSuccess)
    }

    // ═══════════════════════════════════════════════════════════════
    // 9. DATA CLASS EQUALITY & COPY SEMANTICS
    // ═══════════════════════════════════════════════════════════════

    @Test
    fun identicalStates_areEqual() {
        val a = state(isPremium = true, purchaseSuccess = true, purchaseType = "monthly")
        val b = state(isPremium = true, purchaseSuccess = true, purchaseType = "monthly")
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun differentStates_areNotEqual() {
        val success = state(isPremium = true, purchaseSuccess = true)
        val declined = state(error = "Declined")
        assertNotEquals(success, declined)
    }

    @Test
    fun copy_onlyChangesSpecifiedFields() {
        val original = state(isPremium = true, purchaseSuccess = true, purchaseType = "monthly")
        val copied = original.copy(purchaseType = "lifetime")
        assertEquals("lifetime", copied.purchaseType)
        assertTrue("copy preserves isPremium", copied.isPremium)
        assertTrue("copy preserves purchaseSuccess", copied.purchaseSuccess)
    }

    // ═══════════════════════════════════════════════════════════════
    // 10. RAPID STATE TRANSITIONS (race-condition modelling)
    // ═══════════════════════════════════════════════════════════════

    @Test
    fun rapidTransitions_loadingToSuccessToReset() {
        val loading = state(isLoading = true)
        val success = loading.copy(isLoading = false, isPremium = true, purchaseSuccess = true, purchaseType = "monthly")
        val reset = SubscriptionManager.SubscriptionState()

        assertTrue(loading.isLoading)
        assertTrue(success.isPremium)
        assertFalse(reset.isPremium)
        assertFalse(reset.isLoading)
    }

    @Test
    fun rapidTransitions_declinedToRetryToDeclinedAgain() {
        val declined1 = state(error = "Card expired")
        val retry = declined1.copy(isLoading = true, error = null)
        val declined2 = retry.copy(isLoading = false, error = "Insufficient funds")

        assertNotNull(declined1.error)
        assertTrue(retry.isLoading)
        assertNull(retry.error)
        assertNotNull(declined2.error)
        assertNotEquals(declined1.error, declined2.error)
    }
}
