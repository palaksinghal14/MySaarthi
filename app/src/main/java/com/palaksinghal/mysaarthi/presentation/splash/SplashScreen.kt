package com.palaksinghal.mysaarthi.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palaksinghal.mysaarthi.R
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.CaprasimoFamily
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral700
import com.palaksinghal.mysaarthi.presentation.util.toUserMessage


@Composable
fun SplashScreen(
    viewModel: SplashViewModel= hiltViewModel(),
    onNavToWelcomeScreen :()->Unit,
    onNavToOnboardingScreen :()->Unit,
    onNavToHomeScreen :()->Unit,
){
    val splashUiState by viewModel.splashUiState.collectAsStateWithLifecycle()

    LaunchedEffect(splashUiState) {
        when(splashUiState){
            is SplashUiState.OnNavToWelcome -> { onNavToWelcomeScreen()}
            is SplashUiState.OnNavToOnboarding -> { onNavToOnboardingScreen()}
            is SplashUiState.OnNavToHome -> { onNavToHomeScreen() }
           else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg),
        contentAlignment = Alignment.Center
    ) {
        when (splashUiState) {

            is SplashUiState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mysaarthi_logo),
                        contentDescription = "MySaarthi logo",
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier.size(96.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "MySaarthi",
                        fontFamily = CaprasimoFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 36.sp,
                        color = Accent
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = (splashUiState as SplashUiState.Error).exception.toUserMessage(),
                        fontFamily = FigtreeFamily,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.retry() },
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) {
                        Text(
                            text = "Try again",
                            fontFamily = FigtreeFamily,
                            fontWeight = FontWeight.Medium,
                            color = Bg
                        )
                    }
                }
            } else -> {
                // All non-error states show the same centered logo layout
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    // Logo icon — saffron rounded square with flame
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mysaarthi_logo),
                        contentDescription = "MySaarthi logo",
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier.size(96.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // App name
                    Text(
                        text = "MySaarthi",
                        fontFamily = CaprasimoFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 36.sp,
                        color = Accent
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tagline
                    Text(
                        text = "Your companion on the spiritual path — find your practice, find your people.",
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Neutral700,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}