$lines = Get-Content lib/l10n/app_localizations.dart
$out = @()
foreach ($line in $lines) {
  if ($line -match "String get emotionGardenTapToPlant =>") { continue }
  if ($line -match "String get entriesCount =>") { continue }
  $out += $line
}
$out | Set-Content lib/l10n/app_localizations.dart -Encoding UTF8
