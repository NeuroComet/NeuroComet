import '../utils/security_utils.dart';

/// Obfuscated AdMob credentials that match the native Android build pipeline.
///
/// Regenerate with:
///   `SecurityUtils.encrypt('<plain-text-value>')`
class AdMobEnv {
  AdMobEnv._();

  // Obfuscated with SecurityUtils XOR key
  static const String _obfuscatedBannerIdAndroid =
      '0d0458131f13421d1016725a574055405b575c664a5c5747475d40566958554966050803045f';

  static const String _obfuscatedInterstitialIdAndroid =
      '0d0458131f13421d1016725a574055405b575c664a5c5747475d40566e5b564a6e050305045c';

  static const String _obfuscatedRewardedIdAndroid =
      '0d0458131f13421d1016725a574055405b575c664a5c5747475d40566a59574d6c07040b0459';

  static const String _obfuscatedBannerIdIOS =
      '0d0458131f13421d1016725a574055405b575c664a5c5747475d40566d52564d680105050458';

  static const String _obfuscatedInterstitialIdIOS =
      '0d0458131f13421d1016725a574055405b575c664a5c5747475d40566b5f54486b04080b045e';

  static const String _obfuscatedRewardedIdIOS =
      '0d0458131f13421d1016725a574055405b575c664a5c5747475d40566e5c544b6b0a0501045d';

  /// Decrypted AdMob Unit IDs.
  static String get bannerIdAndroid => SecurityUtils.decrypt(_obfuscatedBannerIdAndroid);
  static String get interstitialIdAndroid => SecurityUtils.decrypt(_obfuscatedInterstitialIdAndroid);
  static String get rewardedIdAndroid => SecurityUtils.decrypt(_obfuscatedRewardedIdAndroid);

  static String get bannerIdIOS => SecurityUtils.decrypt(_obfuscatedBannerIdIOS);
  static String get interstitialIdIOS => SecurityUtils.decrypt(_obfuscatedInterstitialIdIOS);
  static String get rewardedIdIOS => SecurityUtils.decrypt(_obfuscatedRewardedIdIOS);
}
