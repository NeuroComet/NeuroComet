param(
    [string]$CronSecret,

    [int]$BatchSize = 10,
    [string]$ProjectRef = 'cdaeimusmufwfixdpoep'
)

$ErrorActionPreference = 'Stop'

if ([string]::IsNullOrWhiteSpace($CronSecret)) {
    $CronSecret = $env:ACCOUNT_DELETION_CRON_SECRET
}

if ([string]::IsNullOrWhiteSpace($CronSecret)) {
    $CronSecret = Read-Host 'Enter ACCOUNT_DELETION_CRON_SECRET'
}

if ([string]::IsNullOrWhiteSpace($CronSecret)) {
    throw 'CronSecret is required.'
}

$headers = @{
    'x-account-deletion-secret' = $CronSecret
    'Content-Type' = 'application/json'
}

$body = (@{ batchSize = $BatchSize } | ConvertTo-Json -Compress)
$uri = "https://$ProjectRef.functions.supabase.co/process-account-deletions"

Write-Host "Invoking $uri ..." -ForegroundColor Cyan
Invoke-RestMethod -Method Post -Uri $uri -Headers $headers -Body $body
