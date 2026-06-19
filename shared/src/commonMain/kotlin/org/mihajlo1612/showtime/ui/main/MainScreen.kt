package org.mihajlo1612.showtime.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.mihajlo1612.showtime.navigation.Screen
import org.mihajlo1612.showtime.ui.catalog.CatalogScreen
import org.mihajlo1612.showtime.ui.favorites.FavoritesScreen
import org.mihajlo1612.showtime.ui.profile.ProfileScreen
import org.mihajlo1612.showtime.ui.quiz.QuizScreen
import org.mihajlo1612.showtime.ui.theme.ShowtimeColors
import org.mihajlo1612.showtime.ui.watchlist.WatchlistScreen

data class BottomNavItem(
    val route: String,
    val icon: String,
    val label: String,
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.CATALOG, "⊞", "Movies"),
    BottomNavItem(Screen.FAVORITES, "♡", "Favorites"),
    BottomNavItem(Screen.WATCHLIST, "⊙", "Watchlist"),
    BottomNavItem(Screen.QUIZ, "◎", "Quiz"),
    BottomNavItem(Screen.PROFILE, "◯", "Profile"),
)

@Composable
fun MainScreen(
    onNavigateToDetail: (String) -> Unit,
    onLogout: () -> Unit,
) {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        containerColor = ShowtimeColors.BackgroundPage,
        bottomBar = {
            NavigationBar(containerColor = ShowtimeColors.BackgroundInput) {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.route == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            innerNavController.navigate(item.route) {
                                popUpTo(innerNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Text(
                                text = item.icon,
                                fontSize = 20.sp,
                                color = if (selected) ShowtimeColors.PrimaryGold else ShowtimeColors.TextSecondary,
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = ShowtimeColors.PrimaryGold,
                            unselectedTextColor = ShowtimeColors.TextSecondary,
                            indicatorColor = ShowtimeColors.BackgroundPage,
                        ),
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = innerNavController,
            startDestination = Screen.CATALOG,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.CATALOG) {
                CatalogScreen(onNavigateToDetail = onNavigateToDetail)
            }
            composable(Screen.FAVORITES) {
                FavoritesScreen(onNavigateToDetail = onNavigateToDetail)
            }
            composable(Screen.WATCHLIST) {
                WatchlistScreen(onNavigateToDetail = onNavigateToDetail)
            }
            composable(Screen.QUIZ) {
                QuizScreen(
                    onBackToCatalog = {
                        innerNavController.navigate(Screen.CATALOG) {
                            popUpTo(innerNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.PROFILE) {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}


