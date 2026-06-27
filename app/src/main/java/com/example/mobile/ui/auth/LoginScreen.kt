package com.example.mobile.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.AppLogo
import com.example.mobile.ui.components.GlassCard

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppLogo(modifier = Modifier.size(80.dp))
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text  = "Finance App",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W800
            )
            
            Text(
                text  = "Quản lý tài chính thông minh",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.W600
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Tái sử dụng GlassCard để tạo tính nhất quán tuyệt đối
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { navController.navigate("onboarding") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Đăng nhập", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W800)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { navController.navigate("register") }
            ) {
                Text(
                    "Chưa có tài khoản? Đăng ký ngay", 
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W700
                )
            }
        }
    }
}
