$lines = Get-Content lib/l10n/app_localizations.dart
$out = @()
$skipEmotion = $false
$skipEntries = $false
foreach ($line in $lines) {
  if ($line -match "String get emotionGardenTapToPlant => get\('emotionGardenTapToPlant'\);") {
    if ($skipEmotion) { continue }
    $skipEmotion = $true
  }
  if ($line -match "String get entriesCount => get\('entriesCount'\);") {
    if ($skipEntries) { continue }
    $skipEntries = $true
  }
  if ($line -match "String get null => get\('null'\);") { continue }
  $out += $line
}
$out | Set-Content lib/l10n/app_localizations.dart -Encoding UTF8
