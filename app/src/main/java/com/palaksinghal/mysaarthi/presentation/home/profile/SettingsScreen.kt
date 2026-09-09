package com.palaksinghal.mysaarthi.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.palaksinghal.mysaarthi.presentation.home.profile.YouViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: YouViewModel = hiltViewModel()
) {
    var showSignOutDialog by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsRow(label = "Version", value = "1.0.0")
                    SettingsRow(label = "Built with", value = "Kotlin + Jetpack Compose")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign out
            Button(
                onClick = { showSignOutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Sign out",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error
                )
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
                            Text(
                                text = "Cancel",
                                fontFamily = FigtreeFamily,
                                color = Neutral700
                            )
                        }
                    }
                )
            }

        }
    }
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = FigtreeFamily,
            fontSize = 14.sp,
            color = Neutral700
        )
        Text(
            text = value,
            fontFamily = FigtreeFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = TextInk
        )
    }
}