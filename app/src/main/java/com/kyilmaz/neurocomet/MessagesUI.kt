@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UNUSED_PARAMETER")

package com.kyilmaz.neurocomet

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import android.content.res.Configuration
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.time.Duration
import java.time.Instant
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.launch
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import android.widget.Toast

// =============================================================================
// NEURO-FRIENDLY MESSAGES UI - REBUILT FROM GROUND UP
// =============================================================================

// Quick reaction emojis for messages (like WhatsApp/Telegram/iMessage)
private val QUICK_REACTIONS = listOf("❤️", "👍", "😂", "😮", "😢", "🙏")

/**
 * Screen size categories for adaptive layouts
 */
private enum class ScreenSize {
    COMPACT,    // Phones in portrait (< 600dp)
    MEDIUM,     // Tablets in portrait, phones in landscape (600-840dp)
    EXPANDED    // Tablets in landscape (> 840dp)
}

@Composable
private fun rememberScreenSize(): ScreenSize {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    return when {
        screenWidthDp < 600 -> ScreenSize.COMPACT
        screenWidthDp < 840 -> ScreenSize.MEDIUM
        else -> ScreenSize.EXPANDED
    }
}

/**
 * Design tokens for the messages UI - Material/Android style
 * Adaptive based on screen size
 */
@Composable
private fun rememberMessagesDesign(): MessagesDesignTokens {
    val screenSize = rememberScreenSize()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    return remember(screenSize, isLandscape) {
        MessagesDesignTokens(
            touchTarget = when (screenSize) {
                ScreenSize.COMPACT -> 48.dp
                ScreenSize.MEDIUM -> 52.dp
                ScreenSize.EXPANDED -> 56.dp
            },
            avatarSize = when (screenSize) {
                ScreenSize.COMPACT -> 36.dp
                ScreenSize.MEDIUM -> 40.dp
                ScreenSize.EXPANDED -> 44.dp
            },
            avatarSizeLarge = when (screenSize) {
                ScreenSize.COMPACT -> 48.dp
                ScreenSize.MEDIUM -> 52.dp
                ScreenSize.EXPANDED -> 56.dp
            },
            bubbleMaxWidth = when (screenSize) {
                ScreenSize.COMPACT -> 280.dp
                ScreenSize.MEDIUM -> 400.dp
                ScreenSize.EXPANDED -> 500.dp
            },
            bubbleCornerRadius = 12.dp,
            composerCornerRadius = 28.dp,
            bubblePadding = when (screenSize) {
                ScreenSize.COMPACT -> 12.dp
                ScreenSize.MEDIUM -> 14.dp
                ScreenSize.EXPANDED -> 16.dp
            },
            itemSpacing = 4.dp,
            horizontalPadding = when (screenSize) {
                ScreenSize.COMPACT -> 8.dp
                ScreenSize.MEDIUM -> 16.dp
                ScreenSize.EXPANDED -> 24.dp
            },
            contentMaxWidth = when (screenSize) {
                ScreenSize.COMPACT -> null // Full width
                ScreenSize.MEDIUM -> 600.dp
                ScreenSize.EXPANDED -> 800.dp
            },
            isLandscape = isLandscape
        )
    }
}

private data class MessagesDesignTokens(
    val touchTarget: androidx.compose.ui.unit.Dp,
    val avatarSize: androidx.compose.ui.unit.Dp,
    val avatarSizeLarge: androidx.compose.ui.unit.Dp,
    val bubbleMaxWidth: androidx.compose.ui.unit.Dp,
    val bubbleCornerRadius: androidx.compose.ui.unit.Dp,
    val composerCornerRadius: androidx.compose.ui.unit.Dp,
    val bubblePadding: androidx.compose.ui.unit.Dp,
    val itemSpacing: androidx.compose.ui.unit.Dp,
    val horizontalPadding: androidx.compose.ui.unit.Dp,
    val contentMaxWidth: androidx.compose.ui.unit.Dp?,
    val isLandscape: Boolean
)

// Static fallback for non-composable contexts
private object MessagesDesign {
    val touchTarget = 48.dp
    val avatarSize = 36.dp
    val avatarSizeLarge = 48.dp
    val bubbleMaxWidth = 320.dp
    val bubbleCornerRadius = 12.dp
    val composerCornerRadius = 28.dp
    val bubblePadding = 12.dp
    val itemSpacing = 4.dp
    val horizontalPadding = 8.dp
}

/**
 * Debug settings for message bar (simplified - navbar padding handled automatically)
 */
object MessageBarDebug {
    var enabled by mutableStateOf(false)
    var surfaceElevation by mutableFloatStateOf(2f)
    var listBottomPadding by mutableFloatStateOf(0f)
}

// Neurocentric sensory modes
private enum class SensoryMode { CALM, FOCUS, STIM }

// Theme-aware bubble colors - pass isDark to avoid @Composable requirement
private fun outgoingBubbleColor(mode: SensoryMode, energy: Float, isDark: Boolean): Color = when (mode) {
    SensoryMode.CALM -> if (isDark) Color(0xFF4A90D9) else Color(0xFF2962FF)  // Blue
    SensoryMode.FOCUS -> if (isDark) Color(0xFF5CB8A5) else Color(0xFF00897B) // Teal
    SensoryMode.STIM -> if (isDark) Color(0xFF9575CD) else Color(0xFF7B1FA2)  // Purple
}

private fun incomingBubbleColor(isDark: Boolean): Color =
    if (isDark) Color(0xFF37474F) else Color(0xFFE0E0E0)  // Dark gray / Light gray

private fun bubbleTextColor(isFromMe: Boolean, isDark: Boolean): Color = when {
    isFromMe -> Color.White  // Outgoing always white (on colored background)
    isDark -> Color.White    // Incoming in dark mode
    else -> Color(0xFF212121) // Incoming in light mode
}

private fun laneAccent(mode: SensoryMode, isDark: Boolean): Color = when (mode) {
    SensoryMode.CALM -> if (isDark) Color(0xFFB3D4FF) else Color(0xFFE3F2FD)
    SensoryMode.FOCUS -> if (isDark) Color(0xFFC9F3E8) else Color(0xFFE0F2F1)
    SensoryMode.STIM -> if (isDark) Color(0xFFE4D1FF) else Color(0xFFF3E5F5)
}

private val sensoryModes = listOf(
    SensoryMode.CALM to "Calm",
    SensoryMode.FOCUS to "Focus",
    SensoryMode.STIM to "Stim"
)

// =============================================================================
// INBOX SCREEN - NeuroComet Unique Design
// =============================================================================

@Composable
fun NeuroInboxScreen(
    conversations: List<Conversation>,
    safetyState: SafetyState,
    onOpenConversation: (String) -> Unit,
    onStartNewChat: (userId: String) -> Unit = {},
    onBack: (() -> Unit)? = null,
    onOpenCallHistory: () -> Unit = {},
    onOpenPracticeCall: () -> Unit = {}
) {
    val context = LocalContext.current
    val parentalState = remember { ParentalControlsSettings.getState(context) }
    val restriction = shouldBlockFeature(parentalState, BlockableFeature.DMS)

    // State for new chat dialog
    var showNewChatDialog by remember { mutableStateOf(false) }

    if (restriction != null) {
        ParentalBlockedScreen(
            restrictionType = restriction,
            featureName = "Direct Messages"
        )
        return
    }

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Unread", "Groups", "Archived")

    val filteredConversations = remember(conversations, searchQuery, selectedFilter) {
        var result = conversations

        // Apply search filter
        if (searchQuery.isNotBlank()) {
            result = result.filter { conv ->
                val otherId = conv.participants.firstOrNull { it != "me" } ?: ""
                val user = MOCK_USERS.find { it.id == otherId }
                val text = "${user?.name ?: otherId} ${conv.messages.lastOrNull()?.content ?: ""}"
                text.contains(searchQuery, ignoreCase = true)
            }
        }

        // Apply category filter
        when (selectedFilter) {
            "All" -> result = result.filter { !it.isArchived }
            "Unread" -> result = result.filter { it.unreadCount > 0 && !it.isArchived }
            "Groups" -> result = result.filter { it.isGroup && !it.isArchived }
            "Archived" -> result = result.filter { it.isArchived }
        }

        result
    }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val unreadTotal = conversations.sumOf { it.unreadCount }
    val haptic = LocalHapticFeedback.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (isSearching) {
                // Search mode
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPadding()
                ) {
                    NeuroSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClose = {
                            isSearching = false
                            searchQuery = ""
                        }
                    )
                }
            } else {
                // Modern header matching Flutter design exactly
                MessagesHeader(
                    unreadCount = unreadTotal,
                    onNewMessage = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showNewChatDialog = true
                    },
                    onSearch = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        isSearching = true
                    },
                    onPracticeCall = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOpenPracticeCall()
                    },
                    isDark = isDark
                )
            }
        },
        floatingActionButton = {
            // Only show FAB when not using the header buttons
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chips matching Flutter style
            if (!isSearching) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    items(filters.size) { index ->
                        val filter = filters[index]
                        val isSelected = selectedFilter == filter
                        val count = when (filter) {
                            "Unread" -> unreadTotal
                            else -> null
                        }

                        MessagesFilterPill(
                            label = filter,
                            count = count,
                            isSelected = isSelected,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedFilter = filter
                            },
                            isDark = isDark
                        )
                    }
                }
            }

            if (filteredConversations.isEmpty()) {
                NeuroEmptyInboxState(
                    isSearchResult = searchQuery.isNotBlank(),
                    selectedFilter = selectedFilter,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredConversations, key = { it.id }) { conversation ->
                        ModernConversationListItem(
                            conversation = conversation,
                            onClick = { onOpenConversation(conversation.id) },
                            isDark = isDark
                        )
                    }

                    // Bottom spacing for navigation bar
                    item { Spacer(Modifier.height(100.dp)) }
                }
            }
        }
    }

    // New Chat Dialog
    if (showNewChatDialog) {
        NewChatDialog(
            existingConversations = conversations,
            onDismiss = { showNewChatDialog = false },
            onSelectUser = { userId ->
                showNewChatDialog = false
                onStartNewChat(userId)
            }
        )
    }
}

/**
 * Modern Messages header matching Flutter design - uses dynamic colors
 */
@Composable
private fun MessagesHeader(
    unreadCount: Int,
    onNewMessage: () -> Unit,
    onSearch: () -> Unit,
    onPracticeCall: () -> Unit,
    isDark: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title section
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.nav_messages),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (unreadCount > 0) {
                        Spacer(Modifier.width(12.dp))
                        UnreadBadge(count = unreadCount)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (unreadCount > 0) {
                        "You have $unreadCount unread ${if (unreadCount == 1) "message" else "messages"}"
                    } else {
                        "Your conversations ✨"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                MessagesHeaderIconButton(
                    icon = Icons.Outlined.Search,
                    onClick = onSearch,
                    contentDescription = stringResource(R.string.conversation_search),
                    isDark = isDark
                )
                MessagesHeaderIconButton(
                    icon = Icons.Outlined.Phone,
                    onClick = onPracticeCall,
                    contentDescription = stringResource(R.string.conversation_practice_calls),
                    isDark = isDark
                )
                MessagesHeaderIconButton(
                    icon = Icons.Outlined.Edit,
                    onClick = onNewMessage,
                    contentDescription = stringResource(R.string.conversation_new_message),
                    isPrimary = true,
                    isDark = isDark
                )
            }
        }
    }
}

/**
 * Animated unread badge - uses dynamic colors
 */
@Composable
private fun UnreadBadge(count: Int) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, tertiaryColor)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

/**
 * Header icon button - uses dynamic colors
 */
@Composable
private fun MessagesHeaderIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String?,
    isDark: Boolean,
    isPrimary: Boolean = false
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isPrimary) {
            primaryColor.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Box(
            modifier = Modifier.padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(22.dp),
                tint = if (isPrimary) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Filter pill - uses dynamic colors
 */
@Composable
private fun MessagesFilterPill(
    label: String,
    count: Int?,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) {
            primaryColor.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        border = if (isSelected) BorderStroke(1.5.dp, primaryColor.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = primaryColor
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (count != null && count > 0) {
                Box(
                    modifier = Modifier
                        .background(
                            color = primaryColor.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }
        }
    }
}

/**
 * Modern conversation list item - uses dynamic colors
 */
@Composable
private fun ModernConversationListItem(
    conversation: Conversation,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val successColor = Color(0xFF4CAF50)

    val hasUnread = conversation.unreadCount > 0
    val otherUserId = conversation.participants.firstOrNull { it != "me" } ?: ""
    val otherUser = MOCK_USERS.find { it.id == otherUserId }
    val displayName = if (conversation.isGroup) {
        conversation.groupName ?: "Group Chat"
    } else {
        otherUser?.name ?: otherUserId
    }
    val lastMessage = conversation.messages.lastOrNull()


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (hasUnread) {
            primaryColor.copy(alpha = 0.08f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with gradient ring for unread
            Box {
                if (hasUnread) {
                    // Gradient ring
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(primaryColor, tertiaryColor)
                                ),
                                shape = CircleShape
                            )
                    )
                }
                // Avatar
                Box(
                    modifier = Modifier
                        .padding(if (hasUnread) 2.dp else 0.dp)
                        .size(54.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                // Online indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = if (hasUnread) (-4).dp else (-2).dp, y = if (hasUnread) (-4).dp else (-2).dp)
                        .size(14.dp)
                        .background(successColor, CircleShape)
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.background,
                            CircleShape
                        )
                )
            }

            Spacer(Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    // Verified badge (if applicable)
                    if (otherUser?.name?.contains("Dr.") == true) {
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Verified",
                            modifier = Modifier.size(16.dp),
                            tint = primaryColor
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = lastMessage?.timestamp?.let { formatMessageTimeString(it) } ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (hasUnread) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (hasUnread) primaryColor else MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lastMessage?.content ?: "Start a conversation",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (hasUnread) FontWeight.Medium else FontWeight.Normal,
                        color = if (hasUnread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (hasUnread) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(primaryColor, tertiaryColor)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (conversation.unreadCount > 99) "99+" else conversation.unreadCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun NeuroSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Close search")
            }
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                placeholder = { Text(stringResource(R.string.dm_search_conversations_placeholder)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, "Clear")
                }
            }
        }
    }
}

/**
 * Dialog for starting a new chat with a user.
 * Requests contact access for syncing device contacts.
 */
@Composable
private fun NewChatDialog(
    existingConversations: List<Conversation>,
    onDismiss: () -> Unit,
    onSelectUser: (userId: String) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    // READ_CONTACTS is needed for syncing device contacts to find friends.
    // Note: The ContactsPickerSessionContract (CinnamonBun+) only covers the
    // picker UI — bulk contact reading for sync still requires READ_CONTACTS.
    var hasContactsPermission by remember {
        mutableStateOf(
            context.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    var showContactsPrompt by remember { mutableStateOf(!hasContactsPermission) }

    // Contact permission launcher
    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactsPermission = isGranted
        showContactsPrompt = false
    }

    // Get users who aren't already in conversations (or show all for simplicity)
    val availableUsers = remember(searchQuery) {
        MOCK_USERS.filter { user ->
            user.id != "me" && (
                searchQuery.isBlank() ||
                user.name.contains(searchQuery, ignoreCase = true) ||
                user.id.contains(searchQuery, ignoreCase = true)
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("💬", fontSize = 24.sp)
                Text(
                    text = stringResource(R.string.new_chat),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                // Contact access prompt (if not granted)
                AnimatedVisibility(visible = showContactsPrompt) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Contacts,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    stringResource(R.string.sync_contacts),
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                stringResource(R.string.allow_access_find_friends),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showContactsPrompt = false },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                ) {
                                    Text(stringResource(R.string.not_now), style = MaterialTheme.typography.labelMedium)
                                }
                                Button(
                                    onClick = {
                                        contactsPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.Check, null, Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(stringResource(R.string.allow), style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }

                // Contacts synced indicator (if permission granted)
                if (hasContactsPermission && !showContactsPrompt) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            stringResource(R.string.contacts_synced),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search_users)) },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Suggested Users",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                // User list
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (availableUsers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔍", fontSize = 32.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "No users found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        availableUsers.take(10).forEach { user ->
                            val hasExistingConversation = existingConversations.any { conv ->
                                conv.participants.contains(user.id)
                            }

                            NewChatUserItem(
                                user = user,
                                hasExistingChat = hasExistingConversation,
                                onClick = { onSelectUser(user.id) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun NewChatUserItem(
    user: User,
    hasExistingChat: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (hasExistingChat)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (user.isVerified) {
                        Icon(
                            Icons.Filled.Verified,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = "@${user.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Existing chat indicator or arrow
            if (hasExistingChat) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Open",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                Icon(
                    Icons.Filled.ArrowForward,
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onPracticeCall: () -> Unit,
    onCallHistory: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Practice Call Card
        Surface(
            onClick = onPracticeCall,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Headset,
                    null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Practice Call",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Safe space 🧘",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Call History Card
        Surface(
            onClick = onCallHistory,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.History,
                    null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Call History",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Recent calls",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NeuroEmptyInboxState(
    isSearchResult: Boolean,
    selectedFilter: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated emoji
            Text(
                text = when {
                    isSearchResult -> "🔍"
                    selectedFilter == "Unread" -> "✨"
                    selectedFilter == "Groups" -> "👥"
                    selectedFilter == "Archived" -> "📦"
                    else -> "💬"
                },
                fontSize = 64.sp
            )

            Text(
                text = when {
                    isSearchResult -> "No results found"
                    selectedFilter == "Unread" -> "All caught up!"
                    selectedFilter == "Groups" -> "No group chats yet"
                    selectedFilter == "Archived" -> "Nothing archived"
                    else -> "No conversations yet"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = when {
                    isSearchResult -> "Try a different search term"
                    selectedFilter != "All" -> "Change filters to see more"
                    else -> "Start a conversation with someone who gets you! 🧠✨"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NeuroConversationListItem(
    conversation: Conversation,
    onClick: () -> Unit,
    isDark: Boolean
) {

    val otherId = conversation.participants.firstOrNull { it != "me" } ?: return
    val user = MOCK_USERS.find { it.id == otherId }
    val avatar = user?.avatarUrl ?: avatarUrl(otherId)
    val name = user?.name ?: otherId
    val lastMessage = conversation.messages.lastOrNull()
    val hasUnread = conversation.unreadCount > 0
    val haptic = LocalHapticFeedback.current

    val timeAgo = remember(lastMessage?.timestamp) {
        formatTimeAgo(lastMessage?.timestamp)
    }

    // Swipe to archive/pin functionality placeholder
    var isPressed by remember { mutableStateOf(false) }
    var showContextMenu by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(),
        label = "scale"
    )

    Box {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .scale(scale)
            .combinedClickable(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showContextMenu = true
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (hasUnread) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        tonalElevation = if (hasUnread) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar with online indicator
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    contentScale = ContentScale.Crop
                )

                // Unread indicator badge
                if (hasUnread) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (conversation.unreadCount > 9) "9+" else "${conversation.unreadCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Time with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (hasUnread) {
                            Icon(
                                Icons.Filled.Circle,
                                null,
                                modifier = Modifier.size(8.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = timeAgo,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (hasUnread)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Message preview with delivery status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (lastMessage?.senderId == "me") {
                        Icon(
                            when (lastMessage.deliveryStatus) {
                                MessageDeliveryStatus.SENDING -> Icons.Outlined.Schedule
                                MessageDeliveryStatus.SENT -> Icons.Filled.Check
                                MessageDeliveryStatus.FAILED -> Icons.Filled.ErrorOutline
                            },
                            null,
                            modifier = Modifier.size(14.dp),
                            tint = when (lastMessage.deliveryStatus) {
                                MessageDeliveryStatus.FAILED -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    Text(
                        text = lastMessage?.content ?: "Start a conversation! 👋",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (hasUnread) FontWeight.Medium else FontWeight.Normal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

        // Context menu dropdown
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("📌  Pin conversation") },
                onClick = {
                    showContextMenu = false
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            DropdownMenuItem(
                text = { Text("🔇  Mute notifications") },
                onClick = {
                    showContextMenu = false
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            DropdownMenuItem(
                text = { Text("📦  Archive") },
                onClick = {
                    showContextMenu = false
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("🗑️  Delete", color = MaterialTheme.colorScheme.error) },
                onClick = {
                    showContextMenu = false
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
        }
    }
}

// =============================================================================
// CONVERSATION SCREEN - Google Messages + AOSP + Neurodivergent Design
// =============================================================================

/**
 * NeuroComet Messaging Screen
 *
 * A unique blend of:
 * - Google Messages: Material 3 design, smooth animations, modern feel
 * - AOSP Messages: Clean simplicity, efficient layout, no bloat
 * - Neurodivergent-friendly: Calm colors, clear hierarchy, reduced cognitive load
 * - Modern reactions: Long-press to react like WhatsApp/Telegram/iMessage
 */
@Composable
fun NeuroConversationScreen(
    conversation: Conversation,
    onBack: () -> Unit,
    onSend: (recipientId: String, content: String) -> Unit,
    onReport: (messageId: String) -> Unit,
    onRetryMessage: (convId: String, msgId: String) -> Unit,
    onReactToMessage: (messageId: String, emoji: String) -> Unit = { _, _ -> },
    isBlocked: (String) -> Boolean,
    isMuted: (String) -> Boolean,
    onBlockUser: (String) -> Unit = {},
    onUnblockUser: (String) -> Unit = {},
    enableVideoChat: Boolean = false
) {
    val recipientId = conversation.participants.firstOrNull { it != "me" } ?: return
    val user = MOCK_USERS.find { it.id == recipientId }
    val avatar = user?.avatarUrl ?: avatarUrl(recipientId)
    val displayName = user?.name ?: recipientId

    val isUserBlocked = isBlocked(recipientId)
    val isUserMuted = isMuted(recipientId)

    // Detect dark mode for theme-aware colors
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var messageText by remember { mutableStateOf("") }
    var showEmojiPanel by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showAttachmentPicker by remember { mutableStateOf(false) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableLongStateOf(0L) }
    var pendingAttachment by remember { mutableStateOf<MessageAttachment?>(null) }
    val context = LocalContext.current

    // Typing indicator state for simulated replies
    var isOtherTyping by remember { mutableStateOf(false) }
    var lastSentMessageCount by remember { mutableIntStateOf(conversation.messages.count { it.senderId == "me" }) }

    // Simulated auto-reply: when user sends a message, simulate a typing delay then reply
    LaunchedEffect(conversation.messages.size) {
        val currentSentCount = conversation.messages.count { it.senderId == "me" }
        if (currentSentCount > lastSentMessageCount && currentSentCount > 0) {
            lastSentMessageCount = currentSentCount
            // Wait a bit then show typing indicator
            kotlinx.coroutines.delay(800L)
            isOtherTyping = true
            // Simulate typing for 1.5-3 seconds
            kotlinx.coroutines.delay((1500L..3000L).random())
            isOtherTyping = false
            // Send a simulated reply
            val lastMsg = conversation.messages.lastOrNull { it.senderId == "me" }?.content ?: ""
            val reply = generateSimulatedReply(displayName, lastMsg)
            onSend(recipientId, reply)
        }
    }

    // Call state
    var showCallDialog by remember { mutableStateOf(false) }
    val currentCall = MockCallManager.currentCall
    val callState = MockCallManager.callState
    val callDuration = MockCallManager.callDuration

    // Voice recorder
    val voiceRecorder = remember { VoiceRecorder(context) }

    // Attachment state with handlers
    val attachmentState = rememberAttachmentState { attachment ->
        pendingAttachment = attachment
        showAttachmentPicker = false
        Toast.makeText(
            context,
            "${attachment.type}: ${attachment.displayName}",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Recording timer effect
    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            while (isRecordingVoice) {
                recordingDuration = voiceRecorder.getCurrentDuration()
                kotlinx.coroutines.delay(100)
            }
        } else {
            recordingDuration = 0
        }
    }

    // Keyboard visibility detection
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val isKeyboardVisible = imeBottom > 0

    // Neurodivergent color palette - calming gradients
    val primaryGradient = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary
    )

    // Auto-hide emoji when keyboard shows
    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible && showEmojiPanel) {
            showEmojiPanel = false
        }
    }

    // Scroll to bottom on new messages or typing indicator
    LaunchedEffect(conversation.messages.size, isOtherTyping) {
        val itemCount = conversation.messages.size + (if (isOtherTyping) 1 else 0)
        if (itemCount > 0) {
            listState.animateScrollToItem(itemCount - 1)
        }
    }

    // Show scroll-to-bottom button logic
    val showScrollButton by remember {
        derivedStateOf {
            val total = conversation.messages.size
            if (total <= 2) false
            else {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisible < total - 2
            }
        }
    }

    // Input bar color
    val inputBarColor = if (isUserBlocked)
        MaterialTheme.colorScheme.errorContainer
    else
        MaterialTheme.colorScheme.surfaceContainer

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            // AOSP-inspired minimal header with Google Messages polish
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button - AOSP style
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.conversation_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Avatar with neurodivergent-friendly gradient ring
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(primaryGradient),
                                shape = CircleShape
                            )
                            .padding(3.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(avatar)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    // User info - clean AOSP layout
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Status row with visual indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Status dot
                            val statusColor = when {
                                isUserBlocked -> MaterialTheme.colorScheme.error
                                isUserMuted -> MaterialTheme.colorScheme.outline
                                isOtherTyping -> MaterialTheme.colorScheme.primary
                                else -> Color(0xFF4CAF50) // Online green
                            }
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(statusColor, CircleShape)
                            )
                            Text(
                                text = when {
                                    isUserBlocked -> stringResource(R.string.status_blocked)
                                    isUserMuted -> stringResource(R.string.status_muted)
                                    isOtherTyping -> "typing..."
                                    else -> stringResource(R.string.status_online)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Action buttons - Google Messages style (gated by enableVideoChat flag)
                    if (enableVideoChat) {
                        IconButton(onClick = {
                            MockCallManager.startCall(
                                recipientId = recipientId,
                                recipientName = displayName,
                                recipientAvatar = avatar,
                                callType = CallType.VIDEO
                            )
                            showCallDialog = true
                        }) {
                            Icon(
                            Icons.Filled.Videocam,
                            contentDescription = stringResource(R.string.conversation_video_call),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(onClick = {
                            MockCallManager.startCall(
                                recipientId = recipientId,
                                recipientName = displayName,
                                recipientAvatar = avatar,
                                callType = CallType.VOICE
                            )
                            showCallDialog = true
                        }) {
                            Icon(
                            Icons.Filled.Call,
                            contentDescription = stringResource(R.string.conversation_voice_call),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Overflow menu
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Filled.MoreVert,
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("View profile") },
                                onClick = { showMenu = false },
                                leadingIcon = { Icon(Icons.Filled.Person, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Search") },
                                onClick = { showMenu = false },
                                leadingIcon = { Icon(Icons.Filled.Search, null) }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(if (isUserBlocked) "Unblock" else "Block") },
                                onClick = { showMenu = false },
                                leadingIcon = { Icon(Icons.Filled.Block, null) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isUserMuted) "Unmute" else "Mute") },
                                onClick = { showMenu = false },
                                leadingIcon = {
                                    Icon(
                                        if (isUserMuted) Icons.AutoMirrored.Filled.VolumeUp
                                        else Icons.AutoMirrored.Filled.VolumeOff,
                                        null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Google Messages-style floating input with neurodivergent enhancements
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = inputBarColor,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    if (isUserBlocked) {
                        // Blocked state - calming design
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Filled.Block,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Messaging paused",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    "Unblock to resume conversation",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                                )
                            }
                            FilledTonalButton(onClick = { onUnblockUser(recipientId) }) {
                                Text("Unblock")
                            }
                        }
                    } else {
                        // Pending attachment preview
                        pendingAttachment?.let { attachment ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        when (attachment.type) {
                                            AttachmentType.IMAGE -> Icons.Filled.Image
                                            AttachmentType.VIDEO -> Icons.Filled.Videocam
                                            AttachmentType.DOCUMENT -> Icons.Outlined.Description
                                            AttachmentType.AUDIO -> Icons.Filled.Headphones
                                            AttachmentType.LOCATION -> Icons.Filled.LocationOn
                                            AttachmentType.CONTACT -> Icons.Filled.Person
                                            AttachmentType.VOICE_MESSAGE -> Icons.Filled.Mic
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        attachment.displayName.ifEmpty { attachment.type.name },
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    IconButton(
                                        onClick = { pendingAttachment = null },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = "Remove attachment",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Voice recording indicator
                        if (isRecordingVoice) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Pulsing recording indicator
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(
                                                MaterialTheme.colorScheme.error,
                                                CircleShape
                                            )
                                    )
                                    Text(
                                        "Recording...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        formatRecordingDuration(recordingDuration),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    IconButton(
                                        onClick = {
                                            voiceRecorder.cancelRecording()
                                            isRecordingVoice = false
                                            Toast.makeText(context, "Recording cancelled", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = "Cancel recording",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Attachment picker with smooth animation
                        AnimatedVisibility(
                            visible = showAttachmentPicker && !isRecordingVoice,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                        ) {
                            NeuroAttachmentPicker(
                                attachmentState = attachmentState,
                                onDismiss = { showAttachmentPicker = false }
                            )
                        }

                        // Emoji picker with smooth animation
                        AnimatedVisibility(
                            visible = showEmojiPanel,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                        ) {
                            NeuroEmojiPanel(
                                onEmojiSelected = { messageText += it },
                                onDismiss = { showEmojiPanel = false }
                            )
                        }

                        // Input row - blend of Google Messages & AOSP
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Attachment button - Google Messages style
                            Box(
                                modifier = Modifier.size(44.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(
                                    onClick = { showAttachmentPicker = !showAttachmentPicker },
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        if (showAttachmentPicker) Icons.Filled.Close else Icons.Filled.Add,
                                        contentDescription = if (showAttachmentPicker) "Close attachments" else "Add attachment",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // Text field - AOSP minimal with Google polish
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 44.dp, max = 120.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Emoji toggle
                                    IconButton(
                                        onClick = {
                                            if (showEmojiPanel) {
                                                showEmojiPanel = false
                                                keyboardController?.show()
                                            } else {
                                                keyboardController?.hide()
                                                showEmojiPanel = true
                                            }
                                        },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            if (showEmojiPanel) Icons.Filled.Keyboard else Icons.Filled.EmojiEmotions,
                                            contentDescription = if (showEmojiPanel) "Keyboard" else "Emoji",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    // Text input
                                    TextField(
                                        value = messageText,
                                        onValueChange = { messageText = it },
                                        modifier = Modifier.weight(1f),
                                        placeholder = { Text(stringResource(R.string.dm_message_placeholder)) },
                                        colors = TextFieldDefaults.colors(
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent
                                        ),
                                        maxLines = 4,
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Sentences,
                                            imeAction = ImeAction.Send
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onSend = {
                                                val text = messageText.trim()
                                                if (text.isNotEmpty()) {
                                                    onSend(recipientId, text)
                                                    messageText = ""
                                                    showEmojiPanel = false
                                                }
                                            }
                                        )
                                    )

                                    // Camera button when empty and not recording
                                    if (messageText.isEmpty() && !isRecordingVoice) {
                                        IconButton(
                                            onClick = { attachmentState.onTakePhoto() },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                Icons.Filled.CameraAlt,
                                                contentDescription = "Take photo",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Send/Mic button with animation
                            val hasText = messageText.isNotBlank()
                            val hasAttachment = pendingAttachment != null

                            Box(
                                modifier = Modifier.size(44.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                FilledIconButton(
                                    onClick = {
                                        when {
                                            hasText || hasAttachment -> {
                                                // Send message with optional attachment
                                                val text = messageText.trim()
                                                if (text.isNotEmpty() || hasAttachment) {
                                                    // Include attachment info in message if present
                                                    val finalMessage = if (hasAttachment && text.isEmpty()) {
                                                        "[${pendingAttachment?.type?.name}: ${pendingAttachment?.displayName}]"
                                                    } else if (hasAttachment) {
                                                        "$text\n[${pendingAttachment?.type?.name}: ${pendingAttachment?.displayName}]"
                                                    } else {
                                                        text
                                                    }
                                                    onSend(recipientId, finalMessage)
                                                    messageText = ""
                                                    pendingAttachment = null
                                                    showEmojiPanel = false
                                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                }
                                            }
                                            isRecordingVoice -> {
                                                // Stop recording and send voice message
                                                val voiceMessage = voiceRecorder.stopRecording()
                                                isRecordingVoice = false
                                                if (voiceMessage != null) {
                                                    // Send voice message
                                                    onSend(recipientId, "[Voice message: ${voiceMessage.durationFormatted}]")
                                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    Toast.makeText(
                                                        context,
                                                        "Voice message sent (${voiceMessage.durationFormatted})",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Recording failed",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                            else -> {
                                                // Start voice recording
                                                if (attachmentState.hasAudioPermission()) {
                                                    if (voiceRecorder.startRecording()) {
                                                        isRecordingVoice = true
                                                        showAttachmentPicker = false
                                                        showEmojiPanel = false
                                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to start recording",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                } else {
                                                    attachmentState.onRequestAudioPermission()
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(44.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = when {
                                            hasText || hasAttachment -> MaterialTheme.colorScheme.primary
                                            isRecordingVoice -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.surfaceContainerHighest
                                        },
                                        contentColor = when {
                                            hasText || hasAttachment -> MaterialTheme.colorScheme.onPrimary
                                            isRecordingVoice -> MaterialTheme.colorScheme.onError
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                ) {
                                    Icon(
                                        when {
                                            hasText || hasAttachment -> Icons.AutoMirrored.Filled.Send
                                            isRecordingVoice -> Icons.Filled.Stop
                                            else -> Icons.Filled.Mic
                                        },
                                        contentDescription = when {
                                            hasText || hasAttachment -> "Send message"
                                            isRecordingVoice -> "Stop recording"
                                            else -> "Record voice message"
                                        },
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
        ) {
            if (conversation.messages.isEmpty()) {
                // Empty state - neurodivergent calming design
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Infinity-inspired icon container
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                ),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Forum,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Start chatting with $displayName",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Messages are end-to-end encrypted. Say hi! 👋",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(24.dp))

                    // Quick action suggestions - neurodivergent friendly
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("👋 Hi!", "✨ Hello!", "💜 Hey there!").forEach { suggestion ->
                            SuggestionChip(
                                onClick = {
                                    onSend(recipientId, suggestion.substringAfter(" "))
                                },
                                label = { Text(suggestion) }
                            )
                        }
                    }
                }
            } else {
                // Message list - AOSP efficiency with Google polish
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(conversation.messages, key = { it.id }) { message ->
                        val isFromMe = message.senderId == "me"

                        NeuroMessageItem(
                            message = message,
                            isFromMe = isFromMe,
                            isDark = isDark,
                            onReport = { onReport(message.id) },
                            onRetry = { onRetryMessage(conversation.id, message.id) },
                            onReact = { emoji -> onReactToMessage(message.id, emoji) }
                        )
                    }

                    // Typing indicator
                    if (isOtherTyping) {
                        item(key = "typing_indicator") {
                            TypingIndicatorBubble(
                                displayName = displayName,
                                isDark = isDark
                            )
                        }
                    }
                }

                // Scroll to bottom FAB - Google Messages style
                AnimatedVisibility(
                    visible = showScrollButton,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(conversation.messages.size - 1)
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Scroll to bottom"
                        )
                    }
                }
            }
        }
    }

    // Call Dialog
    if (showCallDialog && currentCall != null) {
        CallDialog(
            call = currentCall,
            callState = callState,
            callDuration = callDuration,
            onDismiss = { showCallDialog = false }
        )
    }
}

/**
 * Neurodivergent-friendly emoji panel.
 */
@Composable
private fun NeuroEmojiPanel(
    onEmojiSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val recentEmojis = listOf("👍", "❤️", "😊", "🙌", "😂", "🔥", "✨", "💜", "🎉", "👏")
    val smileyEmojis = listOf("😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "😊", "😇", "🥰", "😍", "🤩", "😘")
    val gestureEmojis = listOf("👍", "👎", "👌", "✌️", "🤞", "🤟", "🤘", "👋", "🤚", "✋", "🖐️", "👏", "🙌", "🤝", "🙏")
    val heartEmojis = listOf("❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💕", "💞", "💓", "💗", "💖", "💝")
    val objectEmojis = listOf("🎉", "🎊", "🎁", "🎈", "🔥", "⭐", "🌟", "✨", "💫", "🌈", "☀️", "🌙", "💡", "🎵", "🎶")
    val animalEmojis = listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Recent emojis section
            item {
                Text(
                    "Recent",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                EmojiRow(emojis = recentEmojis, onEmojiSelected = onEmojiSelected)
            }

            // Smileys section
            item {
                Text(
                    "Smileys",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                EmojiRow(emojis = smileyEmojis, onEmojiSelected = onEmojiSelected)
            }

            // Gestures section
            item {
                Text(
                    "Gestures",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                EmojiRow(emojis = gestureEmojis, onEmojiSelected = onEmojiSelected)
            }

            // Hearts section
            item {
                Text(
                    "Hearts",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                EmojiRow(emojis = heartEmojis, onEmojiSelected = onEmojiSelected)
            }

            // Objects section
            item {
                Text(
                    "Objects",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                EmojiRow(emojis = objectEmojis, onEmojiSelected = onEmojiSelected)
            }

            // Animals section
            item {
                Text(
                    "Animals",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                EmojiRow(emojis = animalEmojis, onEmojiSelected = onEmojiSelected)
            }
        }
    }
}

/**
 * Horizontal scrolling row of emojis.
 */
@Composable
private fun EmojiRow(
    emojis: List<String>,
    onEmojiSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(emojis) { emoji ->
            EmojiButton(emoji = emoji, onClick = { onEmojiSelected(emoji) })
        }
    }
}

/**
 * Individual emoji button with proper rendering.
 */
@Composable
private fun EmojiButton(
    emoji: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

/**
 * Neurodivergent-friendly attachment picker.
 * Shows options for photos, camera, files, location, etc.
 */
@Composable
private fun NeuroAttachmentPicker(
    attachmentState: AttachmentState,
    onDismiss: () -> Unit
) {
    data class AttachmentOption(
        val icon: ImageVector,
        val label: String,
        val color: Color,
        val onClick: () -> Unit
    )

    val options = listOf(
        AttachmentOption(Icons.Filled.Image, "Gallery", Color(0xFF4CAF50)) {
            attachmentState.onPickImage()
            onDismiss()
        },
        AttachmentOption(Icons.Filled.CameraAlt, "Camera", Color(0xFF2196F3)) {
            attachmentState.onTakePhoto()
            onDismiss()
        },
        AttachmentOption(Icons.Outlined.Description, "Document", Color(0xFFFF9800)) {
            attachmentState.onPickDocument()
            onDismiss()
        },
        AttachmentOption(Icons.Filled.LocationOn, "Location", Color(0xFFE91E63)) {
            attachmentState.onShareLocation()
            onDismiss()
        },
        AttachmentOption(Icons.Filled.Person, "Contact", Color(0xFF9C27B0)) {
            attachmentState.onPickContact()
            onDismiss()
        },
        AttachmentOption(Icons.Filled.Headphones, "Audio", Color(0xFF00BCD4)) {
            attachmentState.onPickAudio()
            onDismiss()
        }
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Share",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(options.size) { index ->
                    val option = options[index]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { option.onClick() }
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = option.color.copy(alpha = 0.15f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    option.icon,
                                    contentDescription = option.label,
                                    tint = option.color,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            option.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Format recording duration for display.
 */
private fun formatRecordingDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000) % 60
    val minutes = (durationMs / 1000) / 60
    return String.format(java.util.Locale.getDefault(), "%d:%02d", minutes, seconds)
}

/**
 * Neurodivergent-friendly message bubble with reactions.
 * Long-press to add reactions like WhatsApp/Telegram/iMessage.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NeuroMessageItem(
    message: DirectMessage,
    isFromMe: Boolean,
    isDark: Boolean,
    onReport: () -> Unit,
    onRetry: () -> Unit,
    onReact: (emoji: String) -> Unit = {}
) {
    val hapticFeedback = LocalHapticFeedback.current
    var showReactionPicker by remember { mutableStateOf(false) }

    val bubbleColor = if (isFromMe) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val textColor = if (isFromMe) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    // Get font settings from theme
    val fontSettings = LocalFontSettings.current

    // Get grouped reactions for display
    val groupedReactions = remember(message.reactions) {
        message.getGroupedReactions()
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start
        ) {
            // Message bubble with long-press for reactions
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isFromMe) 16.dp else 4.dp,
                    bottomEnd = if (isFromMe) 4.dp else 16.dp
                ),
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .combinedClickable(
                        onClick = { /* Normal tap - could open message options */ },
                        onLongClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            showReactionPicker = true
                        }
                    )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.content,
                        style = NeuroDivergentTypography.messageBody(fontSettings),
                        color = textColor
                    )

                    Spacer(Modifier.height(4.dp))

                    // Timestamp row with delivery status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = formatMessageTimeString(message.timestamp),
                            style = NeuroDivergentTypography.timestamp(fontSettings),
                            color = textColor.copy(alpha = 0.6f)
                        )
                        if (isFromMe) {
                            when (message.deliveryStatus) {
                                MessageDeliveryStatus.SENDING -> Icon(
                                    Icons.Filled.Schedule,
                                    contentDescription = "Sending",
                                    modifier = Modifier.size(12.dp),
                                    tint = textColor.copy(alpha = 0.6f)
                                )
                                MessageDeliveryStatus.SENT -> Icon(
                                    Icons.Filled.Done,
                                    contentDescription = "Sent",
                                    modifier = Modifier.size(12.dp),
                                    tint = textColor.copy(alpha = 0.6f)
                                )
                                MessageDeliveryStatus.FAILED -> Icon(
                                    Icons.Filled.ErrorOutline,
                                    contentDescription = "Failed",
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clickable { onRetry() },
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // Reactions display (like iMessage/WhatsApp - shown below the bubble)
            if (groupedReactions.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                MessageReactionsRow(
                    reactions = groupedReactions,
                    isFromMe = isFromMe,
                    onReactionClick = { emoji -> onReact(emoji) }
                )
            }
        }

        // Reaction picker popup (appears above the message like iMessage)
        if (showReactionPicker) {
            ReactionPickerPopup(
                isFromMe = isFromMe,
                onReactionSelected = { emoji ->
                    onReact(emoji)
                    showReactionPicker = false
                },
                onDismiss = { showReactionPicker = false }
            )
        }
    }
}

/**
 * Display reactions on a message bubble (like WhatsApp/iMessage style).
 */
@Composable
private fun MessageReactionsRow(
    reactions: Map<String, List<String>>,
    isFromMe: Boolean,
    onReactionClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .offset(x = if (isFromMe) (-8).dp else 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        reactions.forEach { (emoji, users) ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onReactionClick(emoji) }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 14.sp
                )
                if (users.size > 1) {
                    Text(
                        text = users.size.toString(),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Reaction picker popup that appears on long-press (like iMessage/WhatsApp/Telegram).
 *
 * Neurodivergent-friendly features:
 * - Gentle, predictable animations that don't cause sensory overload
 * - Staggered entrance for each reaction (visually satisfying)
 * - Clear haptic feedback on selection
 * - Optional full emoji picker via plus button
 */
@Composable
private fun ReactionPickerPopup(
    isFromMe: Boolean,
    onReactionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var showFullPicker by remember { mutableStateOf(false) }

    // Staggered animation for each reaction
    var animationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animationStarted = true
    }

    // Main container animation - gentle scale + fade
    val containerScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "popup-scale"
    )

    val containerAlpha by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "popup-alpha"
    )

    Popup(
        alignment = if (isFromMe) Alignment.TopEnd else Alignment.TopStart,
        offset = IntOffset(0, -120),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Column {
            // Main reaction bar
            Surface(
                modifier = Modifier
                    .scale(containerScale)
                    .graphicsLayer { alpha = containerAlpha }
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    QUICK_REACTIONS.forEachIndexed { index, emoji ->
                        // Staggered animation for each reaction button
                        val delayMs = index * 40 // 40ms delay between each
                        var buttonVisible by remember { mutableStateOf(false) }

                        LaunchedEffect(animationStarted) {
                            if (animationStarted) {
                                kotlinx.coroutines.delay(delayMs.toLong())
                                buttonVisible = true
                            }
                        }

                        val buttonScale by animateFloatAsState(
                            targetValue = if (buttonVisible) 1f else 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "button-scale-$index"
                        )

                        ReactionButton(
                            emoji = emoji,
                            scale = buttonScale,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onReactionSelected(emoji)
                            }
                        )
                    }

                    // "More" button to show full emoji picker
                    var moreButtonVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(animationStarted) {
                        if (animationStarted) {
                            kotlinx.coroutines.delay((QUICK_REACTIONS.size * 40 + 50).toLong())
                            moreButtonVisible = true
                        }
                    }

                    val moreScale by animateFloatAsState(
                        targetValue = if (moreButtonVisible) 1f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "more-scale"
                    )

                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showFullPicker = !showFullPicker
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .scale(moreScale)
                    ) {
                        Icon(
                            if (showFullPicker) Icons.Filled.Close else Icons.Filled.Add,
                            contentDescription = if (showFullPicker) "Close picker" else "More reactions",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Full emoji picker (expands below when plus is tapped)
            AnimatedVisibility(
                visible = showFullPicker,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                FullEmojiPicker(
                    onEmojiSelected = { emoji ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onReactionSelected(emoji)
                    }
                )
            }
        }
    }
}

/**
 * Full emoji picker with categorized emojis for more reaction options.
 */
@Composable
private fun FullEmojiPicker(
    onEmojiSelected: (String) -> Unit
) {
    val emojiCategories = listOf(
        "Smileys" to listOf("😀", "😃", "😄", "😁", "😅", "😂", "🤣", "😊", "😇", "🙂", "😉", "😌", "😍", "🥰", "😘"),
        "Emotions" to listOf("❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "💔", "💕", "💖", "💗", "💝", "💞", "💟"),
        "Gestures" to listOf("👍", "👎", "👏", "🙌", "👐", "🤲", "🤝", "🙏", "✌️", "🤞", "🤟", "🤘", "👌", "🤌", "💪"),
        "Nature" to listOf("🌸", "🌺", "🌻", "🌷", "🌹", "🌼", "💐", "🌿", "🍀", "🌈", "⭐", "✨", "🌙", "☀️", "🔥"),
        "Neurodivergent" to listOf("♾️", "🧠", "💜", "🦋", "🌈", "🎨", "🎵", "📚", "🧩", "💡", "🌟", "🦄", "🐸", "🦦", "🐢")
    )

    var selectedCategory by remember { mutableIntStateOf(0) }

    Surface(
        modifier = Modifier
            .padding(top = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .padding(8.dp)
        ) {
            // Category tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                emojiCategories.forEachIndexed { index, (name, _) ->
                    FilterChip(
                        selected = selectedCategory == index,
                        onClick = { selectedCategory = index },
                        label = {
                            Text(
                                name,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Emoji grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.height(150.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(emojiCategories[selectedCategory].second) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

/**
 * Individual reaction button with scale animation on press.
 */
@Composable
private fun ReactionButton(
    emoji: String,
    scale: Float = 1f,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 1.3f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "reaction-press-scale"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(scale * pressScale)
            .clip(CircleShape)
            .clickable {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 22.sp
        )
    }

    // Reset pressed state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

/**
 * Format message timestamp for display.
 */
private fun formatMessageTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> "${diff / 86_400_000}d ago"
    }
}

/**
 * Format message timestamp string for display.
 * Handles ISO format or falls back to showing the raw string.
 */
private fun formatMessageTimeString(timestamp: String): String {
    return try {
        val instant = Instant.parse(timestamp)
        val now = Instant.now()
        val diff = Duration.between(instant, now).toMillis()
        when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000}m ago"
            diff < 86_400_000 -> "${diff / 3_600_000}h ago"
            else -> "${diff / 86_400_000}d ago"
        }
    } catch (e: Exception) {
        // Fallback: return as-is or extract time portion
        timestamp.substringAfter("T").substringBefore("Z").take(5)
    }
}

// =============================================================================
// HELPER FUNCTIONS AND OLD COMPONENTS (kept for compatibility)
// =============================================================================

@Composable
private fun ConversationTopBar(
    displayName: String,
    avatar: String,
    isBlocked: Boolean,
    isMuted: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val design = rememberMessagesDesign()
    var showOptionsMenu by remember { mutableStateOf(false) }
    val topBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(design.avatarSize)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isBlocked || isMuted) {
                        Text(
                            text = if (isBlocked) "Blocked" else "Muted",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isBlocked)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
        },
        actions = {
            Box {
                IconButton(onClick = { showOptionsMenu = true }) {
                    Icon(Icons.Outlined.MoreVert, "More options")
                }
                DropdownMenu(
                    expanded = showOptionsMenu,
                    onDismissRequest = { showOptionsMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("View Profile") },
                        onClick = {
                            showOptionsMenu = false
                            Toast.makeText(context, "Opening $displayName's profile...", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = { Icon(Icons.Outlined.Person, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Search in Conversation") },
                        onClick = {
                            showOptionsMenu = false
                            Toast.makeText(context, "Search opened in conversation view", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = { Icon(Icons.Outlined.Search, null) }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isMuted) "Unmute Notifications" else "Mute Notifications") },
                        onClick = {
                            showOptionsMenu = false
                            Toast.makeText(context, if (isMuted) "Notifications unmuted" else "Notifications muted", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = { Icon(if (isMuted) Icons.Outlined.NotificationsActive else Icons.Outlined.NotificationsOff, null) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Clear Chat History") },
                        onClick = {
                            showOptionsMenu = false
                            Toast.makeText(context, "Chat history cleared", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = { Icon(Icons.Outlined.DeleteSweep, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Export Conversation") },
                        onClick = {
                            showOptionsMenu = false
                            // Build a text export of the conversation
                            val exportText = buildString {
                                appendLine("NeuroComet Conversation with $displayName")
                                appendLine("Exported: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
                                appendLine("─".repeat(40))
                            }
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, exportText)
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "Conversation with $displayName")
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Export Conversation"))
                        },
                        leadingIcon = { Icon(Icons.Outlined.Download, null) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (isBlocked) "Unblock User" else "Block User",
                                color = if (!isBlocked) MaterialTheme.colorScheme.error else LocalContentColor.current
                            )
                        },
                        onClick = {
                            showOptionsMenu = false
                            Toast.makeText(context, if (isBlocked) "User unblocked" else "User blocked", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = {
                            Icon(
                                if (isBlocked) Icons.Outlined.PersonAdd else Icons.Outlined.Block,
                                null,
                                tint = if (!isBlocked) MaterialTheme.colorScheme.error else LocalContentColor.current
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Report User", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showOptionsMenu = false
                            Toast.makeText(context, "Report submitted. Thank you!", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = { Icon(Icons.Outlined.Flag, null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        },
        colors = topBarColors
    )
}

@Composable
private fun ConversationBottomBar(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    showEmojiPanel: Boolean,
    onToggleEmojiPanel: () -> Unit,
    onSend: () -> Unit,
    onEmojiSelected: (String) -> Unit,
    isBlocked: Boolean,
    sensoryMode: SensoryMode,
    energy: Float,
    onEnergyChange: (Float) -> Unit,
    onModeChange: (SensoryMode) -> Unit
) {
    val screenSize = rememberScreenSize()
    val design = rememberMessagesDesign()

    // Center content on larger screens
    val contentModifier = if (design.contentMaxWidth != null) {
        Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = design.contentMaxWidth)
    } else {
        Modifier.fillMaxWidth()
    }

    // Get the navigation bar height to extend the surface behind it
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val navBarHeight = navBarPadding.calculateBottomPadding()

    // Handle IME padding here
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        // Main content - Surface extends behind navbar for seamless blend
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = MessageBarDebug.surfaceElevation.coerceAtLeast(0f).dp
        ) {
            Column(modifier = contentModifier) {
                if (isBlocked) {
                    BlockedUserNotice()
                } else {
                    // Sensory controls - hide in landscape on compact screens
                    if (!(design.isLandscape && screenSize == ScreenSize.COMPACT)) {
                        SensoryModeSwitcher(selected = sensoryMode, onSelect = onModeChange)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Calm", style = MaterialTheme.typography.labelSmall)
                            Slider(
                                value = energy,
                                onValueChange = onEnergyChange,
                                valueRange = 0f..1f,
                                modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                            )
                            Text("Stim", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Emoji panel
                    AnimatedVisibility(
                        visible = showEmojiPanel,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut()
                    ) {
                        QuickEmojiPanel(
                            onEmojiSelected = onEmojiSelected,
                            onDismiss = onToggleEmojiPanel
                        )
                    }

                    // Composer
                    MessageComposer(
                        text = messageText,
                        onTextChange = onMessageTextChange,
                        showEmojiPanel = showEmojiPanel,
                        onToggleEmoji = onToggleEmojiPanel,
                        onSend = onSend,
                        design = design
                    )

                    // Spacer that extends behind the navigation bar - same color as Surface
                    Spacer(modifier = Modifier.height(navBarHeight))
                }
            }
        }
    }
}


@Composable
private fun BlockedUserNotice() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Filled.Block,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Column {
                Text(
                    "User blocked",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    "Unblock to send messages",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun MessageComposer(
    text: String,
    onTextChange: (String) -> Unit,
    showEmojiPanel: Boolean,
    onToggleEmoji: () -> Unit,
    onSend: () -> Unit,
    design: MessagesDesignTokens = rememberMessagesDesign()
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = design.horizontalPadding, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Emoji button
            IconButton(
                onClick = onToggleEmoji,
                modifier = Modifier.size(design.touchTarget)
            ) {
                Icon(
                    if (showEmojiPanel) Icons.Filled.Keyboard else Icons.Outlined.EmojiEmotions,
                    contentDescription = if (showEmojiPanel) "Show keyboard" else "Show emoji",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Text field
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.dm_message_placeholder)) },
                maxLines = if (design.isLandscape) 2 else 4,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                shape = RoundedCornerShape(design.composerCornerRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Send button
            AnimatedVisibility(
                visible = text.trim().isNotEmpty(),
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FilledIconButton(
                    onClick = onSend,
                    modifier = Modifier.size(design.touchTarget)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send")
                }
            }
        }
    }
}

@Composable
private fun QuickEmojiPanel(
    onEmojiSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val emojis = listOf(
        listOf("😊", "🙂", "😌", "🤗", "💙", "💚", "🧡", "💜"),
        listOf("👍", "👋", "🙏", "✨", "🌟", "💪", "🤝", "👏"),
        listOf("✅", "❌", "❓", "💭", "💡", "🎯", "⏰", "❤️")
    )

    Surface(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Quick emoji",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Close, "Close", Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            emojis.forEach { row ->
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(row) { emoji ->
                        Surface(
                            onClick = { onEmojiSelected(emoji) },
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, style = MaterialTheme.typography.headlineSmall)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun EmptyConversationState(
    displayName: String,
    avatar: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatar)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            displayName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Start your conversation",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MessagesList(
    messages: List<DirectMessage>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    conversationId: String,
    onReport: (String) -> Unit,
    onRetry: (String, String) -> Unit,
    bubbleColorProvider: (isFromMe: Boolean) -> Color,
    isDark: Boolean
) {
    val design = rememberMessagesDesign()

    // Center content on larger screens
    val contentModifier = if (design.contentMaxWidth != null) {
        Modifier
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = design.contentMaxWidth)
    } else {
        Modifier.fillMaxSize()
    }

    LazyColumn(
        state = listState,
        modifier = contentModifier,
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = MessageBarDebug.listBottomPadding.coerceAtLeast(0f).dp,
            start = design.horizontalPadding,
            end = design.horizontalPadding
        ),
        verticalArrangement = Arrangement.spacedBy(design.itemSpacing)
    ) {
        items(messages.size, key = { messages[it].id }) { index ->
            val message = messages[index]
            val isFromMe = message.senderId == "me"
            val prev = messages.getOrNull(index - 1)
            val next = messages.getOrNull(index + 1)
            val isFirstInGroup = prev?.senderId != message.senderId
            val isLastInGroup = next?.senderId != message.senderId

            MessageBubble(
                message = message,
                isFromMe = isFromMe,
                isFirstInGroup = isFirstInGroup,
                isLastInGroup = isLastInGroup,
                onReport = { onReport(message.id) },
                onRetry = { onRetry(conversationId, message.id) },
                bubbleColor = bubbleColorProvider(isFromMe),
                design = design,
                isDark = isDark
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: DirectMessage,
    isFromMe: Boolean,
    isFirstInGroup: Boolean,
    isLastInGroup: Boolean,
    onReport: () -> Unit,
    onRetry: () -> Unit,
    bubbleColor: Color,
    design: MessagesDesignTokens = rememberMessagesDesign(),
    isDark: Boolean = true
) {
    val timeAgo = remember(message.timestamp) {
        formatTimeAgo(message.timestamp)
    }

    // Get font settings from theme
    val fontSettings = LocalFontSettings.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start
    ) {
        // Android-style bubble with consistent corners
        val shape = RoundedCornerShape(design.bubbleCornerRadius)
        val textColor = bubbleTextColor(isFromMe, isDark)

        Surface(
            shape = shape,
            color = bubbleColor,
            modifier = Modifier.widthIn(max = design.bubbleMaxWidth)
        ) {
            Column(
                modifier = Modifier.padding(design.bubblePadding)
            ) {
                Text(
                    text = message.content,
                    style = NeuroDivergentTypography.messageBody(fontSettings),
                    color = textColor
                )

                // Timestamp inline at bottom right - Android style
                if (isLastInGroup) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            timeAgo,
                            style = NeuroDivergentTypography.timestamp(fontSettings),
                            color = textColor.copy(alpha = 0.7f)
                        )
                        if (isFromMe) {
                            DeliveryStatusIndicator(
                                status = message.deliveryStatus,
                                onRetry = onRetry,
                                textColor = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryStatusIndicator(
    status: MessageDeliveryStatus,
    onRetry: () -> Unit,
    textColor: Color = Color.White
) {
    when (status) {
        MessageDeliveryStatus.SENDING -> {
            Icon(
                Icons.Filled.Schedule,
                contentDescription = "Sending",
                modifier = Modifier.size(14.dp),
                tint = textColor.copy(alpha = 0.7f)
            )
        }
        MessageDeliveryStatus.SENT -> {
            Icon(
                Icons.Filled.Done,
                contentDescription = "Sent",
                modifier = Modifier.size(14.dp),
                tint = textColor.copy(alpha = 0.7f)
            )
        }
        MessageDeliveryStatus.FAILED -> {
            Row(
                modifier = Modifier.clickable { onRetry() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    Icons.Filled.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFFFF6B6B)
                )
                Text(
                    "Retry",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFF6B6B)
                )
            }
        }
    }
}

// =============================================================================
// SENSORY LANE UI
// =============================================================================

@Composable
private fun SensoryLane(
    modifier: Modifier = Modifier,
    mode: SensoryMode,
    energy: Float,
    isDark: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing-animation")
    val breathingAlpha = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (4000 + 4000 * (1f - energy)).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing-alpha"
    )

    val laneColor = laneAccent(mode, isDark)
    val gradient = Brush.verticalGradient(
        0f to laneColor.copy(alpha = 0.20f + 0.25f * energy * breathingAlpha.value),
        1f to laneColor.copy(alpha = 0.05f)
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        content = content
    )
}

@Composable
private fun SensoryModeSwitcher(selected: SensoryMode, onSelect: (SensoryMode) -> Unit) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sensoryModes.forEach { (mode, label) ->
            FilterChip(
                selected = selected == mode,
                onClick = { onSelect(mode) },
                label = { Text(label) },
                leadingIcon = {
                    val dot = when (mode) {
                        SensoryMode.CALM -> "●"
                        SensoryMode.FOCUS -> "◆"
                        SensoryMode.STIM -> "✺"
                    }
                    Text(dot, fontSize = 14.sp)
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = laneAccent(mode, isDark).copy(alpha = 0.25f),
                    selectedContainerColor = laneAccent(mode, isDark).copy(alpha = 0.5f),
                    labelColor = if (isDark) Color.White else Color.Black,
                    selectedLabelColor = if (isDark) Color.White else Color.Black
                )
            )
        }
    }
}

// =============================================================================
// UTILITY FUNCTIONS
// =============================================================================

// ═══════════════════════════════════════════════════════════════
// TYPING INDICATOR
// ═══════════════════════════════════════════════════════════════

/**
 * Animated typing indicator bubble — three bouncing dots that simulate
 * the other person composing a message (iMessage / Google Messages style).
 */
/**
 * A realistic animated typing indicator that pulses dots in sequence.
 * Enhanced with professional timing and smooth transitions.
 */
@Composable
private fun TypingIndicatorBubble(
    displayName: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE8E8ED)
    val dotColor = if (isDark) Color(0xFF8E8E93) else Color(0xFF636366)

    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    @Composable
    fun typingDot(delayMs: Int): Float {
        val anim by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1000
                    0.4f at delayMs using FastOutSlowInEasing
                    1f at (delayMs + 300) using FastOutSlowInEasing
                    0.4f at (delayMs + 600) using FastOutSlowInEasing
                    0.4f at 1000 using LinearEasing
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "dot_$delayMs"
        )
        return anim
    }

    val dot1 = typingDot(0)
    val dot2 = typingDot(200)
    val dot3 = typingDot(400)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 64.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
            color = bubbleColor,
            tonalElevation = 1.dp,
            shadowElevation = 0.5.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(dot1, dot2, dot3).forEach { opacity ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .scale(0.8f + (0.2f * opacity))
                            .background(
                                dotColor.copy(alpha = opacity),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

/**
 * A simulated message thread that demonstrates the typing animation and real-time feel.
 * Great for tutorials or "welcome" experiences.
 */
@Composable
fun FakeMessageThread(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    var messages by remember { mutableStateOf(listOf(
        DirectMessage(
            id = "1",
            content = "Hey! welcome to NeuroComet 💫",
            senderId = "ai",
            recipientId = "me",
            timestamp = Instant.now().minusSeconds(120).toString(),
            isRead = true
        )
    )) }

    var isTyping by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()

    // Simulate a conversation flow
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        isTyping = true
        kotlinx.coroutines.delay(2500)
        isTyping = false
        messages = messages + DirectMessage(
            id = "2",
            content = "I'm here to show you how our messaging works. It's designed to be calm and easy to use.",
            senderId = "ai",
            recipientId = "me",
            timestamp = Instant.now().toString(),
            isRead = true
        )

        kotlinx.coroutines.delay(3000)
        isTyping = true
        kotlinx.coroutines.delay(1800)
        isTyping = false
        messages = messages + DirectMessage(
            id = "3",
            content = "See that animation? That's me typing! It helps you know when to wait for a reply. 😊",
            senderId = "ai",
            recipientId = "me",
            timestamp = Instant.now().toString(),
            isRead = true
        )
    }

    // Auto-scroll when new messages arrive
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty() || isTyping) {
            scrollState.animateScrollToItem(if (isTyping) messages.size else messages.size - 1)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // App Bar
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("☄️", fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Comet Guide", style = MaterialTheme.typography.titleMedium)
                        Text(if (isTyping) "typing..." else "Online",
                             style = MaterialTheme.typography.bodySmall,
                             color = if (isTyping) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        )

        // Message List
        LazyColumn(
            state = scrollState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                NeuroMessageItem(
                    message = msg,
                    isFromMe = msg.senderId == "me",
                    isDark = isDark,
                    onReport = {},
                    onRetry = {},
                    onReact = {}
                )
            }

            if (isTyping) {
                item {
                    TypingIndicatorBubble(displayName = "Comet Guide", isDark = isDark)
                }
            }
        }

        // Dummy Input
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text("Type a message...", color = Color.Gray)
                    }
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {},
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).size(42.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

/**
 * Generate a contextual simulated reply based on the user's message.
 * Returns a natural-sounding response that matches the mock persona.
 */
private fun generateSimulatedReply(displayName: String, lastMessage: String): String {
    val lower = lastMessage.lowercase()

    // Context-aware responses
    return when {
        lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ->
            listOf(
                "Hey! 😊 Great to hear from you!",
                "Hi there! How's your day going? 💜",
                "Hey! I was just thinking about you! ✨"
            ).random()

        lower.contains("how are you") || lower.contains("how's it going") ->
            listOf(
                "I'm doing pretty well today! Thanks for asking 💙",
                "Having a good focus day actually! How about you?",
                "A bit overstimulated but managing. How are you? 🧘"
            ).random()

        lower.contains("adhd") || lower.contains("focus") || lower.contains("distract") ->
            listOf(
                "Totally get it. ADHD brain is wild sometimes! Have you tried body doubling?",
                "I feel that so much. My focus has been all over the place this week 😅",
                "Same! I've been using the Pomodoro method — 25 min on, 10 min stim break ⏰"
            ).random()

        lower.contains("anxious") || lower.contains("anxiety") || lower.contains("stressed") ->
            listOf(
                "I'm sorry you're feeling that way 💙 Remember to breathe. You've got this.",
                "Sending you calm vibes ✨ Have you tried the 5-4-3-2-1 grounding technique?",
                "That's tough. Be gentle with yourself today. You deserve rest 🫂"
            ).random()

        lower.contains("stim") || lower.contains("fidget") || lower.contains("sensory") ->
            listOf(
                "Ooh yes! My favorite stim right now is my textured ring 💍✨",
                "Stimming is self-care! What's your go-to stim? 🌈",
                "I just got a new fidget cube and it's SO satisfying! 🎮"
            ).random()

        lower.contains("thanks") || lower.contains("thank you") ->
            listOf(
                "Of course! That's what this community is for 💜",
                "Anytime! We're all in this together ✨",
                "Always happy to help! 😊"
            ).random()

        lower.contains("good") || lower.contains("great") || lower.contains("awesome") ->
            listOf(
                "That's amazing! Celebrating the wins, big or small! 🎉",
                "Love to hear it! ✨",
                "Yay! That makes me so happy for you! 💜"
            ).random()

        lower.length < 10 ->
            listOf(
                "Tell me more! 😊",
                "Oh? What's on your mind? 💭",
                "I'm all ears! 👂✨"
            ).random()

        else ->
            listOf(
                "That's really interesting! Thanks for sharing 😊",
                "I appreciate you telling me that 💜",
                "That totally makes sense. I feel the same way sometimes!",
                "Oh wow, I hadn't thought of it that way! 🧠✨",
                "I love how open this community is. Thanks for being you! 💙"
            ).random()
    }
}

private fun formatTimeAgo(timestamp: String?): String {
    if (timestamp == null) return ""
    return try {
        val time = Instant.parse(timestamp)
        val diff = Duration.between(time, Instant.now())
        when {
            diff.toDays() > 6 -> "${diff.toDays() / 7}w"
            diff.toDays() > 0 -> "${diff.toDays()}d"
            diff.toHours() > 0 -> "${diff.toHours()}h"
            diff.toMinutes() > 0 -> "${diff.toMinutes()}m"
            else -> "now"
        }
    } catch (_: Exception) {
        ""
    }
}
