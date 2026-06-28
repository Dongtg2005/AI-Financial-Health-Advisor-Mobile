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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.AppLogo
import com.example.mobile.ui.components.GlassCard

@Preview(showBackground = true, showSystemUi = true, name = "LoginScreen")
@Composable
fun LoginScreen(
    navController: NavController? = null // Nullable để Preview không cần NavController thật
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

            // Tái sử dụng GlassCard và thêm padding ruột để UI đẹp mắt
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { navController?.navigate("onboarding") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Đăng nhập", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W800)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController?.navigate("register") }
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
