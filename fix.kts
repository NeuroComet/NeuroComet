import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.text.Regex
val file = File("app/src/main/java/com/kyilmaz/neurocomet/MainActivity.kt")
var text = file.readText()
text = text.replace(
    Regex("isBlocked \\= \\{ messagesViewModel\\.isUserBlocked\\(it\\) \\}"),
    "isUserBlocked = messagesState.blockedUserIds.contains(activeConversation?.participants?.firstOrNull { it != messagesState.currentUserId } ?: activeConversation?.participants?.firstOrNull { it != \"me\" } ?: \"\")"
)
text = text.replace(
    Regex("isMuted \\= \\{ messagesViewModel\\.isUserMuted\\(it\\) \\}"),
    "isUserMuted = messagesState.mutedUserIds.contains(activeConversation?.participants?.firstOrNull { it != messagesState.currentUserId } ?: activeConversation?.participants?.firstOrNull { it != \"me\" } ?: \"\")"
)
text = text.replace(
    Regex("isBlocked \\= \\{ messagesViewModel\\.isUserBlocked\\(conv\\.id\\) \\}"),
    "isUserBlocked = messagesState.blockedUserIds.contains(conv.participants.firstOrNull { it != messagesState.currentUserId } ?: conv.participants.firstOrNull { it != \"me\" } ?: \"\")"
)
text = text.replace(
    Regex("isMuted \\= \\{ messagesViewModel\\.isUserMuted\\(conv\\.id\\) \\}"),
    "isUserMuted = messagesState.mutedUserIds.contains(conv.participants.firstOrNull { it != messagesState.currentUserId } ?: conv.participants.firstOrNull { it != \"me\" } ?: \"\")"
)
text = text.replace(
    Regex("posts \\= feedState\\.posts,"),
    "feedUiState = feedState,"
)
text = text.replace(
    Regex("\\s+stories \\= feedState\\.stories,\\n"),
    ""
)
text = text.replace(
    Regex("\\s+currentUser \\= CURRENT_USER\\.copy\\(isVerified \\= isUserVerified\\),\\n"),
    ""
)
text = text.replace(
    Regex("\\s+isLoading \\= feedState\\.isLoading,\\n"),
    ""
)
text = text.replace(
    Regex("onAddPost \\= \\{ content: String, tone: String, imageUrl: String\\?, videoUrl: String\\? \\->\\s+feedViewModel\\.createPost\\(content, tone, imageUrl, videoUrl\\)\\s+\\},"),
    "onAddPost = { content, tone, imageUrl, videoUrl, bgColor, locTag -> feedViewModel.createPost(content, tone, imageUrl, videoUrl, bgColor, locTag) },"
)
text = text.replace(
    Regex("onAddStory \\= \\{ imageUrl, duration \\-> feedViewModel\\.createStory\\(imageUrl, duration\\) \\},"),
    "onAddStory = { type, imageUrl, duration, overlay, bgColor, bgEnd, link -> feedViewModel.createStory(imageUrl, duration, type, overlay, bgColor, bgEnd, link) },"
)
file.writeText(text)
println("Replaced Kotlin")
