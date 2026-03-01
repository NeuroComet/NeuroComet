import 'dart:convert';

/// Utility for basic string obfuscation to prevent simple string searches in the binary.
/// Matches the implementation in the native Android version.
class SecurityUtils {
  SecurityUtils._();

  // Simple XOR key - MUST match the one in build.gradle.kts
  static const String _xorKey = 'neurocomet_internal_security_key_2025';

  /// De-obfuscates a string that was obfuscated with XOR and Hex encoded.
  static String decrypt(String? obfuscated) {
    if (obfuscated == null ||
        obfuscated.isEmpty ||
        obfuscated == 'null' ||
        obfuscated == '""') {
      return '';
    }

    try {
      final result = StringBuffer();
      for (int i = 0; i < obfuscated.length; i += 2) {
        final hex = obfuscated.substring(i, i + 2);
        final byte = int.parse(hex, radix: 16);
        final keyChar = _xorKey[(i ~/ 2) % _xorKey.length].codeUnitAt(0);
        result.writeCharCode(byte ^ keyChar);
      }
      return result.toString();
    } catch (e) {
      return '';
    }
  }

  /// Obfuscates a string using XOR and Hex encoding.
  static String encrypt(String plain) {
    final bytes = utf8.encode(plain);
    final result = StringBuffer();
    for (int i = 0; i < bytes.length; i++) {
      final obfuscatedByte = bytes[i] ^ _xorKey[i % _xorKey.length].codeUnitAt(0);
      result.write((obfuscatedByte & 0xFF).toRadixString(16).padLeft(2, '0'));
    }
    return result.toString();
  }
}
