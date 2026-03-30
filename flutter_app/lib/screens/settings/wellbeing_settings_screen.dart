import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../core/theme/app_colors.dart';
import '../../services/supabase_service.dart';

class WellbeingSettingsScreen extends StatefulWidget {
  const WellbeingSettingsScreen({super.key});

  @override
  State<WellbeingSettingsScreen> createState() => _WellbeingSettingsScreenState();
}

class _WellbeingSettingsScreenState extends State<WellbeingSettingsScreen> {
  bool _isLoading = true;
  bool _isSaving = false;
  bool _breakReminders = false;
  bool _quietHours = false;
  bool _pushNotifications = true;
  bool _calmModeAuto = false;
  int _selectedDetoxDays = 3;
  AccountStatus? _accountStatus;

  @override
  void initState() {
    super.initState();
    _loadState();
  }

  Future<void> _loadState() async {
    final prefs = await SharedPreferences.getInstance();
    final status = await SupabaseService.getCurrentAccountStatus();
    if (!mounted) return;
    setState(() {
      _breakReminders = prefs.getBool('break_reminders') ?? false;
      _quietHours = prefs.getBool('quiet_hours') ?? false;
      _pushNotifications = prefs.getBool('push_enabled') ?? true;
      _calmModeAuto = prefs.getBool('calm_auto') ?? false;
      _accountStatus = status;
      _isLoading = false;
    });
  }

  Future<void> _setBoolPref(String key, bool value) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(key, value);
  }

  Future<void> _toggleBreakReminders(bool value) async {
    setState(() => _breakReminders = value);
    await _setBoolPref('break_reminders', value);
  }

  Future<void> _toggleQuietHours(bool value) async {
    setState(() => _quietHours = value);
    await _setBoolPref('quiet_hours', value);
  }

  Future<void> _togglePushNotifications(bool value) async {
    setState(() => _pushNotifications = value);
    await _setBoolPref('push_enabled', value);
  }

  Future<void> _toggleCalmMode(bool value) async {
    setState(() => _calmModeAuto = value);
    await _setBoolPref('calm_auto', value);
  }

  String _formatRemaining(Duration? remaining) {
    if (remaining == null || remaining.isNegative) return 'ending soon';
    if (remaining.inDays >= 1) return '${remaining.inDays} day${remaining.inDays == 1 ? '' : 's'} left';
    if (remaining.inHours >= 1) return '${remaining.inHours} hour${remaining.inHours == 1 ? '' : 's'} left';
    final minutes = remaining.inMinutes.clamp(1, 59);
    return '$minutes minute${minutes == 1 ? '' : 's'} left';
  }

  Future<void> _startDetox() async {
    setState(() => _isSaving = true);
    final result = await SupabaseService.startDetoxMode(
      duration: Duration(days: _selectedDetoxDays),
    );
    if (!mounted) return;
    setState(() => _isSaving = false);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(result['message']?.toString() ?? 'Done')),
    );
    if (result['success'] == true) {
      context.go('/auth');
    }
  }

  Future<void> _endDetox() async {
    setState(() => _isSaving = true);
    final result = await SupabaseService.endDetoxMode();
    await _loadState();
    if (!mounted) return;
    setState(() => _isSaving = false);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(result['message']?.toString() ?? 'Done')),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final detoxActive = _accountStatus?.isDetoxActive == true;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Wellbeing & Detox'),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : ListView(
              padding: const EdgeInsets.all(16),
              children: [
                _SectionHeader(
                  title: 'Gentler defaults',
                  subtitle: 'Small changes that make stepping away easier.',
                  icon: Icons.spa_outlined,
                ),
                _SettingsCard(
                  child: Column(
                    children: [
                      SwitchListTile.adaptive(
                        title: const Text('Break reminders'),
                        subtitle: const Text('Get nudges to pause before you burn out.'),
                        value: _breakReminders,
                        onChanged: _toggleBreakReminders,
                      ),
                      const Divider(height: 1),
                      SwitchListTile.adaptive(
                        title: const Text('Quiet hours'),
                        subtitle: const Text('Silence the app during your calm window.'),
                        value: _quietHours,
                        onChanged: _toggleQuietHours,
                      ),
                      const Divider(height: 1),
                      SwitchListTile.adaptive(
                        title: const Text('Push notifications'),
                        subtitle: const Text('Turn off device interruptions while you recover.'),
                        value: _pushNotifications,
                        onChanged: _togglePushNotifications,
                      ),
                      const Divider(height: 1),
                      SwitchListTile.adaptive(
                        title: const Text('Auto calm mode'),
                        subtitle: const Text('Bias the app toward quieter, less stimulating defaults.'),
                        value: _calmModeAuto,
                        onChanged: _toggleCalmMode,
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 24),
                _SectionHeader(
                  title: 'Detox mode',
                  subtitle: 'Take a real break without deleting your account.',
                  icon: Icons.self_improvement_outlined,
                ),
                _SettingsCard(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          detoxActive
                              ? 'Detox is active — ${_formatRemaining(_accountStatus?.detoxRemaining)}.'
                              : 'Starting detox signs you out, quiets notifications, and keeps your account intact while you rest.',
                          style: theme.textTheme.bodyMedium,
                        ),
                        if (_accountStatus?.detoxUntil != null) ...[
                          const SizedBox(height: 8),
                          Text(
                            'Until: ${_accountStatus!.detoxUntil!.toLocal()}',
                            style: theme.textTheme.bodySmall?.copyWith(
                              color: theme.colorScheme.onSurfaceVariant,
                            ),
                          ),
                        ],
                        const SizedBox(height: 16),
                        if (!detoxActive) ...[
                          Wrap(
                            spacing: 8,
                            runSpacing: 8,
                            children: [1, 3, 7, 14].map((days) {
                              return ChoiceChip(
                                label: Text('$days day${days == 1 ? '' : 's'}'),
                                selected: _selectedDetoxDays == days,
                                onSelected: (_) => setState(() => _selectedDetoxDays = days),
                              );
                            }).toList(),
                          ),
                          const SizedBox(height: 16),
                          FilledButton.icon(
                            onPressed: _isSaving ? null : _startDetox,
                            icon: _isSaving
                                ? const SizedBox(
                                    width: 16,
                                    height: 16,
                                    child: CircularProgressIndicator(strokeWidth: 2),
                                  )
                                : const Icon(Icons.hotel_outlined),
                            label: const Text('Start detox'),
                          ),
                        ] else ...[
                          OutlinedButton.icon(
                            onPressed: _isSaving ? null : _endDetox,
                            icon: _isSaving
                                ? const SizedBox(
                                    width: 16,
                                    height: 16,
                                    child: CircularProgressIndicator(strokeWidth: 2),
                                  )
                                : const Icon(Icons.login_rounded),
                            label: const Text('End detox early'),
                          ),
                        ],
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 24),
                Container(
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: AppColors.secondaryTeal.withValues(alpha: 0.08),
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: AppColors.secondaryTeal.withValues(alpha: 0.25)),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Need something more permanent?',
                        style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.w700),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Use Privacy settings if you want to schedule account deletion with the 14-day grace period instead of taking a temporary break.',
                        style: theme.textTheme.bodySmall,
                      ),
                    ],
                  ),
                ),
              ],
            ),
    );
  }
}

class _SectionHeader extends StatelessWidget {
  final String title;
  final String subtitle;
  final IconData icon;

  const _SectionHeader({
    required this.title,
    required this.subtitle,
    required this.icon,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.only(left: 4, bottom: 8),
      child: Row(
        children: [
          Icon(icon, color: theme.colorScheme.primary),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.w700),
                ),
                Text(
                  subtitle,
                  style: theme.textTheme.bodySmall?.copyWith(
                    color: theme.colorScheme.onSurfaceVariant,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _SettingsCard extends StatelessWidget {
  final Widget child;

  const _SettingsCard({required this.child});

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: BorderSide(color: Theme.of(context).dividerColor),
      ),
      child: child,
    );
  }
}

