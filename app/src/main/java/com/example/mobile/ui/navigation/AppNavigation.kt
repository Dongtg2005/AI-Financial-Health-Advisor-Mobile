package com.example.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobile.data.local.TokenManager
import com.example.mobile.ui.auth.LoginScreen
import com.example.mobile.ui.auth.RegisterScreen
import com.example.mobile.ui.dashboard.DashboardScreen
import com.example.mobile.ui.debts.DebtScreen
import com.example.mobile.ui.transactions.TransactionsScreen
import com.example.mobile.ui.profile.ProfileScreen
import com.example.mobile.ui.settings.SettingsScreen
import com.example.mobile.ui.onboarding.OnboardingScreen

import com.example.mobile.ui.scores.ScoreHistoryScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("transactions")
    object Debts : Screen("debts")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object ScoreHistory : Screen("score_history")
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // Kiểm tra Token đã lưu: nếu có thì vào thẳng Dashboard, nếu chưa thì vào Login
    val startRoute = remember {
        if (!tokenManager.getToken().isNullOrEmpty()) {
            Screen.Dashboard.route
        } else {
            Screen.Login.route
        }
    }

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startRoute) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = { income ->
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                navController = navController,
                onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) }
            )
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController)
        }
        composable(Screen.Debts.route) {
            DebtScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                onLogoutClick = {
                    tokenManager.clearAuthData()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(Screen.ScoreHistory.route) {
            ScoreHistoryScreen(navController)
        }
    }
}
