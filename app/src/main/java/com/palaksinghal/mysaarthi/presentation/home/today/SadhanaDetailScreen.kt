package com.palaksinghal.mysaarthi.presentation.home.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.palaksinghal.mysaarthi.core.navigation.ScreenRoutes
import com.palaksinghal.mysaarthi.domain.model.PracticeReminder
import com.palaksinghal.mysaarthi.domain.model.SadhanaEntry
import com.palaksinghal.mysaarthi.presentation.home.HomeViewModel
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.CaprasimoFamily
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral300
import com.palaksinghal.mysaarthi.presentation.theme.Neutral400
import com.palaksinghal.mysaarthi.presentation.theme.Neutral700
import com.palaksinghal.mysaarthi.presentation.theme.Surface
import com.palaksinghal.mysaarthi.presentation.theme.Terracotta100
import com.palaksinghal.mysaarthi.presentation.theme.Terracotta700
import com.palaksinghal.mysaarthi.presentation.theme.TextInk

@Composable
fun SadhanaDetailScreen(
    onBack: () -> Unit,
    navController: NavController,
    backStackEntry: NavBackStackEntry
) {
    // Share HomeViewModel from Today screen's back stack entry
    val todayEntry = remember(backStackEntry) {
        navController.getBackStackEntry(ScreenRoutes.Today.route)
    }
    val viewModel: HomeViewModel = hiltViewModel(todayEntry)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

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
                    text = "Today's sadhana",
                    fontFamily = CaprasimoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    color = TextInk
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Progress summary
                val completed = uiState.sadhanaEntries.count { it.isCompleted }
                val total = uiState.sadhanaEntries.size

                Text(
                    text = "$completed of $total practices done today",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Neutral700
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Practice rows with reminder times
                uiState.sadhanaEntries.forEach { entry ->
                    // Find matching reminder from user profile
                    val reminder = uiState.practiceReminders.find { reminder ->
                        (reminder["practice"] as? String) == entry.practice
                    }
                    SadhanaDetailRow(
                        entry = entry,
                        reminderTime = formatReminderTime(reminder),
                        onToggle = { isCompleted ->
                            viewModel.onToggle(entry.practice, isCompleted)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SadhanaDetailRow(
    entry: SadhanaEntry,
    reminderTime: String?,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (entry.isCompleted) Terracotta100 else Surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (entry.isCompleted) Accent else Neutral300
        ),
        onClick = { onToggle(!entry.isCompleted) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Completion indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (entry.isCompleted) Accent else Neutral300)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.practice,
                    fontFamily = FigtreeFamily,
                    fontWeight = if (entry.isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 15.sp,
                    color = if (entry.isCompleted) Terracotta700 else TextInk
                )
                if (reminderTime != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reminderTime,
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = Neutral400
                    )
                }
            }

            Text(
                text = if (entry.isCompleted) "Done" else "Mark done",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = if (entry.isCompleted) Accent else Neutral400
            )
        }
    }
}

private fun formatReminderTime(reminder: Map<String, Any>?): String? {
    if (reminder == null) return null
    val hour = (reminder["hour"] as? Long)?.toInt() ?: return null
    val minute = (reminder["minute"] as? Long)?.toInt() ?: return null
    val amPm = reminder["amPm"] as? String ?: return null
    return "%d:%02d %s".format(hour, minute, amPm)
}