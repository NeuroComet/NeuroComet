param(
    [Parameter(Mandatory = $true)]
    [string]$CronSecret,

    [string]$ProjectRef = 'cdaeimusmufwfixdpoep'
)

$ErrorActionPreference = 'Stop'

if (-not (Get-Command supabase -ErrorAction SilentlyContinue)) {
    throw 'Supabase CLI is not installed or not on PATH.'
}

Write-Host "Setting Edge Function secrets for project $ProjectRef ..." -ForegroundColor Cyan
Write-Host 'Note: SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY are reserved Edge Function env vars and are provided automatically by Supabase.' -ForegroundColor DarkGray

supabase secrets set --project-ref $ProjectRef "ACCOUNT_DELETION_CRON_SECRET=$CronSecret"

if ($LASTEXITCODE -ne 0) {
    throw "supabase secrets set failed with exit code $LASTEXITCODE"
}

Write-Host 'Custom secret uploaded successfully.' -ForegroundColor Green
