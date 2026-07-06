package com.example.mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNav(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val isDark = isSystemInDarkTheme()
    
    // Màu nền Glassmorphism mờ sang trọng, đồng bộ tông màu Aurora (Indigo/Blue)
    val containerBg = if (isDark) {
        Color(0xFF0F172A).copy(alpha = 0.85f)
    } else {
        Color.White.copy(alpha = 0.85f)
    }
    
    // Viền sáng nhẹ tạo cảm giác kính nổi 3D (Glass border)
    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.15f)
    } else {
        Color(0xFF1A237E).copy(alpha = 0.15f)
    }
    
    // Màu sắc item chọn và chưa chọn đồng bộ tông Indigo chủ đạo của giao diện
    val activeColor = if (isDark) Color(0xFF9EC6FF) else Color(0xFF1A237E)
    val inactiveColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF1A237E).copy(alpha = 0.45f)
    val indicatorColor = if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.8f) else Color(0xFFE6F1FB)

    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = activeColor,
        selectedTextColor = activeColor,
        indicatorColor = indicatorColor,
        unselectedIconColor = inactiveColor,
        unselectedTextColor = inactiveColor
    )

    // Tạo thanh điều hướng dạng Floating Dock (viền bo tròn, nổi trên nền Aurora)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        color = containerBg,
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, borderColor)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
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
                label = { 
                    Text(
                        "Tổng quan", 
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (currentRoute == "dashboard") FontWeight.W800 else FontWeight.W600
                    ) 
                },
                colors = itemColors
            )
            NavigationBarItem(
                selected = currentRoute == "transactions",
                onClick  = { 
                    if (currentRoute != "transactions") {
                        navController.navigate("transactions")
                    }
                },
                icon  = { Icon(Icons.AutoMirrored.Rounded.List, contentDescription = null) },
                label = { 
                    Text(
                        "Giao dịch", 
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (currentRoute == "transactions") FontWeight.W800 else FontWeight.W600
                    ) 
                },
                colors = itemColors
            )
            NavigationBarItem(
                selected = currentRoute == "debts",
                onClick  = { 
                    if (currentRoute != "debts") {
                        navController.navigate("debts")
                    }
                },
                icon  = { Icon(Icons.Rounded.CreditCard, contentDescription = null) },
                label = { 
                    Text(
                        "Nợ nần", 
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (currentRoute == "debts") FontWeight.W800 else FontWeight.W600
                    ) 
                },
                colors = itemColors
            )
            NavigationBarItem(
                selected = currentRoute == "profile",
                onClick  = { 
                    if (currentRoute != "profile") {
                        navController.navigate("profile")
                    }
                },
                icon  = { Icon(Icons.Rounded.AccountCircle, contentDescription = null) },
                label = { 
                    Text(
                        "Cá nhân", 
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (currentRoute == "profile") FontWeight.W800 else FontWeight.W600
                    ) 
                },
                colors = itemColors
            )
            NavigationBarItem(
                selected = currentRoute == "settings",
                onClick  = { 
                    if (currentRoute != "settings") {
                        navController.navigate("settings")
                    }
                },
                icon  = { Icon(Icons.Rounded.Settings, contentDescription = null) },
                label = { 
                    Text(
                        "Cài đặt", 
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (currentRoute == "settings") FontWeight.W800 else FontWeight.W600
                    ) 
                },
                colors = itemColors
            )
        }
    }
}
