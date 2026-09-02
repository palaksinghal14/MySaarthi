package com.palaksinghal.mysaarthi.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.palaksinghal.mysaarthi.presentation.authentication.LoginScreen
import com.palaksinghal.mysaarthi.presentation.authentication.RegisterScreen
import com.palaksinghal.mysaarthi.presentation.onboarding.OnboardingScreen
import com.palaksinghal.mysaarthi.presentation.splash.SplashScreen
import com.palaksinghal.mysaarthi.presentation.welcome.WelcomeScreen


@Composable
fun MySaarthiApp(){

    val mainNavController  = rememberNavController()

    NavHost(navController = mainNavController , startDestination = ScreenRoutes.Splash.route){
        composable(route= ScreenRoutes.Splash.route){
            SplashScreen(
                onNavToWelcomeScreen = { mainNavController.navigate(ScreenRoutes.Welcome.route) },
                onNavToOnboardingScreen = { mainNavController.navigate(ScreenRoutes.Onboarding.route) },
                onNavToHomeScreen ={ mainNavController.navigate(ScreenRoutes.Home.route) }
            )
        }
        composable(route= ScreenRoutes.Welcome.route){
            WelcomeScreen()
        }
        composable(route= ScreenRoutes.Login.route){
            LoginScreen(
                onLoginSuccess = { mainNavController.navigate(ScreenRoutes.Home.route) },
                onNavigateToRegister = {mainNavController.navigate(ScreenRoutes.Register.route)}
            )
        }
        composable(route= ScreenRoutes.Register.route){
            RegisterScreen(
                onRegisterSuccess = {mainNavController.navigate(ScreenRoutes.Home.route)},
                onNavigateToLogin = {mainNavController.navigate(ScreenRoutes.Login.route)}
            )
        }
        composable(route= ScreenRoutes.Onboarding.route){
            OnboardingScreen(
                onOnboardingComplete = {
                    mainNavController.navigate(ScreenRoutes.Home.route){
                        popUpTo(ScreenRoutes.Onboarding.route){inclusive=true}
                    }
                }
            )

        }
        composable(route= ScreenRoutes.Home.route){

        }

    }
}