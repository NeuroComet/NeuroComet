-- ============================================================================
-- NeuroComet hard-delete backend support
--
-- Run this after account_care_patch.sql / setup_required_now.sql.
-- Safe to re-run.
--
-- Adds:
--   - worker metadata on public.users
--   - claim_due_account_deletions(batch_size)
--   - mark_account_deletion_failed(target_user_id, error_message)
--   - purge_account_data(target_user_id)
--
-- Intended flow:
--   1. Client schedules deletion by setting deletion_scheduled_at.
--   2. Edge Function claims due rows.
--   3. Edge Function deletes auth.users with service-role auth.
--   4. Edge Function calls purge_account_data(...) to remove remaining public data.
-- ============================================================================

ALTER TABLE public.users
    ADD COLUMN IF NOT EXISTS deletion_processing_started_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS deletion_last_error TEXT,
    ADD COLUMN IF NOT EXISTS deletion_attempt_count INTEGER NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_users_deletion_due
    ON public.users (deletion_scheduled_at)
    WHERE deletion_scheduled_at IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_users_deletion_processing
    ON public.users (deletion_processing_started_at)
    WHERE deletion_processing_started_at IS NOT NULL;

CREATE OR REPLACE FUNCTION public.claim_due_account_deletions(batch_size INTEGER DEFAULT 10)
RETURNS TABLE (
    id UUID,
    email TEXT,
    deletion_scheduled_at TIMESTAMPTZ,
    deletion_attempt_count INTEGER
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    RETURN QUERY
    WITH claimed AS (
        UPDATE public.users u
        SET
            deletion_processing_started_at = NOW(),
            deletion_last_error = NULL,
            deletion_attempt_count = COALESCE(u.deletion_attempt_count, 0) + 1,
            updated_at = NOW()
        WHERE u.id IN (
            SELECT u2.id
            FROM public.users u2
            WHERE u2.deletion_scheduled_at IS NOT NULL
              AND u2.deletion_scheduled_at <= NOW()
              AND (
                    u2.deletion_processing_started_at IS NULL
                    OR u2.deletion_processing_started_at <= NOW() - INTERVAL '15 minutes'
                  )
            ORDER BY u2.deletion_scheduled_at ASC
            LIMIT GREATEST(batch_size, 1)
            FOR UPDATE SKIP LOCKED
        )
        RETURNING u.id, u.email, u.deletion_scheduled_at, u.deletion_attempt_count
    )
    SELECT claimed.id, claimed.email, claimed.deletion_scheduled_at, claimed.deletion_attempt_count
    FROM claimed;
END;
$$;

CREATE OR REPLACE FUNCTION public.mark_account_deletion_failed(
    target_user_id UUID,
    error_message TEXT
)
RETURNS VOID
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    UPDATE public.users
    SET
        deletion_processing_started_at = NULL,
        deletion_last_error = LEFT(COALESCE(error_message, 'Unknown deletion error'), 2000),
        updated_at = NOW()
    WHERE id = target_user_id;
END;
$$;

CREATE OR REPLACE FUNCTION public.purge_account_data(target_user_id UUID)
RETURNS VOID
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    user_id_text TEXT := target_user_id::TEXT;
BEGIN
    -- Text-keyed tables that do not cascade from public.users/auth.users.
    DELETE FROM public.post_likes
    WHERE user_id = user_id_text;

    DELETE FROM public.post_comments
    WHERE user_id = user_id_text;

    DELETE FROM public.posts
    WHERE user_id = user_id_text;

    DELETE FROM public.call_signals
    WHERE from_user_id = user_id_text
       OR to_user_id = user_id_text;

    DELETE FROM public.call_history
    WHERE caller_id = user_id_text
       OR recipient_id = user_id_text;

    -- Delete the canonical public row last so FK-based tables cascade cleanly.
    DELETE FROM public.users
    WHERE id = target_user_id;

    -- Remove any empty conversations left behind after auth-user cascades.
    DELETE FROM public.conversations c
    WHERE NOT EXISTS (
        SELECT 1
        FROM public.conversation_participants cp
        WHERE cp.conversation_id = c.id
    );
END;
$$;

