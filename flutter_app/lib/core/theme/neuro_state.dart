import 'package:flutter/material.dart';

/// Represents a neuro-centric UI state that adapts the theme
/// to the user's current sensory and mental needs.
enum NeuroState {
  defaultState,
  hyperfocus,
  overload,
  calm,
  adhdEnergized,
  adhdLowDopamine,
  adhdTaskMode,
  autismRoutine,
  autismSensorySeek,
  autismLowStim,
  anxietySoothe,
  anxietyGrounding,
  dyslexiaFriendly,
  colorblindDeuter,
  colorblindProtan,
  colorblindTritan,
  colorblindMono,
  blindScreenReader,
  blindHighContrast,
  blindLargeText,
  moodTired,
  moodAnxious,
  moodHappy,
  moodOverwhelmed,
  moodCreative,
  rainbowBrain,
  cinnamonBun,
}

/// Metadata for each NeuroState
class NeuroStateMetadata {
  final String id;
  final String emoji;
  final Color baseColor;

  const NeuroStateMetadata({
    required this.id,
    required this.emoji,
    required this.baseColor,
  });
}

extension NeuroStateX on NeuroState {
  String get name {
    switch (this) {
      case NeuroState.defaultState: return 'Default';
      case NeuroState.hyperfocus: return 'Hyperfocus';
      case NeuroState.overload: return 'Sensory Overload';
      case NeuroState.calm: return 'Calm';
      case NeuroState.adhdEnergized: return 'ADHD - Energized';
      case NeuroState.adhdLowDopamine: return 'ADHD - Low Dopamine';
      case NeuroState.adhdTaskMode: return 'ADHD - Task Mode';
      case NeuroState.autismRoutine: return 'Autism - Routine';
      case NeuroState.autismSensorySeek: return 'Autism - Sensory Seeking';
      case NeuroState.autismLowStim: return 'Autism - Low Stimulation';
      case NeuroState.anxietySoothe: return 'Anxiety - Soothe';
      case NeuroState.anxietyGrounding: return 'Anxiety - Grounding';
      case NeuroState.dyslexiaFriendly: return 'Dyslexia Friendly';
      case NeuroState.colorblindDeuter: return 'Deuteranopia';
      case NeuroState.colorblindProtan: return 'Protanopia';
      case NeuroState.colorblindTritan: return 'Tritanopia';
      case NeuroState.colorblindMono: return 'Monochromacy';
      case NeuroState.blindScreenReader: return 'Screen Reader Mode';
      case NeuroState.blindHighContrast: return 'Maximum Contrast';
      case NeuroState.blindLargeText: return 'Large Text Mode';
      case NeuroState.moodTired: return 'Feeling Tired';
      case NeuroState.moodAnxious: return 'Feeling Anxious';
      case NeuroState.moodHappy: return 'Feeling Happy';
      case NeuroState.moodOverwhelmed: return 'Feeling Overwhelmed';
      case NeuroState.moodCreative: return 'Feeling Creative';
      case NeuroState.rainbowBrain: return 'Rainbow Brain';
      case NeuroState.cinnamonBun: return 'Cinnamon Bun';
    }
  }

  String get emoji {
    switch (this) {
      case NeuroState.defaultState: return '☄️';
      case NeuroState.hyperfocus: return '🎯';
      case NeuroState.overload: return '⚡';
      case NeuroState.calm: return '🌊';
      case NeuroState.adhdEnergized: return '⚡';
      case NeuroState.adhdLowDopamine: return '🔋';
      case NeuroState.adhdTaskMode: return '📝';
      case NeuroState.autismRoutine: return '📊';
      case NeuroState.autismSensorySeek: return '✨';
      case NeuroState.autismLowStim: return '🫧';
      case NeuroState.anxietySoothe: return '💙';
      case NeuroState.anxietyGrounding: return '🌍';
      case NeuroState.dyslexiaFriendly: return '📖';
      case NeuroState.colorblindDeuter: return '👁️';
      case NeuroState.colorblindProtan: return '👁️';
      case NeuroState.colorblindTritan: return '👁️';
      case NeuroState.colorblindMono: return '⚪';
      case NeuroState.blindScreenReader: return '🔊';
      case NeuroState.blindHighContrast: return '🏁';
      case NeuroState.blindLargeText: return '🔠';
      case NeuroState.moodTired: return '😴';
      case NeuroState.moodAnxious: return '😟';
      case NeuroState.moodHappy: return '😊';
      case NeuroState.moodOverwhelmed: return '😵‍💫';
      case NeuroState.moodCreative: return '🎨';
      case NeuroState.rainbowBrain: return '🌈';
      case NeuroState.cinnamonBun: return '🥐';
    }
  }

  Color get color {
    switch (this) {
      case NeuroState.defaultState: return const Color(0xFF9B59B6);
      case NeuroState.hyperfocus: return const Color(0xFFE74C3C);
      case NeuroState.overload: return const Color(0xFFF1C40F);
      case NeuroState.calm: return const Color(0xFF3498DB);
      case NeuroState.adhdEnergized: return const Color(0xFFE67E22);
      case NeuroState.adhdLowDopamine: return const Color(0xFF1ABC9C);
      case NeuroState.adhdTaskMode: return const Color(0xFF34495E);
      case NeuroState.autismRoutine: return const Color(0xFF2ECC71);
      case NeuroState.autismSensorySeek: return const Color(0xFFE91E63);
      case NeuroState.autismLowStim: return const Color(0xFF95A5A6);
      case NeuroState.anxietySoothe: return const Color(0xFFADD8E6);
      case NeuroState.anxietyGrounding: return const Color(0xFF8B4513);
      case NeuroState.dyslexiaFriendly: return const Color(0xFFFFEB3B);
      case NeuroState.colorblindDeuter: return const Color(0xFF0072B2);
      case NeuroState.colorblindProtan: return const Color(0xFFD55E00);
      case NeuroState.colorblindTritan: return const Color(0xFF009E73);
      case NeuroState.colorblindMono: return const Color(0xFFFFFFFF);
      case NeuroState.blindScreenReader: return const Color(0xFF000000);
      case NeuroState.blindHighContrast: return const Color(0xFF000000);
      case NeuroState.blindLargeText: return const Color(0xFF000000);
      case NeuroState.moodTired: return const Color(0xFF483D8B);
      case NeuroState.moodAnxious: return const Color(0xFF708090);
      case NeuroState.moodHappy: return const Color(0xFFFFD700);
      case NeuroState.moodOverwhelmed: return const Color(0xFF4682B4);
      case NeuroState.moodCreative: return const Color(0xFF8A2BE2);
      case NeuroState.rainbowBrain: return const Color(0xFFFF69B4);
      case NeuroState.cinnamonBun: return const Color(0xFFD2691E);
    }
  }
}
