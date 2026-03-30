import '../constants/app_constants.dart';

/// Shared visual metadata for neuro state presentation across multiple UI flows.
class NeuroStateVisualRegistry {
  NeuroStateVisualRegistry._();

  static String displayNameForStateKey(String stateKey, {required String fallback}) {
    switch (stateKey) {
      case 'calm':
        return 'Calm Waters';
      case 'autismSensorySeek':
        return 'Sensory Mode';
      default:
        return fallback;
    }
  }

  static String? assetPathForStateKey(String stateKey) {
    switch (stateKey) {
      case 'calm':
        return AppAssetPaths.neuroStateCalmWatersIcon;
      case 'autismSensorySeek':
        return AppAssetPaths.neuroStateSensoryModeIcon;
      default:
        return null;
    }
  }
}

