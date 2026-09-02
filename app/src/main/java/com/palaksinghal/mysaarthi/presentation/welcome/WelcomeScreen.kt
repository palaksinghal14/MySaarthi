package com.palaksinghal.mysaarthi.presentation.welcome

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palaksinghal.mysaarthi.R
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.CaprasimoFamily
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral200
import com.palaksinghal.mysaarthi.presentation.theme.Neutral700
import com.palaksinghal.mysaarthi.presentation.theme.Sage100
import com.palaksinghal.mysaarthi.presentation.theme.Sage600
import com.palaksinghal.mysaarthi.presentation.theme.TextInk
import com.palaksinghal.mysaarthi.presentation.theme.Terracotta100

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onAlreadyHaveAccount: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top content
            Column {
                // App logo
                Icon(
                    painter = painterResource(id = R.drawable.ic_mysaarthi_logo),
                    contentDescription = "MySaarthi logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tag line
                Text(
                    text = "WELCOME TO MYSAARTHI",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = Accent,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Headline
                Text(
                    text = "Walk your path —\nnever alone.",
                    fontFamily = CaprasimoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp,
                    color = TextInk,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Two things, done simply.",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Neutral700
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Feature 1 — Daily companion
                FeatureRow(
                    icon = R.drawable.ic_brahma_muhurta,
                    iconBgColor = Terracotta100,
                    iconTint = Accent,
                    title = "A daily companion",
                    subtitle = "Your shloka, sadhana and evening check-in — from morning to night."
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Feature 2 — Nearby seekers
                FeatureRow(
                    icon = R.drawable.ic_satsang,
                    iconBgColor = Sage100,
                    iconTint = Sage600,
                    title = "Your people nearby",
                    subtitle = "Find seekers and spiritual places walking the same Bhagwat Marg."
                )
            }

            // Bottom buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text(
                        text = "Get started",
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Bg
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onAlreadyHaveAccount) {
                    Text(
                        text = "I already have an account",
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Accent
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(
    icon: Int,
    iconBgColor: androidx.compose.ui.graphics.Color,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Column {
            Text(
                text = title,
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextInk
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Neutral700,
                lineHeight = 18.sp
            )
        }
    }
}