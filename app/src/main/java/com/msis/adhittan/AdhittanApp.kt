package com.msis.adhittan

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.msis.adhittan.ui.components.BottomNavigationBar
import com.msis.adhittan.ui.navigation.Screen
import com.msis.adhittan.ui.screens.BeadScreen
import com.msis.adhittan.ui.screens.DashboardScreen
import com.msis.adhittan.ui.screens.LibraryScreen
import com.msis.adhittan.ui.screens.NawinBeadScreen
import com.msis.adhittan.ui.screens.NawinDashboardScreen
import com.msis.adhittan.ui.screens.SettingsScreen
import com.msis.adhittan.ui.theme.AdhittanTheme

@Composable
fun AdhittanApp() {
    AdhittanTheme {
        val navController = rememberNavController()
        
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        onNavigateToBeads = { navController.navigate(Screen.Beads.route) }
                    )
                }
                composable(Screen.Nawin.route) {
                    NawinDashboardScreen(
                        onNavigateToNawinBeads = { navController.navigate(Screen.NawinBeads.route) }
                    )
                }
                composable(Screen.Library.route) {
                    LibraryScreen()
                }
                composable(Screen.Beads.route) {
                    BeadScreen()
                }
                composable(Screen.NawinBeads.route) {
                    NawinBeadScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }
}
