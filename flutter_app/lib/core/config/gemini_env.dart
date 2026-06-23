import '../utils/security_utils.dart';

/// Obfuscated Gemini credentials that match the native Android build pipeline.
///
/// Regenerate with:
///   `SecurityUtils.encrypt('<plain-text-value>')`
class GeminiEnv {
  GeminiEnv._();

  // Encrypted with SecurityUtils XOR key
  static const String _obfuscatedApiKey =
      '2f345b330d5b3d23533f353f0842110a00053d1930260401421b3718285d5c48'
      '0f5a4159420d372d463e11573924326f3d0f3d0623';

  /// Decrypted Gemini API key.
  static String get apiKey => SecurityUtils.decrypt(_obfuscatedApiKey);
}
