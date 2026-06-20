-- ============================================================================
-- NeuroComet: Missing Reactions Tables Migration
-- Run this in the Supabase Dashboard SQL Editor
-- Creates the missing tables for Direct Message and Story reactions.
-- ============================================================================

-- ============================================================================
-- 1. MESSAGE REACTIONS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.message_reactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id UUID NOT NULL REFERENCES public.dm_messages(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    emoji TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Prevent duplicate identical reactions by the same user on the same message
    UNIQUE(message_id, user_id, emoji)
);

CREATE INDEX IF NOT EXISTS idx_message_reactions_message ON public.message_reactions(message_id);
CREATE INDEX IF NOT EXISTS idx_message_reactions_user ON public.message_reactions(user_id);

-- ============================================================================
-- 2. STORY REACTIONS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.story_reactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    story_id UUID NOT NULL REFERENCES public.stories(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    emoji TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Prevent duplicate identical reactions by the same user on the same story
    UNIQUE(story_id, user_id, emoji)
);

CREATE INDEX IF NOT EXISTS idx_story_reactions_story ON public.story_reactions(story_id);
CREATE INDEX IF NOT EXISTS idx_story_reactions_user ON public.story_reactions(user_id);

-- ============================================================================
-- 3. ENABLE ROW LEVEL SECURITY (RLS)
-- ============================================================================
ALTER TABLE public.message_reactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.story_reactions ENABLE ROW LEVEL SECURITY;

-- ============================================================================
-- 4. RLS POLICIES FOR MESSAGE REACTIONS
-- ============================================================================
-- Message Reactions: Users can read reactions on messages in conversations they are part of
DROP POLICY IF EXISTS "Users can read message reactions" ON public.message_reactions;
CREATE POLICY "Users can read message reactions" ON public.message_reactions
    FOR SELECT
    USING (
        EXISTS (
            SELECT 1 FROM public.dm_messages m
            JOIN public.conversation_participants p ON m.conversation_id = p.conversation_id
            WHERE m.id = message_reactions.message_id
            AND p.user_id = auth.uid()
        )
    );

-- Message Reactions: Users can add reactions to messages in conversations they are part of
DROP POLICY IF EXISTS "Users can insert message reactions" ON public.message_reactions;
CREATE POLICY "Users can insert message reactions" ON public.message_reactions
    FOR INSERT
    WITH CHECK (
        auth.uid() = user_id AND
        EXISTS (
            SELECT 1 FROM public.dm_messages m
            JOIN public.conversation_participants p ON m.conversation_id = p.conversation_id
            WHERE m.id = message_id
            AND p.user_id = auth.uid()
        )
    );

-- Message Reactions: Users can delete their own reactions
DROP POLICY IF EXISTS "Users can delete own message reactions" ON public.message_reactions;
CREATE POLICY "Users can delete own message reactions" ON public.message_reactions
    FOR DELETE
    USING (auth.uid() = user_id);

-- ============================================================================
-- 5. RLS POLICIES FOR STORY REACTIONS
-- ============================================================================
-- Story Reactions: Authors can see reactions to their stories, and users can see their own reactions
DROP POLICY IF EXISTS "Users can read story reactions" ON public.story_reactions;
CREATE POLICY "Users can read story reactions" ON public.story_reactions
    FOR SELECT
    USING (
        auth.uid() = user_id OR
        EXISTS (
            SELECT 1 FROM public.stories s
            WHERE s.id = story_reactions.story_id
            AND s.user_id = auth.uid()
        )
    );

-- Story Reactions: Users can insert reactions to existing stories
DROP POLICY IF EXISTS "Users can insert story reactions" ON public.story_reactions;
CREATE POLICY "Users can insert story reactions" ON public.story_reactions
    FOR INSERT
    WITH CHECK (
        auth.uid() = user_id AND
        EXISTS (
            SELECT 1 FROM public.stories s
            WHERE s.id = story_id
        )
    );

-- Story Reactions: Users can delete their own reactions
DROP POLICY IF EXISTS "Users can delete own story reactions" ON public.story_reactions;
CREATE POLICY "Users can delete own story reactions" ON public.story_reactions
    FOR DELETE
    USING (auth.uid() = user_id);

-- ============================================================================
-- 6. GRANTS AND REALTIME
-- ============================================================================
GRANT SELECT, INSERT, DELETE ON public.message_reactions TO anon, authenticated;
GRANT SELECT, INSERT, DELETE ON public.story_reactions TO anon, authenticated;

-- Enable Realtime so clients receive live updates when reactions are added/removed
ALTER PUBLICATION supabase_realtime ADD TABLE public.message_reactions;
ALTER PUBLICATION supabase_realtime ADD TABLE public.story_reactions;

-- ============================================================================
-- DONE!
-- ============================================================================
