package com.palaksinghal.mysaarthi.presentation.home.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.CaprasimoFamily
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral200
import com.palaksinghal.mysaarthi.presentation.theme.Neutral300
import com.palaksinghal.mysaarthi.presentation.theme.Neutral400
import com.palaksinghal.mysaarthi.presentation.theme.Neutral700
import com.palaksinghal.mysaarthi.presentation.theme.Surface
import com.palaksinghal.mysaarthi.presentation.theme.Terracotta100
import com.palaksinghal.mysaarthi.presentation.theme.Terracotta300
import com.palaksinghal.mysaarthi.presentation.theme.Terracotta500
import com.palaksinghal.mysaarthi.presentation.theme.Terracotta700
import com.palaksinghal.mysaarthi.presentation.theme.TextInk

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun YouScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: YouViewModel = hiltViewModel()
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
                        .size(28.dp)
                        .align(Alignment.Center),
                    strokeWidth = 2.dp
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Top bar — edit + settings icons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                            IconButton(onClick = onNavigateToEditProfile) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit profile",
                                    tint = Neutral700
                                )
                            }
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = Neutral700
                                )
                            }

                    }

                    // Profile header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Initials avatar
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Terracotta100),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.displayName
                                    .split(" ")
                                    .take(2)
                                    .joinToString("") { it.firstOrNull()?.uppercase() ?: "" },
                                fontFamily = FigtreeFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Accent
                            )
                        }

                        Column {
                            Text(
                                text = uiState.displayName,
                                fontFamily = CaprasimoFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 22.sp,
                                color = TextInk
                            )
                            if (uiState.howLongOnPath.isNotBlank()) {
                                Text(
                                    text = uiState.howLongOnPath + " on this path",
                                    fontFamily = FigtreeFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = Neutral700
                                )
                            }
                        }
                    }

                    // Spiritual intro
                    if (uiState.spiritualIntro.isNotBlank()) {
                        Text(
                            text = "\"${uiState.spiritualIntro}\"",
                            fontFamily = FigtreeFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Neutral700,
                            lineHeight = 22.sp
                        )
                    }

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            value = uiState.currentStreak.toString(),
                            label = "current\nstreak",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            value = uiState.longestStreak.toString(),
                            label = "longest\nstreak",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            value = uiState.daysPracticed.toString(),
                            label = "days\npracticed",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // 30-day grid
                    if (uiState.last30Days.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Last 30 days",
                                    fontFamily = FigtreeFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = TextInk
                                )
                                val activeDays = uiState.last30Days.count { it > 0f }
                                Text(
                                    text = "${(activeDays * 100 / 30)}% days active",
                                    fontFamily = FigtreeFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = Neutral400
                                )
                            }
                            ActivityGrid(days = uiState.last30Days)
                        }
                    }

                    // Practices
                    if (uiState.practices.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "PRACTICES",
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
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp, Neutral300
                                        )
                                    ) {
                                        Text(
                                            text = practice,
                                            fontFamily = FigtreeFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 13.sp,
                                            color = TextInk,
                                            modifier = Modifier.padding(
                                                horizontal = 14.dp,
                                                vertical = 6.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Neutral300)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                fontFamily = CaprasimoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                color = Accent
            )
            Text(
                text = label,
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = Neutral400,
                lineHeight = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ActivityGrid(days: List<Float>) {
    val columns = 10
    val rows = 3

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        (0 until rows).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (0 until columns).forEach { col ->
                    val index = row * columns + col
                    val ratio = if (index < days.size) days[index] else 0f
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(getGridColor(ratio))
                    )
                }
            }
        }
    }
}

private fun getGridColor(ratio: Float) = when {
    ratio == 0f -> Neutral200
    ratio <= 0.33f -> Terracotta100
    ratio <= 0.66f -> Terracotta300
    ratio < 1f -> Terracotta500
    else -> com.palaksinghal.mysaarthi.presentation.theme.Accent
}