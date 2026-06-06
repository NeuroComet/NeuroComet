$lines = Get-Content lib/l10n/app_localizations.dart
$out = @()
$skip = $false
foreach ($line in $lines) {
  if ($line -match "String get emotionGardenTapToPlant => get\('emotionGardenTapToPlant'\);" -or $line -match "String get entriesCount => get\('entriesCount'\);") {
    if ($skip) { continue }
    $skip = $true
  }
  $out += $line
}
$out | Set-Content lib/l10n/app_localizations.dart -Encoding UTF8
