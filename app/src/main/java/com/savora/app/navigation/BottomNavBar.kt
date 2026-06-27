package com.savora.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.savora.app.ui.theme.SavoraOnSurfaceVariant
import com.savora.app.ui.theme.SavoraPrimary
import com.savora.app.ui.theme.SavoraSurface

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Beranda", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.RECIPES, "Resep", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu),
    BottomNavItem(Routes.STORE, "Toko", Icons.Filled.Store, Icons.Outlined.Store),
    BottomNavItem(Routes.PROFILE, "Profil", Icons.Filled.Person, Icons.Outlined.Person),
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = SavoraSurface.copy(alpha = 0.92f),
        tonalElevation = 0.dp,
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SavoraPrimary,
                    selectedTextColor = SavoraPrimary,
                    unselectedIconColor = SavoraOnSurfaceVariant,
                    unselectedTextColor = SavoraOnSurfaceVariant,
                    indicatorColor = SavoraSurface,
                )
            )
        }
    }
}
