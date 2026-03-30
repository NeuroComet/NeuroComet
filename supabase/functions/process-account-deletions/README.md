# `process-account-deletions`

Supabase Edge Function template for true server-side account deletion.

## What it does

1. claims due rows from `public.users`
2. deletes the matching `auth.users` account with the service-role key
3. purges remaining public/text-keyed user data via SQL helper
4. records failures back on `public.users.deletion_last_error`

## Prerequisites

Run these SQL files first:

- `supabase/account_care_patch.sql`
- `supabase/account_care_hard_delete.sql`

## Required secrets

Custom secret you must set manually:

- `ACCOUNT_DELETION_CRON_SECRET`

Generate it in PowerShell with:

```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$cronSecret = ($bytes | ForEach-Object { $_.ToString('x2') }) -join ''
$cronSecret
```

Built-in Edge Function env vars provided automatically by Supabase:

- `SUPABASE_URL`
- `SUPABASE_SERVICE_ROLE_KEY`

## Suggested deploy flow

```bash
supabase functions deploy process-account-deletions --project-ref cdaeimusmufwfixdpoep --no-verify-jwt
```

Why `--no-verify-jwt` matters:

- this function is protected by `ACCOUNT_DELETION_CRON_SECRET`
- requests are expected to use `x-account-deletion-secret`
- if JWT verification stays enabled, Supabase may reject the request at the gateway before the function code runs

## Manual invocation

```bash
curl -X POST "https://<project-ref>.functions.supabase.co/process-account-deletions" \
  -H "x-account-deletion-secret: <ACCOUNT_DELETION_CRON_SECRET>" \
  -H "Content-Type: application/json" \
  -d '{"batchSize":10}'
```

## Scheduling

You can trigger this from:

- Supabase Scheduled Functions / cron
- GitHub Actions
- another trusted backend worker

Use the same `x-account-deletion-secret` value in the request.

## Safe rollout suggestion

1. schedule a test account for deletion
2. manually set `deletion_scheduled_at` to a past timestamp for that test account
3. invoke the function once
4. run `supabase/verify_account_deletion_backend.sql`
5. confirm the user is gone from both `auth.users` and `public.users`

## Important note

The client apps can keep using the current scheduled deletion UX.
This function is the backend processor that makes deletion truly permanent.
