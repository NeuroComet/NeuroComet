-- ============================================================================
-- NeuroComet backend hard-delete verification
--
-- Replace the email below with the account you scheduled for deletion.
-- Run before and after invoking the process-account-deletions Edge Function.
-- ============================================================================

WITH target AS (
    SELECT email
    FROM (VALUES ('replace-me@example.com')) AS v(email)
),
auth_match AS (
    SELECT au.id, au.email, au.created_at
    FROM auth.users au
    JOIN target t ON t.email = au.email
),
public_match AS (
    SELECT u.id, u.email, u.is_active, u.deletion_scheduled_at,
           u.deletion_processing_started_at, u.deletion_last_error, u.deletion_attempt_count
    FROM public.users u
    JOIN target t ON t.email = u.email
),
profile_match AS (
    SELECT p.id
    FROM public.profiles p
    JOIN auth_match a ON a.id = p.id
),
text_keyed_counts AS (
    SELECT
        COALESCE((SELECT COUNT(*) FROM public.posts p JOIN auth_match a ON p.user_id = a.id::text), 0) AS posts_count,
        COALESCE((SELECT COUNT(*) FROM public.post_likes pl JOIN auth_match a ON pl.user_id = a.id::text), 0) AS post_likes_count,
        COALESCE((SELECT COUNT(*) FROM public.post_comments pc JOIN auth_match a ON pc.user_id = a.id::text), 0) AS post_comments_count,
        COALESCE((SELECT COUNT(*) FROM public.call_signals cs JOIN auth_match a ON cs.from_user_id = a.id::text OR cs.to_user_id = a.id::text), 0) AS call_signals_count,
        COALESCE((SELECT COUNT(*) FROM public.call_history ch JOIN auth_match a ON ch.caller_id = a.id::text OR ch.recipient_id = a.id::text), 0) AS call_history_count
)
SELECT
    EXISTS (SELECT 1 FROM auth_match) AS auth_user_exists,
    EXISTS (SELECT 1 FROM public_match) AS public_user_exists,
    EXISTS (SELECT 1 FROM profile_match) AS profile_exists,
    (SELECT deletion_scheduled_at FROM public_match LIMIT 1) AS deletion_scheduled_at,
    (SELECT deletion_processing_started_at FROM public_match LIMIT 1) AS deletion_processing_started_at,
    (SELECT deletion_last_error FROM public_match LIMIT 1) AS deletion_last_error,
    (SELECT deletion_attempt_count FROM public_match LIMIT 1) AS deletion_attempt_count,
    tk.posts_count,
    tk.post_likes_count,
    tk.post_comments_count,
    tk.call_signals_count,
    tk.call_history_count
FROM text_keyed_counts tk;

-- Expected final state after successful processing:
--   auth_user_exists = false
--   public_user_exists = false
--   profile_exists = false
--   all *_count columns = 0

