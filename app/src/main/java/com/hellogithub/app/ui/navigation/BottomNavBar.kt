package com.hellogithub.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem("首页", Routes.FEED, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("榜单", Routes.RANK, Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
    BottomNavItem("月刊", Routes.PERIODICAL, Icons.Filled.Book, Icons.Outlined.Book),
    BottomNavItem("搜索", Routes.SEARCH, Icons.Filled.Search, Icons.Outlined.Search),
    BottomNavItem("设置", Routes.SETTINGS, Icons.Filled.Settings, Icons.Outlined.Settings),
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.FEED) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
            )
        }
    }
}
