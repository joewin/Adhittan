package com.msis.adhittan.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.msis.adhittan.ui.navigation.Screen
import com.msis.adhittan.ui.theme.DeepMoss
import com.msis.adhittan.ui.theme.SoftClay
import com.msis.adhittan.ui.theme.WarmSand

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Dashboard,
        Screen.Nawin,
        Screen.Library,
        Screen.Settings
    )
    
    NavigationBar(
        containerColor = SoftClay,
        contentColor = DeepMoss
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DeepMoss,
                    selectedTextColor = DeepMoss,
                    indicatorColor = WarmSand,
                    unselectedIconColor = DeepMoss.copy(alpha = 0.5f),
                    unselectedTextColor = DeepMoss.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    BottomNavigationBar(rememberNavController())
}
