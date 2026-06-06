import re
with open("app/src/main/java/com/kyilmaz/neurocomet/MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()
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
text = re.sub(
    r'isBlocked \= \{ messagesViewModel\.isUserBlocked\(conv\.id\) \}',
    r'isUserBlocked = messagesState.blockedUserIds.contains(conv.participants.firstOrNull { it != messagesState.currentUserId } ?: conv.participants.firstOrNull { it != "me" } ?: "")',
    text
)
text = re.sub(
    r'isMuted \= \{ messagesViewModel\.isUserMuted\(conv\.id\) \}',
    r'isUserMuted = messagesState.mutedUserIds.contains(conv.participants.firstOrNull { it != messagesState.currentUserId } ?: conv.participants.firstOrNull { it != "me" } ?: "")',
    text
)
text = re.sub(
    r'posts \= feedState\.posts,',
    r'feedUiState = feedState,',
    text
)
text = re.sub(
    r'\s+stories \= feedState\.stories,\n',
    r'\n',
    text
)
text = re.sub(
    r'\s+currentUser \= CURRENT_USER\.copy\(isVerified \= isUserVerified\),\n',
    r'\n',
    text
)
text = re.sub(
    r'\s+isLoading \= feedState\.isLoading,\n',
    r'\n',
    text
)
text = re.sub(
    r'onAddPost \= \{ content: String, tone: String, imageUrl: String\?, videoUrl: String\? \->\s+feedViewModel\.createPost\(content, tone, imageUrl, videoUrl\)\s+\},',
    r'onAddPost = { content, tone, imageUrl, videoUrl, parentId, category -> feedViewModel.createPost(content, tone, imageUrl, videoUrl, parentId, category) },',
    text
)
text = re.sub(
    r'onAddStory \= \{ imageUrl, duration \-> feedViewModel\.createStory\(imageUrl, duration\) \},',
    r'onAddStory = { type, imageUrl, duration, overlay, bgColor, bgEnd, link -> feedViewModel.createStory(imageUrl, duration, type, overlay, bgColor, bgEnd, link) },',
    text
)
with open("app/src/main/java/com/kyilmaz/neurocomet/MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)
print("Finished python patch")
