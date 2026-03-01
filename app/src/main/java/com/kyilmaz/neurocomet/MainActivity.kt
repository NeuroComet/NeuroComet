@file:Suppress(
    "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE",
    "UNUSED_VALUE",
    "AssignedValueIsNeverRead",
    "AssignmentToStateVariable"
)

package com.kyilmaz.neurocomet

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.revenuecat.purchases.*
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.kyilmaz.neurocomet.calling.NeurodivergentPersona
import com.kyilmaz.neurocomet.calling.PracticeCallScreen
import com.kyilmaz.neurocomet.calling.PracticeCallSelectionScreen

// --- 1. NAVIGATION & ROUTES ---
sealed class Screen(val route: String, val labelId: Int, val iconFilled: ImageVector, val iconOutlined: ImageVector) {
    data object Feed : Screen("feed", R.string.nav_feed, Icons.Filled.Home, Icons.Outlined.Home)
    data object Explore : Screen("explore", R.string.nav_explore, Icons.Filled.Search, Icons.Outlined.Search)
    data object Messages : Screen("messages", R.string.nav_messages, Icons.Filled.Mail, Icons.Outlined.Mail)
    data object Notifications : Screen("notifications", R.string.nav_notifications, Icons.Filled.Notifications, Icons.Outlined.Notifications)
    data object Settings : Screen("settings", R.string.nav_settings, Icons.Filled.Settings, Icons.Outlined.Settings)
    data object ThemeSettings : Screen("theme_settings", R.string.nav_settings, Icons.Filled.Palette, Icons.Outlined.Palette)
    data object AnimationSettings : Screen("animation_settings", R.string.nav_settings, Icons.Filled.Animation, Icons.Outlined.Animation)
    data object IconCustomization : Screen("icon_customization", R.string.settings_app_icon, Icons.Filled.Palette, Icons.Outlined.Palette)
    data object PrivacySettings : Screen("privacy_settings", R.string.nav_settings, Icons.Filled.Lock, Icons.Outlined.Lock)
    data object NotificationSettings : Screen("notification_settings", R.string.nav_settings, Icons.Filled.Notifications, Icons.Outlined.Notifications)
    data object ContentSettings : Screen("content_settings", R.string.nav_settings, Icons.Filled.PlayArrow, Icons.Outlined.PlayArrow)
    data object AccessibilitySettingsScreen : Screen("accessibility_settings", R.string.nav_settings, Icons.Filled.Accessibility, Icons.Outlined.Accessibility)
    data object WellbeingSettings : Screen("wellbeing_settings", R.string.nav_settings, Icons.Filled.Spa, Icons.Outlined.Spa)
    data object FontSettings : Screen("font_settings", R.string.nav_settings, Icons.Filled.TextFields, Icons.Outlined.TextFields)
    data object ParentalControls : Screen("parental_controls", R.string.nav_settings, Icons.Filled.Shield, Icons.Outlined.Shield)
    data object Conversation : Screen("conversation/{conversationId}", R.string.nav_messages, Icons.Filled.Mail, Icons.Outlined.Mail) {
        fun route(conversationId: String) = "conversation/$conversationId"
    }

    data object DevOptions : Screen("dev_options", R.string.settings_developer_options_group, Icons.Filled.Build, Icons.Outlined.Build)
    data object TopicDetail : Screen("topic/{topicId}", R.string.nav_explore, Icons.Filled.Search, Icons.Outlined.Search) {
        fun route(topicId: String) = "topic/$topicId"
    }
    data object Subscription : Screen("subscription", R.string.nav_settings, Icons.Filled.Star, Icons.Outlined.Star)
    data object CallHistory : Screen("call_history", R.string.nav_messages, Icons.Filled.Phone, Icons.Outlined.Phone)
    data object PracticeCallSelection : Screen("practice_call_selection", R.string.nav_messages, Icons.Filled.Headset, Icons.Outlined.Headset)
    data object PracticeCall : Screen("practice_call/{personaId}", R.string.nav_messages, Icons.Filled.Phone, Icons.Outlined.Phone) {
        fun route(personaId: String) = "practice_call/$personaId"
    }
    data object Profile : Screen("profile/{userId}", R.string.nav_settings, Icons.Filled.Person, Icons.Outlined.Person) {
        fun route(userId: String) = "profile/$userId"
    }
    data object MyProfile : Screen("my_profile", R.string.nav_settings, Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    data object GamesHub : Screen("games_hub", R.string.games_hub_title, Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports)
    data object GamePlay : Screen("game/{gameId}", R.string.games_hub_title, Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports) {
        fun route(gameId: String) = "game/$gameId"
    }
    data object FeedbackHub : Screen("feedback_hub/{action}", R.string.feedback_hub_title, Icons.Filled.Feedback, Icons.Outlined.Feedback) {
        fun route(action: String = "none") = "feedback_hub/$action"
    }
}

class MainActivity : AppCompatActivity() {

    private fun configureOrientationForDevice() {
        val displayMetrics = resources.displayMetrics
        val widthDp = displayMetrics.widthPixels / displayMetrics.density
        val heightDp = displayMetrics.heightPixels / displayMetrics.density
        val smallestWidthDp = minOf(widthDp, heightDp)

        requestedOrientation = if (smallestWidthDp >= 600) {
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        // Android 17+: Enable cross-device handoff by default.
        // Per-route toggling is handled in NeuroCometApp via HandoffManager.
        HandoffManager.setHandoffEnabled(this, true)

        PerformanceOverlayState.init(this)
        SettingsManager.init(this)
        CredentialStorage.initialize(this)

        try {
            SecurityManager.performSecurityCheck(this)
            if (!BuildConfig.DEBUG) {
                SecurityManager.enforceSecurity(
                    context = this,
                    allowEmulator = false,
                    allowDeveloperOptions = true
                )
            }
        } catch (_: SecurityException) {
            finish()
            return
        }

        configureOrientationForDevice()

        if (!NotificationChannels.hasNotificationPermission(this)) {
            NotificationChannels.requestNotificationPermission(this)
        }

        // RevenueCat: Only configure in release builds (or when a real key is present).
        // In debug builds, SubscriptionManager operates in test mode and simulates
        // the full purchase flow without the SDK.
        if (!BuildConfig.DEBUG) {
            val revenueCatKey = SecurityUtils.decrypt(BuildConfig.REVENUECAT_API_KEY).ifEmpty {
                "test_ghfalVJOgCZfjWpsJdiyCbHARmz"
            }
            Purchases.logLevel = LogLevel.DEBUG
            Purchases.configure(PurchasesConfiguration.Builder(this, revenueCatKey).build())
        } else {
            Log.d("MainActivity", "🧪 TEST MODE: RevenueCat SDK skipped — purchases will be simulated")
        }

        setContent {
            val feedViewModel: FeedViewModel = viewModel()
            val authViewModel: AuthViewModel = viewModel()
            val themeViewModel: ThemeViewModel = viewModel()
            val safetyViewModel: SafetyViewModel = viewModel()

            val context = LocalContext.current
            LaunchedEffect(Unit) {
                authViewModel.initialize(context)
            }

            val authState by authViewModel.user.collectAsState()
            val authError by authViewModel.error.collectAsState()
            val is2FARequired by authViewModel.is2FARequired.collectAsState()

            val themeState by themeViewModel.themeState.collectAsState()
            val isDark = themeState.isDarkMode
            
            SideEffect {
                val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
                windowInsetsController.isAppearanceLightStatusBars = !isDark
                windowInsetsController.isAppearanceLightNavigationBars = !isDark
            }

            LaunchedEffect(Unit) {
                val currentLocales = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()
                val languageTag = if (!currentLocales.isEmpty) {
                    currentLocales.get(0)?.toLanguageTag() ?: ""
                } else {
                    ""
                }
                themeViewModel.setLanguageCode(languageTag)

                if (!BuildConfig.DEBUG) {
                    Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
                        override fun onReceived(customerInfo: CustomerInfo) {
                            val isPremium = customerInfo.entitlements["premium"]?.isActive == true
                            feedViewModel.setPremiumStatus(isPremium)
                        }
                        override fun onError(error: PurchasesError) { }
                    })
                } else {
                    // In debug/test mode, use SubscriptionManager which simulates everything
                    SubscriptionManager.checkPremiumStatus { isPremium ->
                        feedViewModel.setPremiumStatus(isPremium)
                    }
                }
            }

            var showSplash by remember { mutableStateOf(true) }
            var showStaySignedIn by remember { mutableStateOf(false) }
            var staySignedInHandled by remember { mutableStateOf(false) }

            LaunchedEffect(authState, staySignedInHandled) {
                if (authState != null && !staySignedInHandled) {
                    if (StaySignedInSettings.shouldShowPrompt(this@MainActivity)) {
                        showStaySignedIn = true
                    } else {
                        staySignedInHandled = true
                    }
                }
            }

            NeuroThemeApplication(themeViewModel = themeViewModel) {
                if (showSplash) {
                    NeuroSplashScreen(
                        onFinished = { showSplash = false },
                        neuroState = themeState.selectedState
                    )
                } else if (showStaySignedIn && authState != null) {
                    StaySignedInScreen(
                        userEmail = authState?.id ?: "",
                        userDisplayName = authState?.name,
                        onYes = { dontShowAgain ->
                            StaySignedInSettings.savePreference(
                                context = this@MainActivity,
                                staySignedIn = true,
                                dontShowAgain = dontShowAgain
                            )
                            showStaySignedIn = false
                            staySignedInHandled = true
                        },
                        onNo = { dontShowAgain ->
                            StaySignedInSettings.savePreference(
                                context = this@MainActivity,
                                staySignedIn = false,
                                dontShowAgain = dontShowAgain
                            )
                            showStaySignedIn = false
                            staySignedInHandled = true
                        }
                    )
                } else {
                    NeuroCometApp(
                        feedViewModel = feedViewModel,
                        authViewModel = authViewModel,
                        themeViewModel = themeViewModel,
                        safetyViewModel = safetyViewModel,
                        authError = authError,
                        is2FARequired = is2FARequired,
                        authState = authState
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeuroCometApp(
    feedViewModel: FeedViewModel,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel,
    safetyViewModel: SafetyViewModel,
    authError: String?,
    is2FARequired: Boolean,
    authState: User?
) {
    val navController = rememberNavController()
    val feedState by feedViewModel.uiState.collectAsState()
    val safetyState by safetyViewModel.state.collectAsState()
    val themeState by themeViewModel.themeState.collectAsState()

    val authedUser = authState
    val isUserVerified = authedUser?.isVerified ?: CURRENT_USER.isVerified

    var showPremiumDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedState.errorMessage) {
        val msg = feedState.errorMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message = msg)
            feedViewModel.clearError()
        }
    }

    val context = LocalContext.current
    val app = remember(context) { context.applicationContext as android.app.Application }
    val devOptionsViewModel: DevOptionsViewModel = viewModel()
    val devOptions by devOptionsViewModel.options.collectAsState()

    LaunchedEffect(Unit) {
        devOptionsViewModel.refresh(app)
        safetyViewModel.refresh(app)
    }

    // Sync social settings → consuming components on startup and when returning from settings
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
            // Accessibility → ThemeViewModel
            val a11y = SocialSettingsManager.getAccessibilitySettings(context)
            themeViewModel.setDisableAllAnimations(a11y.reduceMotion)
            if (a11y.dyslexiaFont) {
                themeViewModel.setSelectedFont(AccessibilityFont.OPEN_DYSLEXIC)
            }
            if (a11y.largerText) {
                themeViewModel.setTextScaleFactor(1.3f)
            }

            // Content preferences → FeedViewModel
            val content = SocialSettingsManager.getContentPreferences(context)
            feedViewModel.applyContentPreferences(
                hideLikeCounts = content.hideLikeCounts,
                hideViewCounts = content.hideViewCounts,
                dataSaverMode = content.dataSaverMode
            )
        }
    }

    // Sync dev option flags to their consuming components
    LaunchedEffect(devOptions) {
        // Performance overlay
        PerformanceOverlayState.isEnabled = devOptions.showPerformanceOverlay
        // Propagate feature flags to FeedViewModel
        feedViewModel.setFeatureFlags(
            enableNewFeedLayout = devOptions.enableNewFeedLayout,
            enableAdvancedSearch = devOptions.enableAdvancedSearch,
            enableAiSuggestions = devOptions.enableAiSuggestions
        )
        // Re-fetch posts when simulation flags change so they take effect immediately
        feedViewModel.fetchPosts()
    }

    // Wellbeing: Break reminders via snackbar
    LaunchedEffect(Unit) {
        while (true) {
            val wellbeing = SocialSettingsManager.getWellbeingSettings(context)
            if (wellbeing.breakRemindersEnabled && wellbeing.breakIntervalMinutes > 0) {
                kotlinx.coroutines.delay(wellbeing.breakIntervalMinutes * 60_000L)
                val messages = listOf(
                    "🧘 Time for a mindful break! Stretch, breathe, or look away from the screen for a moment.",
                    "💙 Hey, you've been scrolling for a while. How about a quick break?",
                    "🌿 Gentle reminder: Take a breath. You deserve a moment of calm.",
                    "☕ Break time! Grab some water or a snack.",
                    "✨ Your brain deserves a rest. Step away for a few minutes!"
                )
                snackbarHostState.showSnackbar(
                    message = messages.random(),
                    duration = SnackbarDuration.Long
                )
            } else {
                kotlinx.coroutines.delay(60_000L) // Re-check every minute
            }
        }
    }

    if (authState == null) {
        AuthScreen(
            onSignIn = { email, password -> authViewModel.signIn(email, password) },
            onSignUp = { email, password, audience ->
                authViewModel.signUp(email, password, audience)
                audience?.let { safetyViewModel.setAudienceDirect(it) }
            },
            onVerify2FA = { code -> authViewModel.verify2FA(code) },
            is2FARequired = is2FARequired,
            error = authError,
            // Only allow skipping auth in debug builds to prevent unauthorized access
            onSkip = if (BuildConfig.DEBUG) {{ authViewModel.skipAuth() }} else null
        )
    } else {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Android 17+: Toggle handoff per-route based on content sensitivity.
        // Respects the dev option toggle (enableHandoff).
        val activity = context as? android.app.Activity
        LaunchedEffect(currentRoute, devOptions.enableHandoff) {
            if (activity != null) {
                val enabled = HandoffManager.shouldEnableHandoff(
                    route = currentRoute,
                    userOptIn = true,
                    devOverride = if (!devOptions.enableHandoff) false else null
                )
                HandoffManager.setHandoffEnabled(activity, enabled)
            }
        }

        val showBottomBar = currentRoute in listOf(
            Screen.Feed.route,
            Screen.Explore.route,
            Screen.Messages.route,
            Screen.Notifications.route,
            Screen.Settings.route
        )

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showBottomBar) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = NavigationBarDefaults.containerColor,
                        tonalElevation = NavigationBarDefaults.Elevation
                    ) {
                        Column {
                            NavigationBar(
                                modifier = Modifier.fillMaxWidth(),
                                tonalElevation = 0.dp,
                                windowInsets = WindowInsets(0, 0, 0, 0)
                            ) {
                                val screens = listOf(Screen.Feed, Screen.Explore, Screen.Messages, Screen.Notifications, Screen.Settings)
                                val currentDestination = navBackStackEntry?.destination

                                screens.forEach { screen ->
                                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                    NavigationBarItem(
                                        icon = {
                                            Box(
                                                modifier = if (screen == Screen.Settings) {
                                                    Modifier.pointerInput(Unit) {
                                                        detectTapGestures(
                                                            onLongPress = {
                                                                DevOptionsSettings.setDevMenuEnabled(app, true)
                                                                devOptionsViewModel.refresh(app)
                                                                navController.navigate(Screen.DevOptions.route)
                                                            }
                                                        )
                                                    }
                                                } else Modifier
                                            ) {
                                                Icon(
                                                    if (isSelected) screen.iconFilled else screen.iconOutlined,
                                                    stringResource(screen.labelId)
                                                )
                                            }
                                        },
                                        label = { Text(stringResource(screen.labelId)) },
                                        selected = isSelected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            val navHostPadding = if (showBottomBar) {
                innerPadding
            } else {
                PaddingValues(top = innerPadding.calculateTopPadding())
            }

            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Feed.route,
                    modifier = Modifier
                        .padding(navHostPadding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                composable(Screen.Feed.route) {
                    FeedScreen(
                        posts = feedState.posts,
                        stories = feedState.stories,
                        currentUser = CURRENT_USER.copy(isVerified = isUserVerified),
                        isLoading = feedState.isLoading,
                        onLikePost = { postId: Long -> feedViewModel.toggleLike(postId) },
                        onReplyPost = { post: Post -> feedViewModel.openCommentSheet(post) },
                        onSharePost = { ctx: Context, post: Post -> feedViewModel.sharePost(ctx, post) },
                        onAddPost = { content: String, tone: String, imageUrl: String?, videoUrl: String? ->
                            feedViewModel.createPost(content, tone, imageUrl, videoUrl)
                        },
                        onDeletePost = { postId: Long -> feedViewModel.deletePost(postId) },
                        onProfileClick = { userId ->
                            navController.navigate(Screen.Profile.route(userId))
                        },
                        onViewStory = { story -> feedViewModel.viewStory(story) },
                        onAddStory = { imageUrl, duration -> feedViewModel.createStory(imageUrl, duration) },
                        isPremium = feedState.isPremium,
                        onUpgradeClick = { showPremiumDialog = true },
                        isMockInterfaceEnabled = feedState.isMockInterfaceEnabled,
                        animationSettings = themeState.animationSettings,
                        safetyState = safetyState,
                        enableNewFeedLayout = devOptions.enableNewFeedLayout,
                        onSettingsClick = {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    feedState.activeStory?.let { story ->
                        StoryViewerDialog(
                            story = story,
                            onDismiss = { feedViewModel.dismissStory() },
                            onStoryViewed = { viewedStory ->
                                feedViewModel.markStoryAsViewed(viewedStory.id)
                            },
                            onReply = { _, _ -> },
                            enableReactions = devOptions.enableStoryReactions
                        )
                    }
                }
                composable(Screen.Explore.route) {
                    ExploreScreen(
                        posts = feedState.posts,
                        safetyState = safetyState,
                        onLikePost = { postId -> feedViewModel.toggleLike(postId) },
                        onSharePost = { ctx, post -> feedViewModel.sharePost(ctx, post) },
                        onCommentPost = { post -> feedViewModel.openCommentSheet(post) },
                        onTopicClick = { topicId ->
                            navController.navigate(Screen.TopicDetail.route(topicId))
                        },
                        onProfileClick = { userId ->
                            navController.navigate(Screen.Profile.route(userId))
                        }
                    )
                }
                composable(Screen.Messages.route) {
                    val state by feedViewModel.uiState.collectAsState()
                    NeuroInboxScreen(
                        conversations = state.conversations,
                        safetyState = safetyState,
                        onOpenConversation = { conversationId ->
                            feedViewModel.openConversation(conversationId)
                            navController.navigate(Screen.Conversation.route(conversationId))
                        },
                        onStartNewChat = { userId ->
                            val conversationId = feedViewModel.startOrOpenConversation(userId)
                            navController.navigate(Screen.Conversation.route(conversationId))
                        },
                        onOpenCallHistory = {
                            navController.navigate(Screen.CallHistory.route)
                        },
                        onOpenPracticeCall = {
                            navController.navigate(Screen.PracticeCallSelection.route)
                        }
                    )
                }
                composable(Screen.Conversation.route) { backStackEntry ->
                    val conversationId = backStackEntry.arguments?.getString("conversationId")
                    val state by feedViewModel.uiState.collectAsState()
                    val conv = state.conversations.find { it.id == conversationId } ?: state.activeConversation
                    if (conv == null) {
                        NeuroInboxScreen(
                            conversations = state.conversations,
                            safetyState = safetyState,
                            onOpenConversation = { id ->
                                feedViewModel.openConversation(id)
                                navController.navigate(Screen.Conversation.route(id))
                            },
                            onStartNewChat = { userId ->
                                val conversationId = feedViewModel.startOrOpenConversation(userId)
                                navController.navigate(Screen.Conversation.route(conversationId))
                            },
                            onBack = { navController.popBackStack() },
                            onOpenCallHistory = {
                                navController.navigate(Screen.CallHistory.route)
                            },
                            onOpenPracticeCall = {
                                navController.navigate(Screen.PracticeCallSelection.route)
                            }
                        )
                    } else {
                        NeuroConversationScreen(
                            conversation = conv,
                            onBack = {
                                navController.popBackStack()
                                feedViewModel.dismissConversation()
                            },
                            onSend = { recipientId, content ->
                                feedViewModel.sendDirectMessage(recipientId, content)
                            },
                            onReport = { messageId ->
                                feedViewModel.reportMessage(messageId)
                            },
                            onRetryMessage = { convId, msgId ->
                                feedViewModel.retryDirectMessage(convId, msgId)
                            },
                            onReactToMessage = { messageId, emoji ->
                                feedViewModel.reactToMessage(conv.id, messageId, emoji)
                            },
                            isBlocked = { feedViewModel.isUserBlocked(it) },
                            isMuted = { feedViewModel.isUserMuted(it) },
                            enableVideoChat = devOptions.enableVideoChat
                        )
                    }
                }
                composable(Screen.Notifications.route) {
                    val state by feedViewModel.uiState.collectAsState()
                    NotificationsScreen(
                        notifications = state.notifications,
                        modifier = Modifier.fillMaxSize(),
                        onRefresh = { feedViewModel.fetchNotifications() },
                        onNotificationClick = { notification ->
                            when (notification.type) {
                                NotificationType.LIKE, NotificationType.COMMENT, NotificationType.MENTION, NotificationType.REPOST -> {
                                    notification.relatedPostId?.let {
                                        navController.navigate(Screen.Feed.route) {
                                            popUpTo(Screen.Notifications.route) { inclusive = true }
                                        }
                                    }
                                }
                                NotificationType.FOLLOW -> {
                                    notification.relatedUserId?.let { userId ->
                                        navController.navigate(Screen.Profile.route(userId))
                                    }
                                }
                                else -> {}
                            }
                        },
                        onMarkAsRead = { notificationId ->
                            feedViewModel.markNotificationAsRead(notificationId)
                        },
                        onMarkAllAsRead = {
                            feedViewModel.markAllNotificationsAsRead()
                        },
                        onDismissNotification = { notificationId ->
                            feedViewModel.dismissNotification(notificationId)
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    val settingsContext = LocalContext.current
                    SettingsScreen(
                        authViewModel = authViewModel,
                        onLogout = {
                            if (!StaySignedInSettings.isStaySignedIn(settingsContext)) {
                                StaySignedInSettings.clearAll(settingsContext)
                            }
                            authViewModel.signOut()
                            navController.popBackStack(Screen.Feed.route, true)
                        },
                        safetyViewModel = safetyViewModel,
                        devOptionsViewModel = devOptionsViewModel,
                        canShowDevOptions = devOptions.devMenuEnabled,
                        onOpenDevOptions = {
                            navController.navigate(Screen.DevOptions.route)
                        },
                        onOpenParentalControls = {
                            navController.navigate(Screen.ParentalControls.route)
                        },
                        onOpenThemeSettings = {
                            navController.navigate(Screen.ThemeSettings.route)
                        },
                        onOpenAnimationSettings = {
                            navController.navigate(Screen.AnimationSettings.route)
                        },
                        onOpenIconCustomization = {
                            navController.navigate(Screen.IconCustomization.route)
                        },
                        onOpenPrivacySettings = {
                            navController.navigate(Screen.PrivacySettings.route)
                        },
                        onOpenNotificationSettings = {
                            navController.navigate(Screen.NotificationSettings.route)
                        },
                        onOpenContentSettings = {
                            navController.navigate(Screen.ContentSettings.route)
                        },
                        onOpenAccessibilitySettings = {
                            navController.navigate(Screen.AccessibilitySettingsScreen.route)
                        },
                        onOpenWellbeingSettings = {
                            navController.navigate(Screen.WellbeingSettings.route)
                        },
                        onOpenFontSettings = {
                            navController.navigate(Screen.FontSettings.route)
                        },
                        onOpenSubscription = {
                            navController.navigate(Screen.Subscription.route)
                        },
                        onOpenMyProfile = {
                            navController.navigate(Screen.MyProfile.route)
                        },
                        onOpenGames = {
                            navController.navigate(Screen.GamesHub.route)
                        },
                        onOpenBugReport = {
                            navController.navigate(Screen.FeedbackHub.route("bug"))
                        },
                        onOpenFeatureRequest = {
                            navController.navigate(Screen.FeedbackHub.route("feature"))
                        },
                        onOpenGeneralFeedback = {
                            navController.navigate(Screen.FeedbackHub.route("feedback"))
                        },
                        isPremium = feedState.isPremium,
                        isFakePremiumEnabled = feedState.isFakePremiumEnabled,
                        onFakePremiumToggle = { enabled ->
                            feedViewModel.toggleFakePremium(enabled)
                        },
                        themeViewModel = themeViewModel
                    )
                }
                composable(Screen.ThemeSettings.route) {
                    ThemeSettingsScreen(
                        themeViewModel = themeViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.AnimationSettings.route) {
                    AnimationSettingsScreen(
                        onBack = { navController.popBackStack() },
                        themeViewModel = themeViewModel
                    )
                }
                composable(Screen.IconCustomization.route) {
                    IconCustomizationScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.PrivacySettings.route) {
                    PrivacySettingsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.NotificationSettings.route) {
                    NotificationSettingsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.ContentSettings.route) {
                    ContentPreferencesScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.AccessibilitySettingsScreen.route) {
                    AccessibilitySettingsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.WellbeingSettings.route) {
                    WellbeingSettingsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.FontSettings.route) {
                    FontSettingsScreen(
                        themeViewModel = themeViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.DevOptions.route) {
                    DevOptionsScreen(
                        onBack = { navController.popBackStack() },
                        devOptionsViewModel = devOptionsViewModel,
                        safetyViewModel = safetyViewModel,
                        feedViewModel = feedViewModel,
                        authViewModel = authViewModel,
                        themeViewModel = themeViewModel,
                        onNavigateToGame = { gameId ->
                            navController.navigate(Screen.GamePlay.route(gameId))
                        }
                    )
                }
                composable(Screen.ParentalControls.route) {
                    ParentalControlsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.TopicDetail.route) { backStackEntry ->
                    val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
                    TopicDetailScreen(
                        topicName = topicId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Subscription.route) {
                    SubscriptionScreen(
                        onBack = { navController.popBackStack() },
                        onPurchaseSuccess = {
                            feedViewModel.setPremiumStatus(true)
                            navController.popBackStack()
                        }
                    )
                }
                composable(Screen.CallHistory.route) {
                    CallHistoryScreen(
                        onBack = { navController.popBackStack() },
                        onCallUser = { userId, userName, userAvatar, callType ->
                            MockCallManager.startCall(
                                recipientId = userId,
                                recipientName = userName,
                                recipientAvatar = userAvatar,
                                callType = callType
                            )
                        },
                        onOpenPracticeCallSelection = {
                            navController.navigate(Screen.PracticeCallSelection.route)
                        }
                    )
                }
                composable(Screen.PracticeCallSelection.route) {
                    PracticeCallSelectionScreen(
                        onBack = { navController.popBackStack() },
                        onPersonaSelected = { persona ->
                            navController.navigate(Screen.PracticeCall.route(persona.name))
                        }
                    )
                }
                composable(Screen.PracticeCall.route) { backStackEntry ->
                    val personaId = backStackEntry.arguments?.getString("personaId") ?: ""
                    val persona = try {
                        NeurodivergentPersona.valueOf(personaId)
                    } catch (_: IllegalArgumentException) {
                        NeurodivergentPersona.ADHD_FRIEND
                    }
                    PracticeCallScreen(
                        persona = persona,
                        onEndCall = { navController.popBackStack() }
                    )
                }
                composable(Screen.Profile.route) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    ProfileScreen(
                        userId = userId,
                        onBack = { navController.popBackStack() },
                        onMessageUser = { uid ->
                            val existingConvo = feedState.conversations.find { conv ->
                                conv.participants.contains(uid)
                            }
                            if (existingConvo != null) {
                                feedViewModel.openConversation(existingConvo.id)
                                navController.navigate(Screen.Conversation.route(existingConvo.id))
                            } else {
                                val convId = feedViewModel.startOrOpenConversation(uid)
                                navController.navigate(Screen.Conversation.route(convId))
                            }
                        },
                        onFollowToggle = { /* Toggle handled internally by ProfileScreen state */ },
                        onPostClick = {
                            navController.navigate(Screen.Feed.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onEditProfile = {
                            navController.navigate(Screen.MyProfile.route)
                        }
                    )
                }
                composable(Screen.MyProfile.route) {
                    ProfileScreen(
                        userId = "me",
                        onBack = { navController.popBackStack() },
                        onMessageUser = { },
                        onFollowToggle = { },
                        onPostClick = { },
                        onEditProfile = { }
                    )
                }
                composable(Screen.GamesHub.route) {
                    com.kyilmaz.neurocomet.games.GamesHubScreen(
                        onBack = { navController.popBackStack() },
                        onGameSelected = { game ->
                            navController.navigate(Screen.GamePlay.route(game.id))
                        }
                    )
                }
                composable(Screen.GamePlay.route) { backStackEntry ->
                    val gameId = backStackEntry.arguments?.getString("gameId") ?: "bubble_pop"
                    com.kyilmaz.neurocomet.games.GameScreen(
                        gameId = gameId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.FeedbackHub.route) { backStackEntry ->
                    val action = backStackEntry.arguments?.getString("action") ?: "none"
                    val initialAction = when (action) {
                        "bug" -> FeedbackInitialAction.BUG_REPORT
                        "feature" -> FeedbackInitialAction.FEATURE_REQUEST
                        "feedback" -> FeedbackInitialAction.GENERAL_FEEDBACK
                        else -> FeedbackInitialAction.NONE
                    }
                    FeedbackHubScreen(
                        onBack = { navController.popBackStack() },
                        initialAction = initialAction
                    )
                }
            }

            DebugPerformanceOverlay(
                enabled = PerformanceOverlayState.isEnabled,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
            )
        }
        }

        if (showPremiumDialog) {
            AlertDialog(
                onDismissRequest = { showPremiumDialog = false },
                title = { Text(stringResource(R.string.premium_dialog_title)) },
                text = { Text(stringResource(R.string.premium_dialog_message)) },
                confirmButton = {
                    TextButton(onClick = { showPremiumDialog = false }) { Text(stringResource(R.string.button_ok)) }
                }
            )
        }

        CommentBottomSheet(
            isVisible = feedState.isCommentSheetVisible,
            comments = feedState.activePostComments,
            onDismiss = { feedViewModel.dismissCommentSheet() },
            onAddComment = { content -> feedViewModel.addComment(content) },
            postAuthor = feedState.posts.find { it.id == feedState.activePostId }?.userId
        )

        TutorialTrigger()
        TutorialOverlay()
    }
}
