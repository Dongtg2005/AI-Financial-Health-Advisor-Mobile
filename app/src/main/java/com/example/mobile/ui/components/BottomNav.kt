package com.example.mobile.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNav(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick  = { 
                if (currentRoute != "dashboard") {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            },
            icon  = { Icon(Icons.Rounded.Home, contentDescription = null) },
            label = { Text("Tổng quan") }
        )
        NavigationBarItem(
            selected = currentRoute == "transactions",
            onClick  = { 
                if (currentRoute != "transactions") {
                    navController.navigate("transactions")
                }
            },
            icon  = { Icon(Icons.AutoMirrored.Rounded.List, contentDescription = null) },
            label = { Text("Giao dịch") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick  = { 
                if (currentRoute != "profile") {
                    navController.navigate("profile")
                }
            },
            icon  = { Icon(Icons.Rounded.AccountCircle, contentDescription = null) },
            label = { Text("Cá nhân") }
        )
        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick  = { 
                if (currentRoute != "settings") {
                    navController.navigate("settings")
                }
            },
            icon  = { Icon(Icons.Rounded.Settings, contentDescription = null) },
            label = { Text("Cài đặt") }
        )
    }
}
