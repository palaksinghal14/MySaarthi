package com.palaksinghal.mysaarthi.presentation.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palaksinghal.mysaarthi.R
import com.palaksinghal.mysaarthi.domain.model.PracticeReminder
import com.palaksinghal.mysaarthi.presentation.theme.*
import com.palaksinghal.mysaarthi.presentation.util.toUserMessage

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    LaunchedEffect(saveState) {
        if (saveState is OnboardingUiState.Success) {
            viewModel.resetSaveState()
            onOnboardingComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Bg)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 48.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {

            // ── SECTION 1 — Name ──────────────────────────────────────────
            item {
                OnboardingSectionHeader(
                    tag = "YOUR NAME",
                    title = "What should we call you?",
                    subtitle = "This is how MySaarthi will greet you."
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = formState.displayName,
                    onValueChange = { viewModel.updateDisplayName(it) },
                    placeholder = {
                        Text(
                            "Your name",
                            fontFamily = FigtreeFamily,
                            color = Neutral400
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
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

            // ── SECTION 2 — How long on path ─────────────────────────────
            item {
                OnboardingSectionHeader(
                    tag = "YOUR JOURNEY",
                    title = "How long have you walked this path?",
                    subtitle = "No wrong answer — every step counts."
                )
                Spacer(modifier = Modifier.height(20.dp))
                val options = listOf(
                    "Just beginning",
                    "Under a year",
                    "1 – 3 years",
                    "3 – 10 years",
                    "10+ years"
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    options.forEach { option ->
                        SelectableRow(
                            label = option,
                            isSelected = formState.howLongOnPath == option,
                            isMultiSelect = false,
                            onClick = { viewModel.updateHowLongOnPath(option) }
                        )
                    }
                }
            }

            // ── SECTION 3 — Practices ─────────────────────────────────────
            item {
                OnboardingSectionHeader(
                    tag = "YOUR SADHANA",
                    title = "Which practices do you want to keep?",
                    subtitle = "Pick any — this becomes your daily checklist."
                )
                Spacer(modifier = Modifier.height(20.dp))
                val presetPractices = listOf(
                    "Brahma muhurta" to R.drawable.ic_brahma_muhurta,
                    "Daily paath" to R.drawable.ic_daily_paath,
                    "Satwik diet" to R.drawable.ic_satwik_diet,
                    "Meditation" to R.drawable.ic_meditation,
                    "Kirtan / chanting" to R.drawable.ic_kirtan
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    presetPractices.forEach { (practice, icon) ->
                        SelectableRow(
                            label = practice,
                            icon = icon,
                            isSelected = practice in formState.selectedPractices,
                            isMultiSelect = true,
                            onClick = { viewModel.togglePractice(practice) }
                        )
                    }
                    // Show added custom practices below presets
                    val presetNames = listOf(
                        "Brahma muhurta", "Daily paath",
                        "Satwik diet", "Meditation", "Kirtan / chanting"
                    )
                    val customPractices = formState.selectedPractices.filter { it !in presetNames }
                    if (customPractices.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        customPractices.forEach { practice ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Terracotta100)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Accent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Bg,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = practice,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Terracotta700
                                )
                                IconButton(
                                    onClick = { viewModel.removeCustomPractice(practice) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove $practice",
                                        tint = Neutral600,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    // Custom practice input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.customPracticeInput,
                            onValueChange = { viewModel.updateCustomPracticeInput(it) },
                            placeholder = {
                                Text(
                                    "Add your own practice",
                                    fontFamily = FigtreeFamily,
                                    fontSize = 14.sp,
                                    color = Neutral400
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
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
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Accent)
                                .clickable { viewModel.addCustomPractice() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add practice",
                                tint = Bg,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // ── SECTION 4 — Reminder times ────────────────────────────────
            if (formState.selectedPractices.isNotEmpty()) {
                item {
                    OnboardingSectionHeader(
                        tag = "GENTLE NUDGES",
                        title = "When should we remind you?",
                        subtitle = "Local reminders — they work even you are offline."
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        formState.practiceReminders.forEach { reminder ->
                            PracticeReminderRow(
                                reminder = reminder,
                                onTimeChanged = { hour, minute, amPm ->
                                    viewModel.updatePracticeReminderTime(
                                        reminder.practice, hour, minute, amPm
                                    )
                                },
                                onToggle = { enabled ->
                                    viewModel.togglePracticeReminder(reminder.practice, enabled)
                                }
                            )
                        }
                    }
                }
            }

            // ── SECTION 5 — Satsang & community ──────────────────────────
            item {
                OnboardingSectionHeader(
                    tag = "YOUR PEOPLE",
                    title = "Find your kind of people nearby?",
                    subtitle = "Only your path is shared — never your exact location or personal details."
                )
                Spacer(modifier = Modifier.height(20.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ToggleRow(
                        icon = R.drawable.ic_location,
                        label = "Use my location",
                        checked = formState.useLocation,
                        onCheckedChange = { viewModel.updateUseLocation(it) }
                    )
                    ToggleRow(
                        icon = R.drawable.ic_satsang,
                        label = "Open to satsang requests",
                        checked = formState.isOpenToSatsang,
                        onCheckedChange = { viewModel.updateIsOpenToSatsang(it) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "One line about yourself and your path (optional)",
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Neutral700
                    )
                    OutlinedTextField(
                        value = formState.spiritualIntro,
                        onValueChange = { viewModel.updateSpiritualIntro(it) },
                        placeholder = {
                            Text(
                                "e.g. Chanting keeps me steady on the marg.",
                                fontFamily = FigtreeFamily,
                                fontSize = 13.sp,
                                color = Neutral400
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
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
            }

            // ── Error ──────────────────────────────────────────────────────
            if (saveState is OnboardingUiState.Error) {
                item {
                    Text(
                        text = (saveState as OnboardingUiState.Error).exception.toUserMessage(),
                        fontFamily = FigtreeFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // ── Fixed bottom button ────────────────────────────────────────────
        Button(
            onClick = { viewModel.completeOnboarding() },
            enabled = saveState !is OnboardingUiState.Loading,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Accent)
        ) {
            if (saveState is OnboardingUiState.Loading) {
                CircularProgressIndicator(
                    color = Bg,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Finish setup",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Bg
                )
            }
        }
    }
}

// ── Shared section header ──────────────────────────────────────────────────
@Composable
fun OnboardingSectionHeader(
    tag: String,
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = tag,
            fontFamily = FigtreeFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = Accent,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextInk
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral700
        )
    }
}

// ── Selectable row (single or multi-select) ────────────────────────────────
@Composable
fun SelectableRow(
    label: String,
    isSelected: Boolean,
    isMultiSelect: Boolean,
    onClick: () -> Unit,
    icon: Int? = null
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Accent else Neutral300,
        label = "border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Terracotta100 else Surface,
        label = "bg"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Accent else Neutral200),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = if (isSelected) Bg else Neutral600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) Terracotta700 else TextInk
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Accent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Bg,
                        modifier = Modifier.size(13.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .padding(1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(20.dp),
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = BorderStroke(1.5.dp, Neutral300)
                        ) {}
                    }
                }
            }
        }
    }
}

// ── Toggle row ──────────────────────────────────────────────────────────────
@Composable
fun ToggleRow(
    icon: Int,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Neutral300)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Neutral600,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = TextInk
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Bg,
                    checkedTrackColor = Accent,
                    uncheckedThumbColor = Neutral400,
                    uncheckedTrackColor = Neutral300
                )
            )
        }
    }
}

// ── Practice reminder row with inline time picker ──────────────────────────
@Composable
fun PracticeReminderRow(
    reminder: PracticeReminder,
    onTimeChanged: (hour: Int, minute: Int, amPm: String) -> Unit,
    onToggle: (Boolean) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (reminder.isEnabled) Terracotta100 else Surface,
        border = BorderStroke(
            1.dp,
            if (reminder.isEnabled) Accent else Neutral300
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = reminder.practice,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = if (reminder.isEnabled) TextInk else Neutral500
            )
            // Time display — tap to open picker
            Text(
                text = "%d:%02d %s".format(reminder.hour, reminder.minute, reminder.amPm),
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = if (reminder.isEnabled) Accent else Neutral400,
                modifier = Modifier.clickable { showTimePicker = true }
            )
            Switch(
                checked = reminder.isEnabled,
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

    // Time picker dialog
    if (showTimePicker) {
        WheelTimePickerDialog(
            initialHour = reminder.hour,
            initialMinute = reminder.minute,
            initialAmPm = reminder.amPm,
            onConfirm = { hour, minute, amPm ->
                onTimeChanged(hour, minute, amPm)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

// ── Wheel time picker dialog ───────────────────────────────────────────────
@Composable
fun WheelTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    initialAmPm: String,
    onConfirm: (hour: Int, minute: Int, amPm: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }
    var selectedAmPm by remember { mutableStateOf(initialAmPm) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Bg,
        title = {
            Text(
                text = "Set reminder time",
                style = MaterialTheme.typography.titleMedium,
                color = TextInk
            )
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth() .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hour picker
                WheelColumn(
                    items = (1..12).map { it.toString() },
                    selectedIndex = selectedHour - 1,
                    onSelected = { selectedHour = it + 1 },
                    label = "HH"
                )
                Text(":", fontFamily = FigtreeFamily, fontSize = 22.sp, color = TextInk)
                // Minute picker
                WheelColumn(
                    items = (0..59).map { "%02d".format(it) },
                    selectedIndex = selectedMinute,
                    onSelected = { selectedMinute = it },
                    label = "MM"
                )
                // AM/PM picker
                WheelColumn(
                    items = listOf("AM", "PM"),
                    selectedIndex = if (selectedAmPm == "AM") 0 else 1,
                    onSelected = { selectedAmPm = if (it == 0) "AM" else "PM" },
                    label = "AM/PM"
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedHour, selectedMinute, selectedAmPm) }) {
                Text("Set", fontFamily = FigtreeFamily, color = Accent, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = FigtreeFamily, color = Neutral600)
            }
        }
    )
}

// ── Wheel column (single drum-roll column) ─────────────────────────────────
@Composable
fun WheelColumn(
    items: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    label: String
) {
    val itemHeightDp = 40.dp
    val visibleItems = 3
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (selectedIndex - 1).coerceAtLeast(0)
    )

    // Snap to nearest item when scroll settles
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val index = listState.firstVisibleItemIndex +
                    if (listState.firstVisibleItemScrollOffset > 20) 1 else 0
            val snappedIndex = index.coerceIn(0, items.size - 1)
            listState.animateScrollToItem(snappedIndex)
            onSelected(snappedIndex)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontFamily = FigtreeFamily,
            fontSize = 10.sp,
            color = Neutral400,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .width(64.dp)
                .height(itemHeightDp * visibleItems)
        ) {
            // Highlight bar behind the selected item
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(itemHeightDp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Terracotta100)
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = itemHeightDp)
            ) {
                itemsIndexed(items) { index, item ->
                    val isSelected = index ==
                            (listState.firstVisibleItemIndex +
                                    if (listState.firstVisibleItemScrollOffset > 20) 1 else 0)
                                .coerceIn(0, items.size - 1)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeightDp)
                            .clickable {
                                onSelected(index)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item,
                            fontFamily = FigtreeFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = if (isSelected) 18.sp else 14.sp,
                            color = if (isSelected) Accent else Neutral400
                        )
                    }
                }
            }
        }
    }
}
