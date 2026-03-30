-- ============================================================================
-- NeuroComet account-care patch
--
-- Use this if you already ran an older setup script and only need the
-- account deletion + detox columns added safely.
-- Safe to re-run.
-- ============================================================================

ALTER TABLE public.users
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS deletion_scheduled_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS detox_started_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS detox_until TIMESTAMPTZ;

-- Optional cleanup for users whose detox window already expired.
UPDATE public.users
SET
    detox_started_at = NULL,
    detox_until = NULL,
    updated_at = NOW()
WHERE detox_until IS NOT NULL
  AND detox_until <= NOW();

