# Account care runbook

Use this after the new account deletion + detox mode changes.

## 1) Apply the backend delta

If you have **already** run the broader setup script before, run:

- `supabase/account_care_patch.sql`

If you have **not** applied the full current setup yet, run:

- `supabase/setup_required_now.sql`

## 2) Add server-side hard deletion support

Run:

- `supabase/account_care_hard_delete.sql`

This adds:
- worker metadata columns
- deletion claim/failure helpers
- `purge_account_data(...)`

## 3) Verify the account-care columns exist

Run:

- `supabase/verify_account_lifecycle.sql`

Before running it, replace:

- `replace-me@example.com`

with a real test account email.

## 4) What to test in the app

### Scheduled deletion
1. Sign in
2. Open privacy/account settings
3. Schedule deletion
4. Confirm the app signs you out
5. Run the verification SQL

Expected:
- `is_active = false`
- `deletion_scheduled_at != null`

### Cancel deletion
1. Sign back in with the same account
2. Choose **Cancel deletion** when prompted
3. Run the verification SQL

Expected:
- `is_active = true`
- `deletion_scheduled_at = null`

### Detox mode
1. Sign in
2. Open wellbeing/detox settings
3. Start detox for 1 / 3 / 7 / 14 days
4. Confirm the app signs you out
5. Run the verification SQL

Expected:
- `detox_started_at != null`
- `detox_until != null`
- `detox_until > now()`

### End detox early
1. Sign back in with the same account
2. Choose **End detox** when prompted
3. Run the verification SQL

Expected:
- `detox_started_at = null`
- `detox_until = null`

## 5) Generate the cron secret

In PowerShell, from the repo root, generate a strong secret like this:

```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$cronSecret = ($bytes | ForEach-Object { $_.ToString('x2') }) -join ''
$cronSecret
```

Save the printed value somewhere safe.
You will use this same value for:
- `ACCOUNT_DELETION_CRON_SECRET`
- manual function invocation tests

## 6) Upload the cron secret

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\set_supabase_function_secrets.ps1 -CronSecret $cronSecret
```

## 7) Deploy the hard-delete Edge Function

Use:

- `supabase/functions/process-account-deletions/index.ts`
- `supabase/functions/process-account-deletions/README.md`

Custom secret you must set manually:
- `ACCOUNT_DELETION_CRON_SECRET`

Built-in Edge Function env vars Supabase already provides:
- `SUPABASE_URL`
- `SUPABASE_SERVICE_ROLE_KEY`

Suggested deploy command:

```bash
supabase functions deploy process-account-deletions --project-ref cdaeimusmufwfixdpoep --no-verify-jwt
```

Use `--no-verify-jwt` because this function is protected by `ACCOUNT_DELETION_CRON_SECRET` instead of a Supabase JWT.

## 8) Test true permanent deletion

1. Schedule deletion for a real test account
2. In Supabase SQL Editor, temporarily set that account's `deletion_scheduled_at` to a past timestamp
3. Invoke the Edge Function once, for example in PowerShell:

```powershell
$cronSecret = 'your-current-cron-secret'
powershell -ExecutionPolicy Bypass -File .\tools\invoke_account_deletion_function.ps1 -CronSecret $cronSecret
```

4. Run:
   - `supabase/verify_account_deletion_backend.sql`

Expected final state:
- user is gone from `auth.users`
- user is gone from `public.users`
- user is gone from `public.profiles`
- text-keyed content rows are gone too

## 9) Important limitation

The current app UX is still a **scheduled deletion flow first**.
The new Edge Function is what turns a due scheduled deletion into a real permanent deletion.

That means:
- client app schedules deletion safely
- backend worker performs actual `auth.users` deletion later

## 10) Quick note

If you already ran an older version of `setup_required_now.sql`, the safest next move is:
1. run `account_care_patch.sql`
2. run `account_care_hard_delete.sql`
3. test one real account
4. run `verify_account_lifecycle.sql`
5. deploy/invoke the Edge Function
6. run `verify_account_deletion_backend.sql`
