package com.palaksinghal.mysaarthi.presentation.home.today

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.CaprasimoFamily
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral300
import com.palaksinghal.mysaarthi.presentation.theme.Neutral400
import com.palaksinghal.mysaarthi.presentation.theme.Neutral700
import com.palaksinghal.mysaarthi.presentation.theme.Surface
import com.palaksinghal.mysaarthi.presentation.theme.TextInk

@Composable
fun EveningCheckInScreen(
    onBack: () -> Unit
) {
    var reflection by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<String?>(null) }

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
                text = "Evening check-in",
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Question
            Text(
                text = "How was your\npath today?",
                fontFamily = CaprasimoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                color = TextInk,
                lineHeight = 36.sp
            )

            Text(
                text = "A moment to be honest with yourself — no pressure, just presence.",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Neutral700,
                lineHeight = 22.sp
            )

            // Mood selection
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "TODAY I",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = Accent,
                    letterSpacing = 1.sp
                )
                listOf(
                    "Stayed on the path",
                    "Mostly followed through",
                    "Struggled today"
                ).forEach { option ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (selectedMood == option) Accent else Surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedMood == option) Accent else Neutral300
                        ),
                        onClick = { selectedMood = option },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = option,
                            fontFamily = FigtreeFamily,
                            fontWeight = if (selectedMood == option)
                                FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = if (selectedMood == option) Bg else TextInk,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 14.dp
                            )
                        )
                    }
                }
            }

            // Reflection text field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ONE THOUGHT (OPTIONAL)",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = Accent,
                    letterSpacing = 1.sp
                )
                OutlinedTextField(
                    value = reflection,
                    onValueChange = { reflection = it },
                    placeholder = {
                        Text(
                            text = "What stayed with you today?",
                            fontFamily = FigtreeFamily,
                            fontSize = 14.sp,
                            color = Neutral400
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 5,
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Saving coming soon — for now, just reflect.",
                fontFamily = FigtreeFamily,
                fontSize = 12.sp,
                color = Neutral400
            )
        }
    }
}