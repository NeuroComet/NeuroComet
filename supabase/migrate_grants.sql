-- ============================================================================
-- NeuroComet: Grant table permissions to PostgREST roles
--
-- PostgREST uses `anon` for unauthenticated requests and `authenticated`
-- for requests with a valid JWT.  Without explicit GRANTs the roles cannot
-- see the tables at all — PostgREST returns 404 (table invisible) or 500
-- (RLS policy subquery fails because a referenced table is invisible).
--
-- Run this in the Supabase SQL Editor.  Safe to re-run.
-- ============================================================================

-- Schema-level access (usually already set in Supabase, but just in case)
GRANT USAGE ON SCHEMA public TO anon, authenticated, service_role;

-- Ensure future tables also get grants automatically
ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO anon, authenticated, service_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO anon, authenticated, service_role;

-- ============================================================================
-- Explicit grants for every table the app uses
-- ============================================================================

GRANT SELECT, INSERT, UPDATE, DELETE ON public.users               TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.profiles            TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.posts               TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.post_likes          TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.post_comments       TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.conversations       TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.conversation_participants TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.dm_messages         TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.follows             TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.blocked_users       TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.muted_users         TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.bookmarks           TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.notifications       TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.reports             TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.call_signals        TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.call_history        TO anon, authenticated;

-- Sequences (auto-increment IDs like posts.id BIGSERIAL)
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO anon, authenticated;

