package com.kyilmaz.neuronetworkingtitle

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import java.time.Instant
import androidx.compose.ui.platform.LocalContext

/**
 * Contract:
 * - Under-13: DMs are disabled.
 * - Teens/Adults: can view and send DMs.
 * - Anti-abuse: basic local rate-limit + block/report actions.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DmInboxScreen(
    conversations: List<Conversation>,
    safetyState: SafetyState,
    onOpenConversation: (String) -> Unit,
    onBack: (() -> Unit)? = null
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(conversations, query) {
        val q = query.trim().lowercase()
        if (q.isBlank()) return@remember conversations
        conversations.filter { conv ->
            val other = conv.participants.firstOrNull { it != "me" } ?: conv.participants.firstOrNull().orEmpty()
            val last = conv.messages.lastOrNull()?.content.orEmpty()
            other.lowercase().contains(q) || last.lowercase().contains(q)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dm_title)) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.dm_back))
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (safetyState.isKidsMode) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.dm_kids_disabled_title), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.dm_kids_disabled_body), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text(stringResource(R.string.dm_search_placeholder)) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.dm_empty_title), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(stringResource(R.string.dm_empty_subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                return@Column
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(filtered, key = { it.id }) { conv ->
                    ConversationRow(conversation = conv, onClick = { onOpenConversation(conv.id) })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(conversation: Conversation, onClick: () -> Unit) {
    val other = conversation.participants.firstOrNull { it != "me" } ?: conversation.participants.firstOrNull().orEmpty()
    val lastMsg = conversation.messages.lastOrNull()?.content.orEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(other, fontWeight = FontWeight.Bold)
                if (conversation.unreadCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    AssistChip(onClick = {}, label = { Text("${conversation.unreadCount}") })
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(lastMsg, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
        Text(
            TimeFormatters.relativeDayOrShort(conversation.lastMessageTimestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DmConversationScreen(
    conversation: Conversation,
    safetyState: SafetyState,
    onBack: () -> Unit,
    onSend: (recipientId: String, content: String) -> Unit,
    onReport: (messageId: String) -> Unit,
    onRetryMessage: (conversationId: String, messageId: String) -> Unit = { _, _ -> },
    onBlockUser: (userId: String) -> Unit = {},
    onUnblockUser: (userId: String) -> Unit = {},
    onMuteUser: (userId: String) -> Unit = {},
    onUnmuteUser: (userId: String) -> Unit = {},
    isBlocked: (userId: String) -> Boolean = { false },
    isMuted: (userId: String) -> Boolean = { false },
    modifier: Modifier = Modifier
) {
    if (safetyState.isKidsMode) {
        // Should never happen if gated at navigation, but keep it robust.
        Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(stringResource(R.string.dm_kids_disabled_title), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.dm_kids_disabled_body), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onBack) { Text(stringResource(R.string.dm_back)) }
        }
        return
    }

    val recipientId = remember(conversation.id) {
        conversation.participants.firstOrNull { it != "me" } ?: conversation.participants.lastOrNull().orEmpty()
    }

    var input by remember { mutableStateOf("") }
    var showInfo by remember { mutableStateOf(false) }
    var showThreadMenu by remember { mutableStateOf(false) }
    var showBlockConfirm by remember { mutableStateOf(false) }
    var showUnblockConfirm by remember { mutableStateOf(false) }

    // Anti-abuse: small local rate limit (prevents spam tapping)
    var lastSendAt by remember { mutableLongStateOf(0L) }
    val minIntervalMs = 1200L
    val nowMs = System.currentTimeMillis()
    val cooldownRemainingMs = (minIntervalMs - (nowMs - lastSendAt)).coerceAtLeast(0L)
    val blocked = isBlocked(recipientId)
    val canSend = input.isNotBlank() && cooldownRemainingMs == 0L && !blocked

    val listState = rememberLazyListState()
    LaunchedEffect(conversation.id, conversation.messages.size) {
        if (conversation.messages.isNotEmpty()) {
            listState.animateScrollToItem(conversation.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipientId) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.dm_back))
                    }
                },
                actions = {
                    IconButton(onClick = { showThreadMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.dm_conversation_options))
                    }

                    DropdownMenu(expanded = showThreadMenu, onDismissRequest = { showThreadMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.dm_safety_title)) },
                            onClick = {
                                showThreadMenu = false
                                showInfo = true
                            }
                        )

                        if (isMuted(recipientId)) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.dm_unmute_user)) },
                                onClick = {
                                    showThreadMenu = false
                                    onUnmuteUser(recipientId)
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.dm_mute_user)) },
                                onClick = {
                                    showThreadMenu = false
                                    onMuteUser(recipientId)
                                }
                            )
                        }

                        if (isBlocked(recipientId)) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.dm_unblock_user)) },
                                onClick = {
                                    showThreadMenu = false
                                    showUnblockConfirm = true
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.dm_block_user)) },
                                onClick = {
                                    showThreadMenu = false
                                    showBlockConfirm = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (blocked) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.dm_blocked_banner),
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(conversation.messages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == "me"
                    MessageBubble(
                        message = msg,
                        isMe = isMe,
                        onReport = { onReport(msg.id) },
                        onBlockUser = { onBlockUser(recipientId) },
                        onRetry = {
                            if (msg.deliveryStatus == MessageDeliveryStatus.FAILED) {
                                onRetryMessage(conversation.id, msg.id)
                            }
                        }
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.dm_message_placeholder)) },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (canSend) {
                                lastSendAt = System.currentTimeMillis()
                                onSend(recipientId, input.trim())
                                input = ""
                            }
                        }
                    )
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (!canSend) return@IconButton
                        lastSendAt = System.currentTimeMillis()
                        onSend(recipientId, input.trim())
                        input = ""
                    },
                    enabled = canSend
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(R.string.dm_send))
                }
            }

            if (cooldownRemainingMs > 0L) {
                Text(
                    text = stringResource(R.string.dm_slow_down),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
                )
            }
        }

        if (showInfo) {
            AlertDialog(
                onDismissRequest = { showInfo = false },
                title = { Text(stringResource(R.string.dm_safety_title)) },
                text = {
                    Text(stringResource(R.string.dm_safety_body))
                },
                confirmButton = {
                    TextButton(onClick = { showInfo = false }) { Text("OK") }
                }
            )
        }

        if (showBlockConfirm) {
            AlertDialog(
                onDismissRequest = { showBlockConfirm = false },
                title = { Text(stringResource(R.string.dm_block_confirm_title, recipientId)) },
                text = { Text(stringResource(R.string.dm_block_confirm_body)) },
                confirmButton = {
                    Button(
                        onClick = {
                            showBlockConfirm = false
                            onBlockUser(recipientId)
                        }
                    ) { Text(stringResource(R.string.dm_block_user)) }
                },
                dismissButton = {
                    TextButton(onClick = { showBlockConfirm = false }) { Text(stringResource(R.string.dm_back)) }
                }
            )
        }

        if (showUnblockConfirm) {
            AlertDialog(
                onDismissRequest = { showUnblockConfirm = false },
                title = { Text(stringResource(R.string.dm_unblock_confirm_title, recipientId)) },
                text = { Text(stringResource(R.string.dm_unblock_confirm_body)) },
                confirmButton = {
                    Button(
                        onClick = {
                            showUnblockConfirm = false
                            onUnblockUser(recipientId)
                        }
                    ) { Text(stringResource(R.string.dm_unblock_user)) }
                },
                dismissButton = {
                    TextButton(onClick = { showUnblockConfirm = false }) { Text(stringResource(R.string.dm_back)) }
                }
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: DirectMessage,
    isMe: Boolean,
    onReport: () -> Unit,
    onBlockUser: () -> Unit,
    onRetry: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboard = remember(context) { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    var menuExpanded by remember { mutableStateOf(false) }

    val bg = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    val isFailedOutgoing = isMe && message.deliveryStatus == MessageDeliveryStatus.FAILED

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = bg,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .combinedClickable(
                    onClick = { if (isFailedOutgoing) onRetry?.invoke() },
                    onLongClick = { menuExpanded = true }
                )
         ) {
             Column(modifier = Modifier.padding(10.dp)) {
                 if (message.moderationStatus == ModerationStatus.FLAGGED) {
                     Text(
                         stringResource(R.string.dm_flagged_badge),
                         style = MaterialTheme.typography.labelSmall,
                         color = Color(0xFFB45309),
                         fontWeight = FontWeight.Bold
                     )
                     Spacer(Modifier.height(4.dp))
                 }
                 Text(message.content, color = fg)

                 Spacer(Modifier.height(6.dp))
                 Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        TimeFormatters.timeOfDay(message.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(8.dp))
                    when (message.deliveryStatus) {
                        MessageDeliveryStatus.SENDING -> Text(
                            stringResource(R.string.dm_sending),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        MessageDeliveryStatus.FAILED -> Text(
                            "${stringResource(R.string.dm_failed)} • ${stringResource(R.string.dm_tap_to_retry)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        MessageDeliveryStatus.SENT -> Unit
                    }
                }
             }
         }

        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.dm_copy)) },
                onClick = {
                    menuExpanded = false
                    clipboard.setPrimaryClip(ClipData.newPlainText("message", message.content))
                }
            )

            if (!isMe) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.dm_report)) },
                    onClick = {
                        menuExpanded = false
                        onReport()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.dm_block_user)) },
                    onClick = {
                        menuExpanded = false
                        onBlockUser()
                    }
                )
            } else if (message.deliveryStatus == MessageDeliveryStatus.FAILED && onRetry != null) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.dm_tap_to_retry)) },
                    onClick = {
                        menuExpanded = false
                        onRetry()
                    }
                )
            }
        }
    }
}
