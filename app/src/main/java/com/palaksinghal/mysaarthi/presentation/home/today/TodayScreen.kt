package com.palaksinghal.mysaarthi.presentation.home.today

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palaksinghal.mysaarthi.R
import com.palaksinghal.mysaarthi.domain.model.SadhanaEntry
import com.palaksinghal.mysaarthi.domain.model.Shloka
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TodayScreen(
    onShlokaClick: () -> Unit,
    onSadhanaClick: () -> Unit,
    onEveningCheckInClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    color = Accent,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center),
                    strokeWidth = 2.dp
                )
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Something went wrong",
                        fontFamily = FigtreeFamily,
                        fontSize = 16.sp,
                        color = TextInk,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please restart the app",
                        fontFamily = FigtreeFamily,
                        fontSize = 14.sp,
                        color = Neutral700,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header
                    TodayHeader(
                        displayName = uiState.displayName,
                        shlokaNumber = uiState.shloka?.shlokaNumber ?: 0
                    )

                    // Shloka card
                    uiState.shloka?.let { shloka ->
                        ShlokaCard(
                            shloka = shloka,
                            onAdvance = { viewModel.advanceNextShloka() },
                            onClick = onShlokaClick
                        )
                    }

                    // Sadhana checklist
                    if (uiState.sadhanaEntries.isNotEmpty()) {
                        SadhanaCard(
                            entries = uiState.sadhanaEntries,
                            onToggle = { practice, isCompleted ->
                                viewModel.onToggle(practice, isCompleted)
                            },
                            onClick = onSadhanaClick
                        )
                    }

                    EveningCheckInCard(
                        onClick = onEveningCheckInClick
                    )
                    // Evening check-in — only shows after 6 PM
                   // if (uiState.isEvening) {
                  //      EveningCheckInCard()
                  //  }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun TodayHeader(
    displayName: String,
    shlokaNumber: Int
) {
    val today = LocalDate.now()
    val dayOfWeek = today.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)).uppercase()
    val date = today.format(DateTimeFormatter.ofPattern("d MMMM", Locale.ENGLISH)).uppercase()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "$dayOfWeek · $date",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = Accent,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Namaste, $displayName",
                fontFamily = CaprasimoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 26.sp,
                color = TextInk
            )
        }

        if (shlokaNumber > 0) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Terracotta100
            ) {
                Text(
                    text = "Day $shlokaNumber",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Terracotta700,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun ShlokaCard(
    shloka: Shloka,
    onAdvance: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Surface,
        border = BorderStroke(1.dp, Neutral300),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Chapter.Verse label
            Text(
                text = "BHAGAVAD GITA · ${shloka.chapter}.${shloka.verse}",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = Accent,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sanskrit text
            Text(
                text = shloka.shlokaSanskrit,
                fontFamily = CaprasimoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                color = TextInk,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // English translation
            Text(
                text = shloka.englishTranslation,
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Neutral700,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Next shloka button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAdvance() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_daily_paath),
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Next shloka",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Accent
                )
            }
        }
    }
}

@Composable
private fun SadhanaCard(
    entries: List<SadhanaEntry>,
    onToggle: (practice: String, isCompleted: Boolean) -> Unit,
    onClick: () -> Unit
) {
    val completedCount = entries.count { it.isCompleted }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Bg,
        border = BorderStroke(1.dp, Neutral300),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's sadhana",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = TextInk
                )
                Text(
                    text = "$completedCount of ${entries.size}",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = Neutral400
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            entries.forEach { entry ->
                SadhanaEntryRow(
                    entry = entry,
                    onToggle = { isCompleted -> onToggle(entry.practice, isCompleted) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SadhanaEntryRow(
    entry: SadhanaEntry,
    onToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!entry.isCompleted) }
    ) {
        // Custom checkbox
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .then(
                    if (entry.isCompleted) {
                        Modifier.background(Accent)
                    } else {
                        Modifier
                            .background(Color.Transparent)
                            .border(1.5.dp, Neutral300, CircleShape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (entry.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Bg,
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        Text(
            text = entry.practice,
            fontFamily = FigtreeFamily,
            fontWeight = if (entry.isCompleted) FontWeight.Medium else FontWeight.Normal,
            fontSize = 14.sp,
            color = if (entry.isCompleted) Accent else TextInk
        )
    }
}

@Composable
private fun EveningCheckInCard(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Terracotta100,
        border = BorderStroke(1.dp, Accent),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "EVENING CHECK-IN",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = Accent,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "How was your path today?",
                fontFamily = CaprasimoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = TextInk
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "A moment to be honest with yourself — no pressure, just presence.",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Neutral700,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Placeholder — evening check-in feature to be built in Phase 4
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("Stayed on path", "Mostly", "Not today").forEach { option ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Bg,
                        border = BorderStroke(1.dp, Neutral300),
                        modifier = Modifier.clickable { }
                    ) {
                        Text(
                            text = option,
                            fontFamily = FigtreeFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = TextInk,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}