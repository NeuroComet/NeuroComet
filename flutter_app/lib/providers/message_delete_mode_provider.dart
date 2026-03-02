import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// How users choose to delete conversations in the Messages tab.
enum MessageDeleteMode {
  /// Swipe left to delete (with confirmation).
  swipe,

  /// Long-press to reveal options including delete.
  longPress,
}

/// Riverpod provider for the message-delete-mode user preference.
final messageDeleteModeProvider =
    NotifierProvider<MessageDeleteModeNotifier, MessageDeleteMode>(
  MessageDeleteModeNotifier.new,
);

class MessageDeleteModeNotifier extends Notifier<MessageDeleteMode> {
  static const _key = 'message_delete_mode';

  @override
  MessageDeleteMode build() {
    _loadFromPrefs();
    // Default to long-press (safer, more accessible)
    return MessageDeleteMode.longPress;
  }

  Future<void> _loadFromPrefs() async {
    final prefs = await SharedPreferences.getInstance();
    final value = prefs.getString(_key);
    if (value != null) {
      state = MessageDeleteMode.values.firstWhere(
        (m) => m.name == value,
        orElse: () => MessageDeleteMode.longPress,
      );
    }
  }

  Future<void> setMode(MessageDeleteMode mode) async {
    state = mode;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_key, mode.name);
  }
}

