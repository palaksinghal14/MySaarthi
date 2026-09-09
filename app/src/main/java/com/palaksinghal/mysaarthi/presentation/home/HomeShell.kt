package com.palaksinghal.mysaarthi.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.palaksinghal.mysaarthi.R
import com.palaksinghal.mysaarthi.core.navigation.ScreenRoutes
import com.palaksinghal.mysaarthi.presentation.home.today.EveningCheckInScreen
import com.palaksinghal.mysaarthi.presentation.home.today.SadhanaDetailScreen
import com.palaksinghal.mysaarthi.presentation.home.today.ShlokaDetailScreen
import com.palaksinghal.mysaarthi.presentation.home.today.TodayScreen
import com.palaksinghal.mysaarthi.presentation.nearby.NearbyScreen
import com.palaksinghal.mysaarthi.presentation.home.profile.YouScreen
import com.palaksinghal.mysaarthi.presentation.profile.EditProfileScreen
import com.palaksinghal.mysaarthi.presentation.profile.SettingsScreen
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral400
import com.palaksinghal.mysaarthi.presentation.theme.Surface

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: Int
)

@Composable
fun HomeShell(
    onSignOut:()->Unit
) {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Only show bottom bar on the three main tab routes
    // Hide it on detail screens like ShlokaDetail, SadhanaDetail etc.
    val showBottomBar = currentDestination?.route in listOf(
        ScreenRoutes.Today.route,
        ScreenRoutes.Nearby.route,
        ScreenRoutes.You.route
    )

    val tabs = listOf(
        BottomNavItem(ScreenRoutes.Today.route, "Today", R.drawable.ic_brahma_muhurta),
        BottomNavItem(ScreenRoutes.Nearby.route, "Nearby", R.drawable.ic_location),
        BottomNavItem(ScreenRoutes.You.route, "You", R.drawable.ic_satsang)
    )

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            if(showBottomBar) {
                NavigationBar(
                    containerColor = Bg,
                    tonalElevation = androidx.compose.ui.unit.Dp(0f)
                ) {
                    tabs.forEach { tab ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.route == tab.route
                        } == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                tabNavController.navigate(tab.route) {
                                    popUpTo(tabNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = tab.icon),
                                    contentDescription = tab.label
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontFamily = FigtreeFamily
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Accent,
                                selectedTextColor = Accent,
                                unselectedIconColor = Neutral400,
                                unselectedTextColor = Neutral400,
                                indicatorColor = Surface
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = ScreenRoutes.Today.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(ScreenRoutes.Today.route) {
                TodayScreen(
                    onShlokaClick = {
                        tabNavController.navigate(ScreenRoutes.ShlokaDetail.route)
                    },
                    onSadhanaClick = {
                        tabNavController.navigate(ScreenRoutes.SadhanaDetail.route)
                    },
                    onEveningCheckInClick = {
                        tabNavController.navigate(ScreenRoutes.EveningCheckIn.route)
                    }
                )
            }
            composable(ScreenRoutes.ShlokaDetail.route) {
                ShlokaDetailScreen(onBack = { tabNavController.popBackStack() })
            }
            composable(ScreenRoutes.SadhanaDetail.route) { backStackEntry ->
                SadhanaDetailScreen(
                    onBack = { tabNavController.popBackStack() },
                    navController = tabNavController,
                    backStackEntry = backStackEntry
                )
            }
            composable(ScreenRoutes.EveningCheckIn.route) {
                EveningCheckInScreen(onBack = { tabNavController.popBackStack() })
            }
            composable(ScreenRoutes.Nearby.route) { NearbyScreen() }
            composable(ScreenRoutes.You.route) {
                YouScreen(
                onNavigateToEditProfile = {
                    tabNavController.navigate(ScreenRoutes.EditProfile.route)
                },
                onNavigateToSettings = {
                    tabNavController.navigate(ScreenRoutes.Settings.route)
                }
               )
            }

            composable(ScreenRoutes.EditProfile.route) {
                EditProfileScreen(
                    onBack = { tabNavController.popBackStack() }
                )
            }
            composable(ScreenRoutes.Settings.route) {
                SettingsScreen(
                    onBack = { tabNavController.popBackStack() },
                    onSignOut =  onSignOut
                )
            }
        }
    }
}