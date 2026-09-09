package com.palaksinghal.mysaarthi.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palaksinghal.mysaarthi.presentation.home.profile.EditProfileViewModel
import com.palaksinghal.mysaarthi.presentation.onboarding.OnboardingSectionHeader
import com.palaksinghal.mysaarthi.presentation.onboarding.PracticeReminderRow
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.CaprasimoFamily
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral300
import com.palaksinghal.mysaarthi.presentation.theme.Neutral400
import com.palaksinghal.mysaarthi.presentation.theme.Neutral700
import com.palaksinghal.mysaarthi.presentation.theme.Surface
import com.palaksinghal.mysaarthi.presentation.theme.TextInk

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate back on successful save
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.resetSaveSuccess()
            onBack()
        }
    }

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
                    tint = Accent
                )
            }
            Text(
                text = "Edit profile",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Accent
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section label
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "SHOWN TO SEEKERS NEARBY",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = Accent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Your public path",
                    fontFamily = CaprasimoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    color = TextInk
                )
            }

            // Spiritual intro
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "One-line intro",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = Neutral700
                )
                OutlinedTextField(
                    value = uiState.spiritualIntro,
                    onValueChange = { viewModel.updateSpiritualIntro(it) },
                    placeholder = {
                        Text(
                            text = "e.g. Chanting keeps me steady on the marg.",
                            fontFamily = FigtreeFamily,
                            fontSize = 13.sp,
                            color = Neutral400
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Neutral300,
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface,
                        cursorColor = Accent,
                        focusedTextColor = TextInk,
                        unfocusedTextColor = TextInk
                    )
                )
            }

            // Practices
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "PRACTICES SHOWN",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = Accent,
                    letterSpacing = 1.sp
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.practices.forEach { practice ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Accent),
                            onClick = { viewModel.removeCustomPractice(practice) }
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = practice,
                                    fontFamily = FigtreeFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = TextInk
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Neutral400,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Add custom practice
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.customPracticeInput,
                            onValueChange = { viewModel.updateCustomPracticeInput(it) },
                            placeholder = {
                                Text(
                                    "+ Add",
                                    fontFamily = FigtreeFamily,
                                    fontSize = 13.sp,
                                    color = Neutral400
                                )
                            },
                            modifier = Modifier.width(120.dp),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Accent,
                                unfocusedBorderColor = Neutral300,
                                focusedContainerColor = Surface,
                                unfocusedContainerColor = Surface,
                                cursorColor = Accent,
                                focusedTextColor = TextInk,
                                unfocusedTextColor = TextInk
                            )
                        )
                        if (uiState.customPracticeInput.isNotBlank()) {
                            IconButton(
                                onClick = { viewModel.addCustomPractice() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Accent)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Bg,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Reminder times section — add after Practices FlowRow, before satsang toggle
            if (uiState.practices.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "REMINDER TIMES",
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        color = Accent,
                        letterSpacing = 1.sp
                    )
                    uiState.practiceReminders.forEach { reminder ->
                        EditProfileReminderRow(
                            reminder = reminder,
                            onTimeChanged = { hour, minute, amPm ->
                                val practice = reminder["practice"] as? String ?: return@EditProfileReminderRow
                                viewModel.updateReminderTime(practice, hour, minute, amPm)
                            },
                            onToggle = { enabled ->
                                val practice = reminder["practice"] as? String ?: return@EditProfileReminderRow
                                viewModel.toggleReminderEnabled(practice, enabled)
                            }
                        )
                    }
                }
            }

            // Open to satsang toggle
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Open to satsang",
                        modifier = Modifier.weight(1f),
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = TextInk
                    )
                    Switch(
                        checked = uiState.isOpenToSatsang,
                        onCheckedChange = { viewModel.updateIsOpenToSatsang(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Bg,
                            checkedTrackColor = Accent,
                            uncheckedThumbColor = Neutral400,
                            uncheckedTrackColor = Neutral300
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Save button — fixed at bottom
        Button(
            onClick = { viewModel.saveProfile() },
            enabled = !uiState.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Accent)
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    color = Bg,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Save profile",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Bg
                )
            }
        }
    }
}

@Composable
private fun EditProfileReminderRow(
    reminder: Map<String, Any>,
    onTimeChanged: (hour: Int, minute: Int, amPm: String) -> Unit,
    onToggle: (Boolean) -> Unit
) {
    val practice = reminder["practice"] as? String ?: ""
    val hour = (reminder["hour"] as? Long)?.toInt() ?: (reminder["hour"] as? Int) ?: 7
    val minute = (reminder["minute"] as? Long)?.toInt() ?: (reminder["minute"] as? Int) ?: 0
    val amPm = reminder["amPm"] as? String ?: "AM"
    val isEnabled = reminder["isEnabled"] as? Boolean ?: true

    var showTimePicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (isEnabled) Surface else Bg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isEnabled) Neutral300 else Neutral300
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = practice,
                modifier = Modifier.weight(1f),
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = if (isEnabled) TextInk else Neutral400
            )

            Text(
                text = "%d:%02d %s".format(hour, minute, amPm),
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (isEnabled) Accent else Neutral400,
                modifier = Modifier
                    .clickable { showTimePicker = true }
                    .padding(4.dp)
            )

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Bg,
                    checkedTrackColor = Accent,
                    uncheckedThumbColor = Neutral400,
                    uncheckedTrackColor = Neutral300
                )
            )
        }
    }

    if (showTimePicker) {
        com.palaksinghal.mysaarthi.presentation.onboarding.WheelTimePickerDialog(
            initialHour = hour,
            initialMinute = minute,
            initialAmPm = amPm,
            onConfirm = { h, m, ap ->
                onTimeChanged(h, m, ap)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}