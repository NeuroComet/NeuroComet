import re
with open("app/src/main/java/com/kyilmaz/neurocomet/MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()
# Fix isBlocked and isMuted 
# (Matches the older closure and replaces with boolean checks)
text = re.sub(
    r'isBlocked \= \{ messagesViewModel\.isUserBlocked\(it\) \}',
    r'isUserBlocked = messagesState.blockedUserIds.contains(activeConversation?.participants?.firstOrNull { it != messagesState.currentUserId } ?: activeConversation?.participants?.firstOrNull { it != "me" } ?: "")',
    text
)
text = re.sub(
    r'isMuted \= \{ messagesViewModel\.isUserMuted\(it\) \}',
    r'isUserMuted = messagesState.mutedUserIds.contains(activeConversation?.participants?.firstOrNull { it != messagesState.currentUserId } ?: activeConversation?.participants?.firstOrNull { it != "me" } ?: "")',
    text
)
# Special case for "conv" variable in dual pane layout fallback
text = re.sub(
    r'isBlocked \= \{ messagesViewModel\.isUserBlocked\(conv\.id\) \}',  # simplified match
    r'isUserBlocked = messagesState.blockedUserIds.contains(conv.participants.firstOrNull { it != messagesState.currentUserId } ?: conv.participants.firstOrNull { it != "me" } ?: "")',
    text
)
text = re.sub(
    r'isMuted \= \{ messagesViewModel\.isUserMuted\(conv\.id\) \}',
    r'isUserMuted = messagesState.mutedUserIds.contains(conv.participants.firstOrNull { it != messagesState.currentUserId } ?: conv.participants.firstOrNull { it != "me" } ?: "")',
    text
)
# And another isBlocked block that might just use isBlocked = { ...(it) } inside conv block
# We can just run it globally again if there's any left
text = re.sub(
    r'isBlocked \= \{[^}]+\}',
    r'isUserBlocked = messagesState.blockedUserIds.contains(activeConversation?.participants?.firstOrNull { it != "me" } ?: "")',
    text
)
text = re.sub(
    r'isMuted \= \{[^}]+\}',
    r'isUserMuted = messagesState.mutedUserIds.contains(activeConversation?.participants?.firstOrNull { it != "me" } ?: "")',
    text
)
# Fix onStartCall (NeuroInboxScreen)
startCallPatch = """                            onStartCall = { userId, displayName, avatar, isVideo ->
                                if (feedState.isMockInterfaceEnabled) {
                                    com.kyilmaz.neurocomet.MockCallManager.startCall(userId, displayName, avatar, if (isVideo) com.kyilmaz.neurocomet.CallType.VIDEO else com.kyilmaz.neurocomet.CallType.VOICE)
                                } else {
                                    com.kyilmaz.neurocomet.calling.WebRTCCallManager.getInstance().startCall(userId, displayName, avatar, if (isVideo) com.kyilmaz.neurocomet.calling.CallType.VIDEO else com.kyilmaz.neurocomet.calling.CallType.VOICE)
                                }
                            }"""
text = re.sub(
    r'(onOpenPracticeCall \= \{\s+navController\.navigate\(Screen\.PracticeCallSelection\.route\)\s+\}\n\s*)\)',
    r'\1,\n' + startCallPatch + '\n                        )',
    text
)
# Fix onStartCall (NeuroConversationScreen)
# Also needs onStartCall
text = re.sub(
    r'(onSimulatedReply \= \{ [^}]+\s+\}\n\s*)\)',
    r'\1,\n' + startCallPatch + '\n                        )',
    text
)
# Fix FeedScreen instantiation
text = re.sub(r'posts \= feedState\.posts,', r'feedUiState = feedState,', text)
text = re.sub(r'(\s+)stories \= feedState\.stories,\n', r'', text)
text = re.sub(r'(\s+)currentUser \= CURRENT_USER\.copy\(isVerified \= isUserVerified\),\n', r'', text)
text = re.sub(r'(\s+)isLoading \= feedState\.isLoading,\n', r'', text)
text = re.sub(
    r'onAddPost \= \{ content: String, tone: String, imageUrl: String\?, videoUrl: String\? \->\s+feedViewModel\.createPost\(content, tone, imageUrl, videoUrl\)\s+\},',
    r'onAddPost = { content, tone, imageUrl, videoUrl, parentId, category -> feedViewModel.createPost(content, tone, imageUrl, videoUrl, parentId, category) },',
    text
)
text = re.sub(
    r'onAddStory \= \{ imageUrl, duration \-> feedViewModel\.createStory\(imageUrl, duration\) \},',
    r'onAddStory = { type, imageUrl, duration, overlay, bgColor, bgEnd, link -> feedViewModel.createStory(type, imageUrl, duration, overlay, bgColor, bgEnd, link) },',
    text
)
# Replace the specific conv block issue if regex didn't catch the exact string:
with open("app/src/main/java/com/kyilmaz/neurocomet/MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)
print("Complete")
