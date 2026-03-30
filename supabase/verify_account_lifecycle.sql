-- ============================================================================
-- NeuroComet account lifecycle verification
--
-- Replace the email below with the test account you want to verify.
-- Run after sign-in / delete-schedule / cancel-delete / detox actions.
-- ============================================================================

WITH target_user AS (
    SELECT id, email, created_at
    FROM auth.users
    WHERE email = 'replace-me@example.com'
    ORDER BY created_at DESC
    LIMIT 1
),
public_row AS (
    SELECT
        u.id,
        u.email,
        u.is_active,
        u.deletion_scheduled_at,
        u.detox_started_at,
        u.detox_until,
        u.updated_at,
        CASE
            WHEN u.detox_until IS NOT NULL AND u.detox_until > NOW() THEN true
            ELSE false
        END AS detox_active_now
    FROM public.users u
    JOIN target_user t ON t.id = u.id
),
profile_row AS (
    SELECT p.id, p.username, p.display_name, p.updated_at
    FROM public.profiles p
    JOIN target_user t ON t.id = p.id
)
SELECT
    t.id AS auth_user_id,
    t.email AS auth_email,
    EXISTS (SELECT 1 FROM public_row) AS has_public_user,
    EXISTS (SELECT 1 FROM profile_row) AS has_profile,
    pr.is_active,
    pr.deletion_scheduled_at,
    pr.detox_started_at,
    pr.detox_until,
    pr.detox_active_now,
    pf.username,
    pf.display_name,
    pr.updated_at AS users_updated_at,
    pf.updated_at AS profiles_updated_at
FROM target_user t
LEFT JOIN public_row pr ON pr.id = t.id
LEFT JOIN profile_row pf ON pf.id = t.id;

-- --------------------------------------------------------------------------
-- Quick schema checks
-- --------------------------------------------------------------------------
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'users'
  AND column_name IN (
      'is_active',
      'deletion_scheduled_at',
      'detox_started_at',
      'detox_until'
  )
ORDER BY column_name;

-- --------------------------------------------------------------------------
-- Expected state guide
-- --------------------------------------------------------------------------
-- Normal account:
--   is_active = true
--   deletion_scheduled_at = null
--   detox_started_at = null
--   detox_until = null
--
-- Scheduled deletion:
--   is_active = false
--   deletion_scheduled_at != null
--
-- Detox active:
--   detox_started_at != null
--   detox_until != null and > now()
--
-- Detox ended / expired:
--   detox_started_at = null
--   detox_until = null
