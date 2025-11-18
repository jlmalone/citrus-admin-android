package com.citrus.admin.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.citrus.admin.ui.navigation.Screen
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val userCount by viewModel.userCount.collectAsState()
    val activeUserCount by viewModel.activeUserCount.collectAsState()
    val receiptCount by viewModel.receiptCount.collectAsState()
    val pendingReceiptCount by viewModel.pendingReceiptCount.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val unreadAlertCount by viewModel.unreadAlertCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Citrus Admin Dashboard") },
                actions = {
                    if (unreadAlertCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge { Text(unreadAlertCount.toString()) }
                            }
                        ) {
                            IconButton(onClick = { navController.navigate(Screen.Alerts.route) }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                            }
                        }
                    } else {
                        IconButton(onClick = { navController.navigate(Screen.Alerts.route) }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Section
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Quick Stats
            Text(
                text = "Quick Stats",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(
                    title = "Users",
                    value = "$activeUserCount / $userCount",
                    subtitle = "Active / Total",
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Users.route) }
                )
                QuickStatCard(
                    title = "Receipts",
                    value = pendingReceiptCount.toString(),
                    subtitle = "Pending Review",
                    icon = Icons.Default.Email,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Receipts.route) }
                )
            }

            val currencyFormat = NumberFormat.getCurrencyInstance()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.Analytics.route) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = "Total Revenue",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = currencyFormat.format(totalRevenue ?: 0.0),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Quick Actions
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionCard(
                    title = "Users",
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Users.route) }
                )
                ActionCard(
                    title = "Receipts",
                    icon = Icons.Default.Email,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Receipts.route) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionCard(
                    title = "Analytics",
                    icon = Icons.Default.Menu,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Analytics.route) }
                )
                ActionCard(
                    title = "Alerts",
                    icon = Icons.Default.Notifications,
                    badgeCount = if (unreadAlertCount > 0) unreadAlertCount else null,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Alerts.route) }
                )
            }
        }
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (badgeCount != null && badgeCount > 0) {
                    BadgedBox(
                        badge = { Badge { Text(badgeCount.toString()) } }
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
