package com.palaksinghal.mysaarthi.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.palaksinghal.mysaarthi.presentation.authentication.LoginScreen
import com.palaksinghal.mysaarthi.presentation.authentication.RegisterScreen
import com.palaksinghal.mysaarthi.presentation.splash.SplashScreen


@Composable
fun MySaarthiApp(){

    val mainNavController  = rememberNavController()

    NavHost(navController = mainNavController , startDestination = ScreenRoutes.Register.route){
        composable(route= ScreenRoutes.Splash.route){
            SplashScreen()
        }
        composable(route= ScreenRoutes.Welcome.route){

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

        }
        composable(route= ScreenRoutes.Home.route){

        }

    }
}