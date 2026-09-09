package com.palaksinghal.mysaarthi.core.navigation

sealed class ScreenRoutes(val route :String) {
    object Splash : ScreenRoutes("splash")
    object Welcome : ScreenRoutes("welcome")
    object Login : ScreenRoutes("login")
    object Register : ScreenRoutes("register")
    object Onboarding : ScreenRoutes("onboarding")
    object Home : ScreenRoutes("home")

    // Tab routes — nested inside Home
    object Today : ScreenRoutes("today")
    object Nearby : ScreenRoutes("nearby")
    object You : ScreenRoutes("you")

    // routes on today screen
    object ShlokaDetail : ScreenRoutes("shloka_detail")
    object SadhanaDetail : ScreenRoutes("sadhana_detail")
    object EveningCheckIn : ScreenRoutes("evening_check_in")

    //routes on profile screen
    object EditProfile : ScreenRoutes("edit_profile")
    object Settings : ScreenRoutes("settings")
}