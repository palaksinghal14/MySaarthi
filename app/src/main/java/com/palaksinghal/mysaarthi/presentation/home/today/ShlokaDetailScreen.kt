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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palaksinghal.mysaarthi.domain.model.Shloka
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.CaprasimoFamily
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral300
import com.palaksinghal.mysaarthi.presentation.theme.Neutral700
import com.palaksinghal.mysaarthi.presentation.theme.Surface
import com.palaksinghal.mysaarthi.presentation.theme.Terracotta100
import com.palaksinghal.mysaarthi.presentation.theme.Terracotta700
import com.palaksinghal.mysaarthi.presentation.theme.TextInk

@Composable
fun ShlokaDetailScreen(
    onBack: () -> Unit,
    viewModel: ShlokaDetailViewModel = hiltViewModel()
) {
    val shloka by viewModel.shloka.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        if (shloka == null) {
            CircularProgressIndicator(
                color = Accent,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Center),
                strokeWidth = 2.dp
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                        text = "Bhagavad Gita · ${shloka!!.chapter}.${shloka!!.verse}",
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Accent
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Chapter context pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Terracotta100
                    ) {
                        Text(
                            text = "Chapter ${shloka!!.chapter} · Verse ${shloka!!.verse}",
                            fontFamily = FigtreeFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = Terracotta700,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    // Sanskrit
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionLabel(text = "SANSKRIT")
                        Text(
                            text = shloka!!.shlokaSanskrit,
                            fontFamily = CaprasimoFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp,
                            color = TextInk,
                            lineHeight = 32.sp
                        )
                    }

                    Divider(color = Neutral300, thickness = 1.dp)

                    // Hindi
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionLabel(text = "HINDI")
                        Text(
                            text = shloka!!.hindiTranslation,
                            fontFamily = FigtreeFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = TextInk,
                            lineHeight = 26.sp
                        )
                    }

                    Divider(color = Neutral300, thickness = 1.dp)

                    // English
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionLabel(text = "ENGLISH")
                        Text(
                            text = shloka!!.englishTranslation,
                            fontFamily = FigtreeFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Neutral700,
                            lineHeight = 26.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        color = Accent,
        letterSpacing = 1.5.sp
    )
}