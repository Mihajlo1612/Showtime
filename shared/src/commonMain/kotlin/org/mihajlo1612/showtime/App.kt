package org.mihajlo1612.showtime

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.koin.compose.viewmodel.koinViewModel
import org.mihajlo1612.showtime.navigation.MovieDetailRoute
import org.mihajlo1612.showtime.navigation.Screen
import org.mihajlo1612.showtime.ui.auth.AppViewModel
import org.mihajlo1612.showtime.ui.auth.landing.LandingScreen
import org.mihajlo1612.showtime.ui.auth.login.LoginScreen
import org.mihajlo1612.showtime.ui.auth.register.RegisterScreen
import org.mihajlo1612.showtime.ui.detail.DetailScreen
import org.mihajlo1612.showtime.ui.main.MainScreen

@Composable
fun App() {
    val appViewModel: AppViewModel = koinViewModel()
    val isLoggedIn by appViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == false) {
            navController.navigate(Screen.LANDING) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    MaterialTheme {
        if (isLoggedIn != null) {
            NavHost(
                navController = navController,
                startDestination = if (isLoggedIn == true) Screen.MAIN else Screen.LANDING,
            ) {
                composable(Screen.LANDING) {
                    LandingScreen(
                        onNavigateToLogin = { navController.navigate(Screen.LOGIN) },
                        onNavigateToRegister = { navController.navigate(Screen.REGISTER) },
                    )
                }
                composable(Screen.LOGIN) {
                    LoginScreen(
                        onNavigateToHome = {
                            navController.navigate(Screen.MAIN) {
                                popUpTo(Screen.LANDING) { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() },
                    )
                }
                composable(Screen.REGISTER) {
                    RegisterScreen(
                        onNavigateToHome = {
                            navController.navigate(Screen.MAIN) {
                                popUpTo(Screen.LANDING) { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() },
                    )
                }
                composable(Screen.MAIN) {
                    MainScreen(
                        onNavigateToDetail = { imdbId ->
                            navController.navigate(MovieDetailRoute(imdbId))
                        },
                        onLogout = { appViewModel.logout() },
                    )
                }
                composable<MovieDetailRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<MovieDetailRoute>()
                    DetailScreen(
                        imdbId = route.imdbId,
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}