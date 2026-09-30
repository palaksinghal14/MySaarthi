package com.palaksinghal.mysaarthi.presentation.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.CaprasimoFamily
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral300
import com.palaksinghal.mysaarthi.presentation.theme.Neutral700
import com.palaksinghal.mysaarthi.presentation.theme.Surface
import com.palaksinghal.mysaarthi.presentation.theme.TextInk
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palaksinghal.mysaarthi.presentation.home.profile.SettingsViewModel
import com.palaksinghal.mysaarthi.presentation.util.toUserMessage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            onSignOut()
        }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextInk
                )
            }
            Text(
                text = "Settings",
                fontFamily = CaprasimoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = TextInk
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // ABOUT section
            SettingsSection(title = "ABOUT") {
                SettingsRow(label = "Version", value = "1.0.0")
                SettingsDivider()
                SettingsRow(label = "Built with", value = "Kotlin + Jetpack Compose")
            }

            // SUPPORT section
            SettingsSection(title = "SUPPORT") {
                SettingsActionRow(
                    label = "Contact us",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:palak.singhal.builds@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "MySaarthi Feedback")
                        }
                        context.startActivity(intent)
                    }
                )
            }

            // ACCOUNT section — sign out and delete grouped together, clearly separated by severity
            SettingsSection(title = "ACCOUNT") {
                SettingsActionRow(
                    label = "Sign out",
                    onClick = { showSignOutDialog = true }
                )
                SettingsDivider()
                SettingsActionRow(
                    label = "Delete account",
                    labelColor = MaterialTheme.colorScheme.error,
                    onClick = { showDeleteDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor = Bg,
            title = {
                Text(
                    text = "Sign out?",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = TextInk
                )
            },
            text = {
                Text(
                    text = "You'll need to sign in again to access your sadhana and shloka progress.",
                    fontFamily = FigtreeFamily,
                    color = Neutral700
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    viewModel.signOut()
                    onSignOut()
                }) {
                    Text(
                        text = "Sign out",
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", fontFamily = FigtreeFamily, color = Neutral700)
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isDeleting) showDeleteDialog = false },
            containerColor = Bg,
            title = {
                Text(
                    text = "Delete your account?",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column {
                    Text(
                        text = "This permanently deletes your profile, sadhana history, and satsang connections. This cannot be undone.",
                        fontFamily = FigtreeFamily,
                        color = Neutral700
                    )
                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.error!!.toUserMessage(),
                            fontFamily = FigtreeFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteAccount() },
                    enabled = !uiState.isDeleting
                ) {
                    if (uiState.isDeleting) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Delete permanently",
                            fontFamily = FigtreeFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !uiState.isDeleting
                ) {
                    Text("Cancel", fontFamily = FigtreeFamily, color = Neutral700)
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            fontFamily = FigtreeFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = Accent,
            letterSpacing = 1.sp
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontFamily = FigtreeFamily, fontSize = 14.sp, color = Neutral700)
        Text(
            text = value,
            fontFamily = FigtreeFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = TextInk
        )
    }
}

@Composable
private fun SettingsActionRow(
    label: String,
    onClick: () -> Unit,
    labelColor:Color = TextInk
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = FigtreeFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = labelColor
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(color = Neutral300, thickness = 0.5.dp)
}