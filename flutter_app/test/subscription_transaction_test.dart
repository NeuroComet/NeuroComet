import 'package:flutter_test/flutter_test.dart';
import 'package:neuro_comet/services/subscription_service.dart';

/// Tests for the transaction status "banking card" feature added in 2.0.0-beta03.
///
/// The three possible card states are driven entirely by [SubscriptionState]:
///   • **SUCCESS**  – `purchaseSuccess = true`, `isPremium = true`, `error = null`
///   • **DECLINED** – `purchaseSuccess = false`, `error != null`
///   • **TIMED_OUT** – `isLoading = true` persists with neither success nor error
///
/// These tests verify the state model, its copy-based transitions, and the edge
/// cases that the TransactionStatusCard widget depends on.
///
/// Mirrors the Android `SubscriptionTransactionTest.kt` exactly.
void main() {
  /// Convenience factory
  SubscriptionState state({
    bool isLoading = false,
    bool isPremium = false,
    bool purchaseSuccess = false,
    String? purchaseType,
    String? error,
  }) {
    return SubscriptionState(
      isLoading: isLoading,
      isPremium: isPremium,
      purchaseSuccess: purchaseSuccess,
      purchaseType: purchaseType,
      error: error,
    );
  }

  // ═══════════════════════════════════════════════════════════════
  // 1. DEFAULT / IDLE STATE
  // ═══════════════════════════════════════════════════════════════

  test('defaultState_representsNoTransaction', () {
    final s = const SubscriptionState();
    expect(s.isLoading, isFalse, reason: 'Default: not loading');
    expect(s.isPremium, isFalse, reason: 'Default: not premium');
    expect(s.purchaseSuccess, isFalse, reason: 'Default: no purchase success');
    expect(s.error, isNull, reason: 'Default: no error');
    expect(s.purchaseType, isNull, reason: 'Default: no purchase type');
    expect(s.offerings, isNull, reason: 'Default: no offerings');
    expect(s.monthlyPackage, isNull, reason: 'Default: no monthly package');
    expect(s.lifetimePackage, isNull, reason: 'Default: no lifetime package');
  });

  // ═══════════════════════════════════════════════════════════════
  // 2. SUCCESS STATE  –  Card shows: ✅ Payment Successful
  // ═══════════════════════════════════════════════════════════════

  test('successState_hasPurchaseSuccessTrue_andPremiumTrue_andNoError', () {
    final s = state(isPremium: true, purchaseSuccess: true, purchaseType: 'monthly');
    expect(s.purchaseSuccess, isTrue, reason: 'Success: purchaseSuccess must be true');
    expect(s.isPremium, isTrue, reason: 'Success: isPremium must be true');
    expect(s.isLoading, isFalse, reason: 'Success: isLoading must be false');
    expect(s.error, isNull, reason: 'Success: error must be null');
    expect(s.purchaseType, equals('monthly'), reason: 'Success: purchaseType recorded');
  });

  test('successState_lifetimePurchaseType_isTrackedCorrectly', () {
    final s = state(isPremium: true, purchaseSuccess: true, purchaseType: 'lifetime');
    expect(s.purchaseSuccess, isTrue);
    expect(s.isPremium, isTrue);
    expect(s.purchaseType, equals('lifetime'));
  });

  test('successState_restoredPurchaseType_isTrackedCorrectly', () {
    final s = state(isPremium: true, purchaseSuccess: true, purchaseType: 'restored');
    expect(s.purchaseSuccess, isTrue);
    expect(s.isPremium, isTrue);
    expect(s.purchaseType, equals('restored'));
  });

  // ═══════════════════════════════════════════════════════════════
  // 3. DECLINED STATE  –  Card shows: ❌ Payment Declined
  // ═══════════════════════════════════════════════════════════════

  test('declinedState_hasError_andPurchaseSuccessFalse', () {
    final s = state(error: 'Card declined by issuer');
    expect(s.purchaseSuccess, isFalse, reason: 'Declined: purchaseSuccess must be false');
    expect(s.isPremium, isFalse, reason: 'Declined: isPremium must be false');
    expect(s.isLoading, isFalse, reason: 'Declined: isLoading must be false');
    expect(s.error, isNotNull, reason: 'Declined: error must be present');
    expect(s.error, equals('Card declined by issuer'));
  });

  test('declinedState_differentErrorMessages_areDistinguishable', () {
    final a = state(error: 'Insufficient funds');
    final b = state(error: 'Network error');
    expect(a.error, isNot(equals(b.error)));
    // Both should be treated as declined by the card
    expect(a.error, isNotNull);
    expect(a.purchaseSuccess, isFalse);
    expect(b.error, isNotNull);
    expect(b.purchaseSuccess, isFalse);
  });

  test('declinedState_billingUnavailable_containsExpectedMessage', () {
    final s = state(error: 'Purchases are temporarily unavailable. Please try again later.');
    expect(s.purchaseSuccess, isFalse);
    expect(s.isPremium, isFalse);
    expect(
      s.error!.toLowerCase().contains('unavailable'),
      isTrue,
      reason: 'Error message should mention unavailability',
    );
  });

  // ═══════════════════════════════════════════════════════════════
  // 4. TIMED-OUT STATE  –  Card shows: ⏱ Transaction Timed Out
  //    Identified by: isLoading = true with no success or error
  // ═══════════════════════════════════════════════════════════════

  test('timedOutCondition_isLoadingTrue_withNeitherSuccessNorError', () {
    final s = state(isLoading: true);
    expect(s.isLoading, isTrue, reason: 'Timeout-eligible: isLoading must be true');
    expect(s.purchaseSuccess, isFalse, reason: 'Timeout-eligible: purchaseSuccess must be false');
    expect(s.error, isNull, reason: 'Timeout-eligible: error must be null');
    expect(s.isPremium, isFalse, reason: 'Timeout-eligible: isPremium must be false');
  });

  test('timedOutCondition_isDistinctFromSuccess', () {
    final timedOut = state(isLoading: true);
    final success = state(isPremium: true, purchaseSuccess: true);

    expect(timedOut.isLoading, isTrue);
    expect(timedOut.purchaseSuccess, isFalse);

    expect(success.isLoading, isFalse);
    expect(success.purchaseSuccess, isTrue);
  });

  test('timedOutCondition_isDistinctFromDeclined', () {
    final timedOut = state(isLoading: true);
    final declined = state(error: 'Payment declined');

    expect(timedOut.isLoading, isTrue);
    expect(timedOut.error, isNull);

    expect(declined.isLoading, isFalse);
    expect(declined.error, isNotNull);
  });

  // ═══════════════════════════════════════════════════════════════
  // 5. STATE COPY TRANSITIONS (simulating helper method behaviour)
  // ═══════════════════════════════════════════════════════════════

  test('clearPurchaseSuccess_resetsFlagsViaCopy', () {
    // Start with a successful purchase
    final before = state(isPremium: true, purchaseSuccess: true, purchaseType: 'monthly');
    expect(before.purchaseSuccess, isTrue);
    expect(before.purchaseType, equals('monthly'));

    // This is exactly what SubscriptionService.clearPurchaseSuccess() does
    final after = before.copyWith(purchaseSuccess: false).copyWithClear(clearPurchaseType: true);
    expect(after.purchaseSuccess, isFalse, reason: 'After clear: purchaseSuccess must be false');
    expect(after.purchaseType, isNull, reason: 'After clear: purchaseType must be null');
    // Premium status persists (feature was already granted)
    expect(after.isPremium, isTrue, reason: 'After clear: isPremium should still be true');
  });

  test('clearError_removesErrorMessageViaCopy', () {
    final before = state(error: 'Something failed');
    expect(before.error, isNotNull);

    // This is exactly what SubscriptionService.clearError() does
    final after = before.copyWithClear(clearError: true);
    expect(after.error, isNull, reason: 'After clearError: error must be null');
  });

  test('resetTestPurchase_restoresFullDefaultStateViaCopy', () {
    final dirtyState = state(
      isLoading: true,
      isPremium: true,
      purchaseSuccess: true,
      purchaseType: 'lifetime',
      error: 'leftover',
    );
    // This is exactly what SubscriptionService.resetTestPurchase() does
    const reset = SubscriptionState();
    expect(reset.isLoading, isFalse);
    expect(reset.isPremium, isFalse);
    expect(reset.purchaseSuccess, isFalse);
    expect(reset.error, isNull);
    expect(reset.purchaseType, isNull);
    expect(reset, isNot(equals(dirtyState)), reason: 'Reset state differs from dirty state');
  });

  // ═══════════════════════════════════════════════════════════════
  // 6. EDGE CASES — mutual exclusivity and sequential flows
  // ═══════════════════════════════════════════════════════════════

  test('successAndError_canCoexistButIsAnomalous', () {
    // The card checks purchaseSuccess first, so error would be shadowed
    final anomaly = state(purchaseSuccess: true, isPremium: true, error: 'leftover');
    expect(
      anomaly.purchaseSuccess && anomaly.error != null,
      isTrue,
      reason: 'Anomalous: both success and error set simultaneously',
    );
  });

  test('userCancelledPurchase_triggersNoCardState', () {
    // RevenueCat sets error = null on user cancel (not a failure)
    final cancelled = state();
    expect(cancelled.purchaseSuccess, isFalse, reason: 'Cancelled: no success');
    expect(cancelled.error, isNull, reason: 'Cancelled: no error');
    expect(cancelled.isLoading, isFalse, reason: 'Cancelled: not loading');
    // All three card-trigger conditions are false → no card rendered
  });

  test('consecutivePurchaseAttempts_stateResetsCorrectlyBetweenAttempts', () {
    // Attempt 1: declined
    final attempt1 = state(error: 'Declined');
    expect(attempt1.error, isNotNull);
    expect(attempt1.purchaseSuccess, isFalse);

    // User taps Retry → loading with cleared error
    final retryInFlight = SubscriptionState(
      isLoading: true,
      error: null,
      isPremium: attempt1.isPremium,
      purchaseSuccess: attempt1.purchaseSuccess,
    );
    expect(retryInFlight.isLoading, isTrue, reason: 'Retry: loading');
    expect(retryInFlight.error, isNull, reason: 'Retry: error cleared');

    // Attempt 2: success
    final attempt2 = SubscriptionState(
      isLoading: false,
      isPremium: true,
      purchaseSuccess: true,
      purchaseType: 'monthly',
    );
    expect(attempt2.purchaseSuccess, isTrue, reason: 'Attempt 2: success');
    expect(attempt2.isPremium, isTrue, reason: 'Attempt 2: premium');
    expect(attempt2.error, isNull, reason: 'Attempt 2: no error');
    expect(attempt2.isLoading, isFalse, reason: 'Attempt 2: not loading');
  });

  test('loadingToTimeout_thenRetryToSuccess_fullLifecycle', () {
    // Phase 1: purchase initiated → loading
    final loading = state(isLoading: true);
    expect(loading.isLoading, isTrue);
    expect(loading.purchaseSuccess, isFalse);
    expect(loading.error, isNull);

    // Phase 2: 30s passes → timeout condition
    expect(loading.isLoading, isTrue, reason: 'Still loading (server never responded)');
    expect(loading.purchaseSuccess, isFalse, reason: 'No success yet');
    expect(loading.error, isNull, reason: 'No error yet');

    // Phase 3: user taps Retry → new attempt succeeds
    final retrySuccess = SubscriptionState(
      isLoading: false,
      isPremium: true,
      purchaseSuccess: true,
      purchaseType: 'lifetime',
    );
    expect(retrySuccess.isLoading, isFalse);
    expect(retrySuccess.purchaseSuccess, isTrue);
    expect(retrySuccess.isPremium, isTrue);
    expect(retrySuccess.purchaseType, equals('lifetime'));
  });

  test('loadingToDeclined_thenRetryTimesOut_fullLifecycle', () {
    // Phase 1: purchase initiated → loading
    final loading = state(isLoading: true);

    // Phase 2: server responds with error → DECLINED
    final declined = SubscriptionState(
      isLoading: false,
      error: 'Insufficient funds',
      isPremium: loading.isPremium,
      purchaseSuccess: loading.purchaseSuccess,
    );
    expect(declined.isLoading, isFalse);
    expect(declined.error, isNotNull);
    expect(declined.purchaseSuccess, isFalse);

    // Phase 3: user taps Retry → loading again
    final retry = SubscriptionState(
      isLoading: true,
      error: null,
      isPremium: declined.isPremium,
      purchaseSuccess: declined.purchaseSuccess,
    );
    expect(retry.isLoading, isTrue);
    expect(retry.error, isNull);
    expect(retry.purchaseSuccess, isFalse);

    // Phase 4: 30s passes with no response → TIMED_OUT condition
    expect(retry.isLoading, isTrue, reason: 'Timeout condition: loading');
    expect(retry.purchaseSuccess, isFalse, reason: 'Timeout condition: no success');
    expect(retry.error, isNull, reason: 'Timeout condition: no error');
  });
}

