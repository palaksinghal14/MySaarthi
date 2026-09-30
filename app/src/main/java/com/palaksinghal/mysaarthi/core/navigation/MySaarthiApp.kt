package com.palaksinghal.mysaarthi.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.palaksinghal.mysaarthi.presentation.authentication.LoginScreen
import com.palaksinghal.mysaarthi.presentation.authentication.RegisterScreen
import com.palaksinghal.mysaarthi.presentation.home.HomeShell
import com.palaksinghal.mysaarthi.presentation.onboarding.OnboardingScreen
import com.palaksinghal.mysaarthi.presentation.splash.SplashScreen
import com.palaksinghal.mysaarthi.presentation.welcome.WelcomeScreen


@Composable
fun MySaarthiApp(){

    val mainNavController  = rememberNavController()

    LaunchedEffect(mainNavController) {
        mainNavController.currentBackStack.collect { entries ->
            android.util.Log.d("NavDebug", "Stack: ${entries.map { it.destination.route }}")
        }
    }

    NavHost(navController = mainNavController , startDestination = ScreenRoutes.Splash.route){
        composable(route= ScreenRoutes.Splash.route){
            SplashScreen(
                onNavToWelcomeScreen = {
                     mainNavController.navigate(ScreenRoutes.Welcome.route){
                        popUpTo(ScreenRoutes.Splash.route) { inclusive = true }
                     } },
                onNavToOnboardingScreen = {
                    mainNavController.navigate(ScreenRoutes.Onboarding.route){
                    popUpTo(ScreenRoutes.Splash.route) { inclusive = true }
                } },
                onNavToHomeScreen ={
                    mainNavController.navigate(ScreenRoutes.Home.route) {
                        popUpTo(ScreenRoutes.Splash.route) { inclusive = true }
                    }}
            )
        }
        composable(route= ScreenRoutes.Welcome.route){
            WelcomeScreen(
                onGetStarted = { mainNavController.navigate(ScreenRoutes.Register.route)},
                onAlreadyHaveAccount ={  mainNavController.navigate(ScreenRoutes.Login.route)}
            )
        }
        composable(route= ScreenRoutes.Login.route){
            LoginScreen(
                onLoginSuccessGoHome ={ mainNavController.navigate(ScreenRoutes.Home.route){
                    popUpTo(ScreenRoutes.Welcome.route) {
                        inclusive = true
                    }
                    launchSingleTop=true
                } },
                onLoginSuccessGoOnboarding = {mainNavController.navigate(ScreenRoutes.Onboarding.route){
                    popUpTo(ScreenRoutes.Welcome.route) {
                        inclusive = true
                    }
                    launchSingleTop=true
                }},
                onNavigateToRegister = {
                    mainNavController.navigate(ScreenRoutes.Register.route) {
                        popUpTo(ScreenRoutes.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route= ScreenRoutes.Register.route){
            RegisterScreen(
                onRegisterSuccess = {mainNavController.navigate(ScreenRoutes.Onboarding.route){
                    popUpTo(ScreenRoutes.Welcome.route) {
                        inclusive = true
                    }
                    launchSingleTop=true
                }},
                onNavigateToLogin = {
                    mainNavController.navigate(ScreenRoutes.Login.route){
                        popUpTo(ScreenRoutes.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route= ScreenRoutes.Onboarding.route){
            OnboardingScreen(
                onOnboardingComplete = {
                    mainNavController.navigate(ScreenRoutes.Home.route){
                        popUpTo(ScreenRoutes.Onboarding.route){inclusive=true}
                        launchSingleTop = true
                    }
                }
            )

        }
        composable(route= ScreenRoutes.Home.route){
             HomeShell(
                 onSignOut = {
                     mainNavController.navigate(ScreenRoutes.Welcome.route){
                         popUpTo(mainNavController.graph.id){inclusive=true}
                         launchSingleTop = true
                     }
                 }
             )
        }

    }
}