package com.msis.adhittan.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook

import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Settings

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Home)
    object Library : Screen("library", "Library", Icons.Filled.MenuBook)
    object Beads : Screen("beads", "Beads", Icons.Filled.Circle)
    object Nawin : Screen("nawin", "Ko Nawin", Icons.Filled.Home) // Use Home or a Star icon
    object NawinBeads : Screen("nawin_beads", "Nawin Beads", Icons.Filled.Circle)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}
