package com.citrus.admin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.citrus.admin.ui.alerts.AlertsScreen
import com.citrus.admin.ui.analytics.AnalyticsScreen
import com.citrus.admin.ui.dashboard.DashboardScreen
import com.citrus.admin.ui.receipts.ReceiptReviewScreen
import com.citrus.admin.ui.users.UserManagementScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Users : Screen("users")
    object Receipts : Screen("receipts")
    object Analytics : Screen("analytics")
    object Alerts : Screen("alerts")
}

@Composable
fun CitrusNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.Users.route) {
            UserManagementScreen()
        }
        composable(Screen.Receipts.route) {
            ReceiptReviewScreen()
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }
        composable(Screen.Alerts.route) {
            AlertsScreen()
        }
    }
}
