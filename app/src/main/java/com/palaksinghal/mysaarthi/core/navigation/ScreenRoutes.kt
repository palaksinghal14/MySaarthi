package com.palaksinghal.mysaarthi.core.navigation

sealed class ScreenRoutes(val route :String) {
    object Splash : ScreenRoutes("splash")
    object Welcome : ScreenRoutes("welcome")
    object Login : ScreenRoutes("login")
    object Register : ScreenRoutes("register")
    object Onboarding : ScreenRoutes("onboarding")
    object Home : ScreenRoutes("home")
}