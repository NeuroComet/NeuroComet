package com.kyilmaz.neuronetworkingtitle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Minimal Settings screen so navigation compiles.
 *
 * This project’s UI is demo/mock-heavy; this screen intentionally stays lightweight.
 * Developer Options are accessible via long-press on the bottom-bar Settings icon.
 */
@Composable
fun SettingsScreen(
    @Suppress("UNUSED_PARAMETER") authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    @Suppress("UNUSED_PARAMETER") safetyViewModel: SafetyViewModel,
    devOptionsViewModel: DevOptionsViewModel,
    canShowDevOptions: Boolean,
    onOpenDevOptions: () -> Unit
) {
    val options by devOptionsViewModel.options.collectAsState()
    val app = ApplicationProvider.app
    val devUnlockTapCountState = remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.clickable {
                    // Guaranteed fallback unlock: tap the Settings title 7 times.
                    // This avoids reliance on long-press gestures (which can be flaky on some devices/ROMs).
                    devUnlockTapCountState.intValue += 1
                    if (devUnlockTapCountState.intValue >= 7) {
                        devUnlockTapCountState.intValue = 0
                        if (app != null) devOptionsViewModel.setDevMenuEnabled(app, true)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(8.dp)) {
                    ListItem(
                        headlineContent = { Text("Developer Options") },
                        supportingContent = {
                            Text(
                                if (canShowDevOptions) {
                                    "Enabled. Tap to open. (Or long-press the Settings tab.)"
                                } else {
                                    "Enable to show and unlock advanced test tools. (Or tap the Settings title 7 times.)"
                                }
                            )
                        },
                        leadingContent = { Icon(Icons.Default.Build, contentDescription = null) },
                        modifier = Modifier.clickable(enabled = canShowDevOptions) {
                            // Guaranteed entry-point: this MUST navigate.
                            val a = app ?: return@clickable
                            DevOptionsSettings.setDevMenuEnabled(a, true)
                            onOpenDevOptions()
                        },
                        trailingContent = {
                            Switch(
                                checked = options.devMenuEnabled,
                                onCheckedChange = {
                                    val a = app ?: return@Switch
                                    devOptionsViewModel.setDevMenuEnabled(a, it)
                                }
                            )
                        }
                    )

                    // When enabled, show an explicit action button so users are never blocked by gesture issues.
                    if (canShowDevOptions) {
                        TextButton(
                            onClick = {
                                val a = app ?: return@TextButton
                                DevOptionsSettings.setDevMenuEnabled(a, true)
                                onOpenDevOptions()
                            }
                        ) { Text("Open Developer Options") }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                ListItem(
                    headlineContent = { Text("Log out") },
                    supportingContent = { Text("Clears local mock auth state.") },
                    leadingContent = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                    trailingContent = { TextButton(onClick = onLogout) { Text("Log out") } }
                )
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Tip: Long-press the Settings icon in the bottom bar, or tap the Settings title 7× to unlock Developer Options.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
