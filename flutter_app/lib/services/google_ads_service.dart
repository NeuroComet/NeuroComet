import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import '../core/config/admob_env.dart';
import 'subscription_service.dart';

class GoogleAdsService {
  static final GoogleAdsService _instance = GoogleAdsService._internal();
  factory GoogleAdsService() => _instance;
  GoogleAdsService._internal();

  bool _initialized = false;
  bool _isLoading = false;
  bool _adsEnabled = true;
  bool _isPremium = false;

  // Dev overrides
  bool _forceShowAds = false;
  bool _simulateAdFailure = false;

  // Ad instances
  InterstitialAd? _interstitialAd;
  RewardedAd? _rewardedAd;

  // Ad status for UI
  bool get isInitialized => _initialized;
  bool get isLoading => _isLoading;
  bool get adsEnabled => _adsEnabled || _forceShowAds;
  bool get isPremium => _isPremium;
  bool get interstitialLoaded => _interstitialAd != null;
  bool get rewardedLoaded => _rewardedAd != null;

  // Test Unit IDs
  static String get bannerAdUnitId {
    if (Platform.isAndroid) {
      return AdMobEnv.bannerIdAndroid;
    } else if (Platform.isIOS) {
      return AdMobEnv.bannerIdIOS;
    }
    return '';
  }

  static String get interstitialAdUnitId {
    if (Platform.isAndroid) {
      return AdMobEnv.interstitialIdAndroid;
    } else if (Platform.isIOS) {
      return AdMobEnv.interstitialIdIOS;
    }
    return '';
  }

  static String get rewardedAdUnitId {
    if (Platform.isAndroid) {
      return AdMobEnv.rewardedIdAndroid;
    } else if (Platform.isIOS) {
      return AdMobEnv.rewardedIdIOS;
    }
    return '';
  }

  Future<void> initialize() async {
    if (_initialized || _isLoading) return;

    _isLoading = true;
    try {
      await MobileAds.instance.initialize();
      _initialized = true;
      debugPrint('Google Mobile Ads SDK initialized');

      // Sync with premium status
      final subService = SubscriptionService.instance;
      _isPremium = subService.state.isPremium;
      _adsEnabled = !_isPremium;

      subService.stateNotifier.addListener(() {
        _isPremium = subService.state.isPremium;
        _adsEnabled = !_isPremium;
        if (_isPremium && !_forceShowAds) {
          _clearAllAds();
        }
      });

      if (adsEnabled) {
        _preloadAds();
      }
    } catch (e) {
      debugPrint('Failed to initialize ads: $e');
    } finally {
      _isLoading = false;
    }
  }

  void updateDevOptions({
    bool? forceShowAds,
    bool? simulateAdFailure,
    bool? useTestAds,
    bool? isAdsPremium,
  }) {
    if (forceShowAds != null) _forceShowAds = forceShowAds;
    if (simulateAdFailure != null) _simulateAdFailure = simulateAdFailure;
    if (isAdsPremium != null) {
       _isPremium = isAdsPremium;
       _adsEnabled = !_isPremium;
    }

    if (!adsEnabled) {
      _clearAllAds();
    } else {
      _preloadAds();
    }
  }

  void _preloadAds() {
    if (!adsEnabled || _simulateAdFailure) return;
    loadInterstitialAd();
    loadRewardedAd();
  }

  void _clearAllAds() {
    _interstitialAd?.dispose();
    _interstitialAd = null;
    _rewardedAd?.dispose();
    _rewardedAd = null;
  }

  void loadInterstitialAd() {
    if (!adsEnabled || _simulateAdFailure) return;
    if (_interstitialAd != null) return;

    InterstitialAd.load(
      adUnitId: interstitialAdUnitId,
      request: const AdRequest(),
      adLoadCallback: InterstitialAdLoadCallback(
        onAdLoaded: (ad) {
          _interstitialAd = ad;
          ad.fullScreenContentCallback = FullScreenContentCallback(
            onAdDismissedFullScreenContent: (ad) {
              ad.dispose();
              _interstitialAd = null;
              loadInterstitialAd();
            },
            onAdFailedToShowFullScreenContent: (ad, error) {
              ad.dispose();
              _interstitialAd = null;
              loadInterstitialAd();
            },
          );
        },
        onAdFailedToLoad: (error) {
          debugPrint('InterstitialAd failed to load: $error');
          _interstitialAd = null;
        },
      ),
    );
  }

  void showInterstitialAd() {
    if (_interstitialAd == null) {
      loadInterstitialAd();
      return;
    }
    _interstitialAd!.show();
  }

  void loadRewardedAd() {
    if (!adsEnabled || _simulateAdFailure) return;
    if (_rewardedAd != null) return;

    RewardedAd.load(
      adUnitId: rewardedAdUnitId,
      request: const AdRequest(),
      rewardedAdLoadCallback: RewardedAdLoadCallback(
        onAdLoaded: (ad) {
          _rewardedAd = ad;
          ad.fullScreenContentCallback = FullScreenContentCallback(
            onAdDismissedFullScreenContent: (ad) {
              ad.dispose();
              _rewardedAd = null;
              loadRewardedAd();
            },
            onAdFailedToShowFullScreenContent: (ad, error) {
              ad.dispose();
              _rewardedAd = null;
              loadRewardedAd();
            },
          );
        },
        onAdFailedToLoad: (error) {
          debugPrint('RewardedAd failed to load: $error');
          _rewardedAd = null;
        },
      ),
    );
  }

  void showRewardedAd({required OnUserEarnedRewardCallback onUserEarnedReward}) {
    if (_rewardedAd == null) {
      loadRewardedAd();
      return;
    }
    _rewardedAd!.show(onUserEarnedReward: onUserEarnedReward);
  }

  void forceLoadAllAds() {
    loadInterstitialAd();
    loadRewardedAd();
  }
}
