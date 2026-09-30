package com.palaksinghal.mysaarthi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.palaksinghal.mysaarthi.core.navigation.MySaarthiApp

import com.palaksinghal.mysaarthi.presentation.theme.MySaarthiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?)  {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySaarthiTheme {
                MySaarthiApp()
            }
        }
    }
}
