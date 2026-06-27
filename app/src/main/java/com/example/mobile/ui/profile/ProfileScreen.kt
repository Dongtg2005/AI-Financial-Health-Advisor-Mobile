package com.example.mobile.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.BottomNav
import com.example.mobile.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Cá nhân",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.W800
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = { BottomNav(navController) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Avatar Placeholder sử dụng hình tròn bo mượt của Material 3
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Đông",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.W800
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Hộp kính chứa menu (Đã sửa lỗi containerColor)
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ProfileMenuItem(
                            icon = Icons.Rounded.Person,
                            text = "Thông tin tài khoản",
                            onClick = { /* Navigate to Account Details */ }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        
                        ProfileMenuItem(
                            icon = Icons.Rounded.CreditCard,
                            text = "Hạn mức chi tiêu",
                            onClick = { /* Navigate to Budget Limits */ }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        
                        ProfileMenuItem(
                            icon = Icons.Rounded.Notifications,
                            text = "Cài đặt thông báo",
                            onClick = { /* Navigate to Notifications */ }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        
                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Rounded.Logout,
                            text = "Đăng xuất",
                            textColor = MaterialTheme.colorScheme.error,
                            showChevron = false,
                            onClick = { /* Handle Logout logic */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    textColor: Color = Color.Unspecified,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon đại diện phía trước tăng tính nhận diện thị giác
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (textColor == Color.Unspecified) MaterialTheme.colorScheme.onSurfaceVariant else textColor,
            modifier = Modifier.size(22.dp)
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.W600,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        
        // Mũi tên điều hướng nhỏ gọn chuẩn iOS/Premium UI
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
