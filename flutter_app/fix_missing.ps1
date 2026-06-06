$lines = Get-Content lib/l10n/app_localizations.dart
$out = @()
foreach ($line in $lines) {
  if ($line -match "String get yourStory => get\('yourStory'\);") {
    $out += "  String get authSignInSubtitle => get('authSignInSubtitle');"
    $out += "  String get authSignUpSubtitle => get('authSignUpSubtitle');"
  }
  if ($line -match "'es': \{") {
    $out += "      'authSignInSubtitle': 'Sign in to continue',"
    $out += "      'authSignUpSubtitle': 'Create an account to join us',"
  }
  $out += $line
}
$out | Set-Content lib/l10n/app_localizations.dart -Encoding UTF8
