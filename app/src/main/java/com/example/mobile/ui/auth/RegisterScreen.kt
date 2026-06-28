package com.example.mobile.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.GlassCard

@Composable
fun RegisterScreen(
    navController: NavController? = null, // Nullable để Preview không cần NavController thật
    viewModel: AuthViewModel? = null
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
            Text(
                text  = "Tạo tài khoản",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W800
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Tái sử dụng GlassCard để tạo tính nhất quán tuyệt đối
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
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
                    onClick = { navController?.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Đăng ký", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W800)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { navController?.popBackStack() }
            ) {
                Text(
                    "Đã có tài khoản? Đăng nhập", 
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W700
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "RegisterScreen")
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        navController = null,
        viewModel = null
    )
}
