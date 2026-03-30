-- ============================================================================
-- NeuroComet: Fix circular RLS policies on messaging tables
--
-- PROBLEM:
--   conversation_participants has a SELECT policy that subqueries itself.
--   conversations and dm_messages have SELECT policies that subquery
--   conversation_participants.  When PostgreSQL evaluates the inner
--   subquery it re-applies the self-referential policy, causing infinite
--   recursion → 500 Internal Server Error from PostgREST.
--
-- FIX:
--   1. Create a SECURITY DEFINER helper function that checks membership
--      without triggering RLS (runs as the function owner, bypassing row
--      policies on the lookup table).
--   2. Simplify conversation_participants SELECT to a direct uid check.
--   3. Rewrite conversations / dm_messages SELECT policies to use the
--      helper function instead of an inline EXISTS subquery.
--
-- Safe to re-run.
-- ============================================================================

-- 1. Helper function — SECURITY DEFINER bypasses RLS on the inner query
CREATE OR REPLACE FUNCTION public.is_conversation_member(conv_id UUID)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
SECURITY DEFINER
AS $$
    SELECT EXISTS (
        SELECT 1
        FROM public.conversation_participants
        WHERE conversation_id = conv_id
          AND user_id = auth.uid()
    );
$$;

-- 2. Drop the broken policies
DROP POLICY IF EXISTS "Users can view their conversations"          ON public.conversations;
DROP POLICY IF EXISTS "Users can view conversation participants"    ON public.conversation_participants;
DROP POLICY IF EXISTS "Users can view messages in their conversations" ON public.dm_messages;
DROP POLICY IF EXISTS "Users can send messages to their conversations" ON public.dm_messages;
DROP POLICY IF EXISTS "Users can update message read status"        ON public.dm_messages;

-- 3. Recreate with non-recursive definitions

-- conversation_participants: simple direct check (no subquery on self)
CREATE POLICY "Users can view conversation participants"
    ON public.conversation_participants FOR SELECT
    USING (user_id = auth.uid());

-- conversations: use the SECURITY DEFINER helper
CREATE POLICY "Users can view their conversations"
    ON public.conversations FOR SELECT
    USING (public.is_conversation_member(id));

-- dm_messages: use the same helper
CREATE POLICY "Users can view messages in their conversations"
    ON public.dm_messages FOR SELECT
    USING (public.is_conversation_member(conversation_id));

CREATE POLICY "Users can send messages to their conversations"
    ON public.dm_messages FOR INSERT
    WITH CHECK (
        auth.uid() = sender_id
        AND public.is_conversation_member(conversation_id)
    );

CREATE POLICY "Users can update message read status"
    ON public.dm_messages FOR UPDATE
    USING  (public.is_conversation_member(conversation_id))
    WITH CHECK (public.is_conversation_member(conversation_id));

