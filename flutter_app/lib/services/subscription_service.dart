import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Manages subscription state for NeuroComet.
///
/// Products:
/// - Monthly: $2/month ad-free subscription
/// - Lifetime: $60 one-time purchase for lifetime ad-free
///
/// Entitlement: "premium" — grants ad-free experience
///
/// When Play Store / App Store billing is configured, replace the
/// SharedPreferences stub with actual IAP flow using in_app_purchase.
class SubscriptionService {
  SubscriptionService._();
  static final SubscriptionService instance = SubscriptionService._();

  static const String _tag = 'SubscriptionService';
  static const String _billingUnavailableMessage =
      'Purchases are temporarily unavailable. Please try again later.';

  /// Whether we are in debug / test mode.
  static final bool testMode = kDebugMode;

  // ── State ──────────────────────────────────────────────────────
  final ValueNotifier<SubscriptionState> _stateNotifier =
      ValueNotifier(const SubscriptionState());

  ValueNotifier<SubscriptionState> get stateNotifier => _stateNotifier;
  SubscriptionState get state => _stateNotifier.value;

  /// Simulated premium flag used in test mode.
  bool _isTestPremium = false;

  // ── Public API ─────────────────────────────────────────────────

  /// Fetch offerings (simulated in debug builds).
  Future<void> fetchOfferings() async {
    _update(state.copyWith(isLoading: true, error: null));
    if (testMode) {
      await Future.delayed(const Duration(milliseconds: 400));
      _update(state.copyWith(isLoading: false));
      debugPrint('$_tag 🧪 TEST MODE: Offerings simulated');
      return;
    }
    // TODO: Replace with in_app_purchase offerings fetch
    _update(state.copyWith(isLoading: false));
  }

  /// Check current premium status.
  Future<bool> checkPremiumStatus() async {
    if (testMode) {
      _update(state.copyWith(isPremium: _isTestPremium));
      debugPrint('$_tag 🧪 TEST MODE: Premium = $_isTestPremium');
      return _isTestPremium;
    }
    // TODO: Replace with in_app_purchase entitlement check
    final prefs = await SharedPreferences.getInstance();
    final isPremium = prefs.getBool('is_premium') ?? false;
    _update(state.copyWith(isPremium: isPremium));
    return isPremium;
  }

  /// Purchase the monthly subscription.
  Future<void> purchaseMonthly({
    VoidCallback? onSuccess,
    ValueChanged<String>? onError,
  }) async {
    if (testMode) {
      await _simulateTestPurchase('monthly', onSuccess);
      return;
    }
    await _purchasePackage('monthly', onSuccess: onSuccess, onError: onError);
  }

  /// Purchase the lifetime subscription.
  Future<void> purchaseLifetime({
    VoidCallback? onSuccess,
    ValueChanged<String>? onError,
  }) async {
    if (testMode) {
      await _simulateTestPurchase('lifetime', onSuccess);
      return;
    }
    await _purchasePackage('lifetime', onSuccess: onSuccess, onError: onError);
  }

  /// Restore purchases.
  Future<void> restorePurchases({
    ValueChanged<bool>? onSuccess,
    ValueChanged<String>? onError,
  }) async {
    _update(state.copyWith(isLoading: true, error: null));
    if (testMode) {
      await Future.delayed(const Duration(milliseconds: 800));
      _update(state.copyWith(
        isLoading: false,
        isPremium: _isTestPremium,
        purchaseSuccess: _isTestPremium,
        purchaseType: _isTestPremium ? 'restored' : null,
      ));
      debugPrint('$_tag 🧪 TEST MODE: Restore — premium = $_isTestPremium');
      onSuccess?.call(_isTestPremium);
      return;
    }
    // TODO: Replace with in_app_purchase restore
    final prefs = await SharedPreferences.getInstance();
    final isPremium = prefs.getBool('is_premium') ?? false;
    _update(state.copyWith(
      isLoading: false,
      isPremium: isPremium,
      purchaseSuccess: isPremium,
      purchaseType: isPremium ? 'restored' : null,
    ));
    onSuccess?.call(isPremium);
  }

  /// Clear the [purchaseSuccess] flag (call after the UI has shown the result).
  void clearPurchaseSuccess() {
    _update(state.copyWith(purchaseSuccess: false, purchaseType: null));
  }

  /// Clear the error message.
  void clearError() {
    _update(state.copyWith(error: null));
  }

  /// Reset simulated purchase (test mode only).
  void resetTestPurchase() {
    if (!testMode) return;
    _isTestPremium = false;
    _stateNotifier.value = const SubscriptionState();
    debugPrint('$_tag 🧪 TEST MODE: Premium status reset to FREE');
  }

  // ── Test-only helpers for triggering each transaction card state ──

  /// Simulate a successful purchase (test mode only).
  Future<void> simulateTestSuccess() async {
    if (!testMode) return;
    _update(state.copyWith(isLoading: true, error: null));
    await Future.delayed(const Duration(milliseconds: 600));
    _isTestPremium = true;
    _update(state.copyWith(
      isLoading: false,
      isPremium: true,
      purchaseSuccess: true,
      purchaseType: 'monthly',
    ));
    debugPrint('$_tag 🧪 TEST: Simulated SUCCESS');
  }

  /// Simulate a declined purchase (test mode only).
  Future<void> simulateTestDeclined() async {
    if (!testMode) return;
    _update(state.copyWith(isLoading: true, error: null));
    await Future.delayed(const Duration(milliseconds: 600));
    _update(state.copyWith(
      isLoading: false,
      error: 'Payment declined by card issuer.',
    ));
    debugPrint('$_tag 🧪 TEST: Simulated DECLINED');
  }

  /// Simulate a timed-out purchase (test mode only).
  Future<void> simulateTestTimedOut() async {
    if (!testMode) return;
    _update(state.copyWith(isLoading: true, error: null));
    // Short delay to show loading, then just stop — the screen's own
    // timeout timer handles the timed-out state.
    await Future.delayed(const Duration(milliseconds: 600));
    // We don't set success or error — we leave the request "in flight"
    // so the timeout timer on the screen fires.
    debugPrint('$_tag 🧪 TEST: Simulated TIMED_OUT (no response)');
  }

  // ── Private helpers ────────────────────────────────────────────

  void _update(SubscriptionState next) {
    _stateNotifier.value = next;
  }

  Future<void> _purchasePackage(
    String purchaseType, {
    VoidCallback? onSuccess,
    ValueChanged<String>? onError,
  }) async {
    _update(state.copyWith(isLoading: true, error: null));
    try {
      // TODO: Replace with actual in_app_purchase flow
      final prefs = await SharedPreferences.getInstance();
      await prefs.setBool('is_premium', true);
      await prefs.setString(
          'subscription_date', DateTime.now().toIso8601String());

      _update(state.copyWith(
        isLoading: false,
        isPremium: true,
        purchaseSuccess: true,
        purchaseType: purchaseType,
      ));
      debugPrint('$_tag Purchase successful! Type: $purchaseType');
      onSuccess?.call();
    } catch (e) {
      _update(state.copyWith(
        isLoading: false,
        error: e.toString(),
      ));
      debugPrint('$_tag Purchase error: $e');
      onError?.call(e.toString());
    }
  }

  Future<void> _simulateTestPurchase(
      String purchaseType, VoidCallback? onSuccess) async {
    _update(state.copyWith(isLoading: true, error: null));
    await Future.delayed(const Duration(milliseconds: 1200));
    _isTestPremium = true;
    _update(state.copyWith(
      isLoading: false,
      isPremium: true,
      purchaseSuccess: true,
      purchaseType: purchaseType,
    ));
    debugPrint('$_tag 🧪 TEST MODE: Purchase simulated — type = $purchaseType');
    onSuccess?.call();
  }
}

/// Immutable subscription state — mirrors the Android `SubscriptionManager.SubscriptionState`.
class SubscriptionState {
  final bool isLoading;
  final bool isPremium;
  final bool purchaseSuccess;
  final String? purchaseType; // "monthly", "lifetime", or "restored"
  final String? error;

  // Offerings placeholders (will be populated by IAP)
  final dynamic offerings;
  final dynamic monthlyPackage;
  final dynamic lifetimePackage;

  const SubscriptionState({
    this.isLoading = false,
    this.isPremium = false,
    this.purchaseSuccess = false,
    this.purchaseType,
    this.error,
    this.offerings,
    this.monthlyPackage,
    this.lifetimePackage,
  });

  SubscriptionState copyWith({
    bool? isLoading,
    bool? isPremium,
    bool? purchaseSuccess,
    String? purchaseType,
    String? error,
    dynamic offerings,
    dynamic monthlyPackage,
    dynamic lifetimePackage,
  }) {
    return SubscriptionState(
      isLoading: isLoading ?? this.isLoading,
      isPremium: isPremium ?? this.isPremium,
      purchaseSuccess: purchaseSuccess ?? this.purchaseSuccess,
      purchaseType: purchaseType ?? this.purchaseType,
      error: error ?? this.error,
      offerings: offerings ?? this.offerings,
      monthlyPackage: monthlyPackage ?? this.monthlyPackage,
      lifetimePackage: lifetimePackage ?? this.lifetimePackage,
    );
  }

  /// Returns a copy with specific nullable fields explicitly cleared.
  SubscriptionState copyWithClear({
    bool clearPurchaseType = false,
    bool clearError = false,
  }) {
    return SubscriptionState(
      isLoading: isLoading,
      isPremium: isPremium,
      purchaseSuccess: purchaseSuccess,
      purchaseType: clearPurchaseType ? null : purchaseType,
      error: clearError ? null : error,
      offerings: offerings,
      monthlyPackage: monthlyPackage,
      lifetimePackage: lifetimePackage,
    );
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is SubscriptionState &&
          runtimeType == other.runtimeType &&
          isLoading == other.isLoading &&
          isPremium == other.isPremium &&
          purchaseSuccess == other.purchaseSuccess &&
          purchaseType == other.purchaseType &&
          error == other.error &&
          offerings == other.offerings &&
          monthlyPackage == other.monthlyPackage &&
          lifetimePackage == other.lifetimePackage;

  @override
  int get hashCode =>
      isLoading.hashCode ^
      isPremium.hashCode ^
      purchaseSuccess.hashCode ^
      purchaseType.hashCode ^
      error.hashCode ^
      offerings.hashCode ^
      monthlyPackage.hashCode ^
      lifetimePackage.hashCode;

  @override
  String toString() =>
      'SubscriptionState(isLoading: $isLoading, isPremium: $isPremium, '
      'purchaseSuccess: $purchaseSuccess, purchaseType: $purchaseType, '
      'error: $error)';
}

