package com.kyilmaz.neurocomet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String, Audience?) -> Unit,
    onVerify2FA: (String) -> Unit,
    is2FARequired: Boolean,
    error: String?,
    animationSettings: AnimationSettings = AnimationSettings(),
    showDevBypass: Boolean = false,
    onSkip: (() -> Unit)? = null
) {
    val canonicalLayout = LocalCanonicalLayout.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val contentMaxWidth = when (canonicalLayout.authLayout) {
        CanonicalAuthLayout.STACKED -> 520.dp
        CanonicalAuthLayout.BALANCED -> 640.dp
        CanonicalAuthLayout.SPLIT -> 1040.dp
    }
    val wideLayout = canonicalLayout.authLayout == CanonicalAuthLayout.SPLIT && !canonicalLayout.isCompactHeight && !is2FARequired
    val logoMotionEnabled = animationSettings.shouldAnimate(AnimationType.LOGO)

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var twoFactorCode by rememberSaveable { mutableStateOf("") }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    var showAgeDialog by remember { mutableStateOf(false) }
    var pendingEmail by remember { mutableStateOf("") }
    var pendingPassword by remember { mutableStateOf("") }

    val isSignIn = selectedTabIndex == 0
    val resolvedError = error ?: localError
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val passwordsNotMatchError = stringResource(R.string.auth_passwords_not_match)
    val passwordWeakError = stringResource(R.string.auth_password_weak)
    val invalidTwoFactorMessage = stringResource(R.string.auth_2fa_description)

    val submitPrimaryAction: () -> Unit = {
        focusManager.clearFocus(force = true)
        localError = null
        if (isSignIn) {
            onSignIn(email.trim(), password)
        } else {
            when {
                password != confirmPassword -> {
                    localError = passwordsNotMatchError
                }
                !isStrongPassword(password, email) -> {
                    localError = passwordWeakError
                }
                else -> {
                    pendingEmail = email.trim()
                    pendingPassword = password
                    showAgeDialog = true
                }
            }
        }
    }

    NeuroCometAmbientBackground(
        modifier = Modifier.fillMaxSize(),
        primary = primary,
        secondary = secondary,
        tertiary = tertiary,
        motionEnabled = logoMotionEnabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = if (canonicalLayout.isCompactHeight) 8.dp else 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (wideLayout) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = contentMaxWidth)
                        .verticalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AuthFormCard(
                        modifier = Modifier.widthIn(max = 440.dp),
                        is2FARequired = is2FARequired,
                        isSignIn = isSignIn,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        twoFactorCode = twoFactorCode,
                        error = resolvedError,
                        passwordVisible = passwordVisible,
                        confirmPasswordVisible = confirmPasswordVisible,
                        onEmailChange = {
                            email = it
                            localError = null
                        },
                        onPasswordChange = {
                            password = it
                            localError = null
                        },
                        onConfirmPasswordChange = {
                            confirmPassword = it
                            localError = null
                        },
                        onCodeChange = {
                            twoFactorCode = it.filter(Char::isDigit).take(6)
                            localError = null
                        },
                        onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                        onToggleConfirmPasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                        onSelectSignIn = {
                            selectedTabIndex = 0
                            localError = null
                        },
                        onSelectSignUp = {
                            selectedTabIndex = 1
                            localError = null
                        },
                        onPrimaryAction = { submitPrimaryAction() },
                        onVerify2FA = {
                            if (twoFactorCode.length == 6) {
                                focusManager.clearFocus(force = true)
                                onVerify2FA(twoFactorCode)
                            } else {
                                localError = invalidTwoFactorMessage
                            }
                        },
                        onSkip = if (!is2FARequired) onSkip else null,
                        showDevBypass = showDevBypass
                    )
                    AuthHeroPanel(
                        modifier = Modifier.weight(1f),
                        motionEnabled = logoMotionEnabled
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = contentMaxWidth)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(if (canonicalLayout.isCompactHeight) 8.dp else 14.dp)
                ) {
                    AuthHeroPanel(
                        modifier = Modifier.fillMaxWidth(),
                        motionEnabled = logoMotionEnabled
                    )
                    AuthFormCard(
                        modifier = Modifier.fillMaxWidth(),
                        is2FARequired = is2FARequired,
                        isSignIn = isSignIn,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        twoFactorCode = twoFactorCode,
                        error = resolvedError,
                        passwordVisible = passwordVisible,
                        confirmPasswordVisible = confirmPasswordVisible,
                        onEmailChange = {
                            email = it
                            localError = null
                        },
                        onPasswordChange = {
                            password = it
                            localError = null
                        },
                        onConfirmPasswordChange = {
                            confirmPassword = it
                            localError = null
                        },
                        onCodeChange = {
                            twoFactorCode = it.filter(Char::isDigit).take(6)
                            localError = null
                        },
                        onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                        onToggleConfirmPasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                        onSelectSignIn = {
                            selectedTabIndex = 0
                            localError = null
                        },
                        onSelectSignUp = {
                            selectedTabIndex = 1
                            localError = null
                        },
                        onPrimaryAction = { submitPrimaryAction() },
                        onVerify2FA = {
                            if (twoFactorCode.length == 6) {
                                focusManager.clearFocus(force = true)
                                onVerify2FA(twoFactorCode)
                            } else {
                                localError = invalidTwoFactorMessage
                            }
                        },
                        onSkip = if (!is2FARequired) onSkip else null,
                        showDevBypass = showDevBypass
                    )
                }
            }
        }
    }

    if (showAgeDialog) {
        AgeVerificationDialog(
            onDismiss = { showAgeDialog = false },
            onConfirm = { audience ->
                onSignUp(pendingEmail, pendingPassword, audience)
                showAgeDialog = false
            },
            onSkip = if (showDevBypass) {
                {
                    onSignUp(pendingEmail, pendingPassword, null)
                    showAgeDialog = false
                }
            } else {
                null
            }
        )
    }
}

@Composable
private fun AuthHeroPanel(
    modifier: Modifier = Modifier,
    motionEnabled: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BrandPill(text = stringResource(R.string.splash_beta_badge))

        NeuroCometBrandMark(
            modifier = Modifier.size(80.dp),
            haloColor = MaterialTheme.colorScheme.primary,
            accentColor = MaterialTheme.colorScheme.secondary,
            motionEnabled = motionEnabled
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.auth_welcome_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.auth_app_tagline),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.auth_welcome_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 560.dp)
            )
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AuthFeatureRow(
                    icon = Icons.Default.CheckCircle,
                    title = stringResource(R.string.auth_feature_accessible_title),
                    body = stringResource(R.string.auth_feature_accessible_body)
                )
                AuthFeatureRow(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.auth_feature_private_title),
                    body = stringResource(R.string.auth_feature_private_body)
                )
                AuthFeatureRow(
                    icon = Icons.Default.Palette,
                    title = stringResource(R.string.auth_feature_flexible_title),
                    body = stringResource(R.string.auth_feature_flexible_body)
                )
            }
        }
    }
}

@Composable
private fun AuthFeatureRow(
    icon: ImageVector,
    title: String,
    body: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AuthFormCard(
    modifier: Modifier = Modifier,
    is2FARequired: Boolean,
    isSignIn: Boolean,
    email: String,
    password: String,
    confirmPassword: String,
    twoFactorCode: String,
    error: String?,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onSelectSignIn: () -> Unit,
    onSelectSignUp: () -> Unit,
    onPrimaryAction: () -> Unit,
    onVerify2FA: () -> Unit,
    onSkip: (() -> Unit)?,
    showDevBypass: Boolean
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.96f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (is2FARequired) {
                Text(
                    text = stringResource(R.string.auth_2fa_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.auth_2fa_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                AuthModeSwitch(
                    isSignIn = isSignIn,
                    onSelectSignIn = onSelectSignIn,
                    onSelectSignUp = onSelectSignUp
                )
                Text(
                    text = if (isSignIn) stringResource(R.string.auth_sign_in) else stringResource(R.string.auth_create_account),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isSignIn) stringResource(R.string.auth_sign_in_subtitle) else stringResource(R.string.auth_sign_up_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!error.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }

            if (is2FARequired) {
                AuthTextField(
                    value = twoFactorCode,
                    onValueChange = onCodeChange,
                    label = stringResource(R.string.auth_2fa_code_label),
                    placeholder = stringResource(R.string.auth_2fa_code_placeholder),
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
                AuthPrimaryButton(
                    text = stringResource(R.string.auth_2fa_verify),
                    onClick = onVerify2FA
                )
            } else {
                AuthTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = stringResource(R.string.auth_email_label),
                    placeholder = stringResource(R.string.auth_email_placeholder),
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
                PasswordField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = stringResource(R.string.auth_password_label),
                    passwordVisible = passwordVisible,
                    onTogglePasswordVisibility = onTogglePasswordVisibility,
                    imeAction = if (isSignIn) ImeAction.Done else ImeAction.Next
                )

                AnimatedVisibility(
                    visible = !isSignIn,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 5 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 5 })
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        PasswordField(
                            value = confirmPassword,
                            onValueChange = onConfirmPasswordChange,
                            label = stringResource(R.string.auth_confirm_password_label),
                            passwordVisible = confirmPasswordVisible,
                            onTogglePasswordVisibility = onToggleConfirmPasswordVisibility,
                            imeAction = ImeAction.Done
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.auth_password_requirements),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                            )
                        }
                    }
                }

                AuthPrimaryButton(
                    text = if (isSignIn) stringResource(R.string.auth_sign_in) else stringResource(R.string.auth_create_account),
                    onClick = onPrimaryAction
                )
            }

            AuthSecurityFooter(
                onSkip = onSkip,
                showDevBypass = showDevBypass
            )
        }
    }
}

@Composable
private fun AuthModeSwitch(
    isSignIn: Boolean,
    onSelectSignIn: () -> Unit,
    onSelectSignUp: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AuthModeOption(
            text = stringResource(R.string.auth_sign_in),
            selected = isSignIn,
            onClick = onSelectSignIn
        )
        AuthModeOption(
            text = stringResource(R.string.auth_sign_up),
            selected = !isSignIn,
            onClick = onSelectSignUp
        )
    }
}

@Composable
private fun RowScope.AuthModeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
        label = "authModeContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "authModeContent"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .background(containerColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.84f)
        ),
        shape = RoundedCornerShape(20.dp),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        )
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    imeAction: ImeAction
) {
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = "",
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible) {
                        stringResource(R.string.auth_hide_password)
                    } else {
                        stringResource(R.string.auth_show_password)
                    },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )
}

@Composable
private fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AuthSecurityFooter(
    onSkip: (() -> Unit)?,
    showDevBypass: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = stringResource(R.string.auth_data_secure),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        if (onSkip != null) {
            TextButton(onClick = onSkip) {
                Text(
                    text = if (showDevBypass) stringResource(R.string.auth_skip_dev) else stringResource(R.string.auth_skip),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun isStrongPassword(pass: String, emailValue: String): Boolean {
    val hasUpper = pass.any { it.isUpperCase() }
    val hasLower = pass.any { it.isLowerCase() }
    val hasDigit = pass.any { it.isDigit() }
    val hasSymbol = pass.any { !it.isLetterOrDigit() }
    val longEnough = pass.length >= 12
    val emailLocal = emailValue.substringBefore("@").lowercase()
    val containsEmailPart = emailLocal.isNotBlank() && pass.lowercase().contains(emailLocal)
    return longEnough && hasUpper && hasLower && hasDigit && hasSymbol && !containsEmailPart
}
