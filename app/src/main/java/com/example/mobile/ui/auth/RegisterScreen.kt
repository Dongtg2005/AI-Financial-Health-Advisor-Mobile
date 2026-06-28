package com.example.mobile.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.AppLogo
import com.example.mobile.ui.components.GlassCard

@Composable
fun RegisterScreen(
    navController: NavController? = null,
    viewModel: AuthViewModel? = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isLoading = viewModel?.isLoading?.collectAsState()?.value ?: false

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppLogo(modifier = Modifier.size(72.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Đổi text tiêu đề sang màu xanh đen đậm sâu để nổi bật trên nền sáng
            Text(
                text  = "Tạo tài khoản mới 🚀",
                fontSize = 28.sp,
                color = Color(0xFF1A237E),
                fontWeight = FontWeight.W900
            )

            Text(
                text  = "Bắt đầu hành trình làm chủ sinh mệnh tài chính",
                fontSize = 14.sp,
                color = Color(0xFF1A237E).copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cấu hình màu sắc Text mượt mà, dễ nhìn (Sử dụng các tone màu sẫm thanh lịch)
                    val textFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF212121),
                        unfocusedTextColor = Color(0xFF424242),
                        focusedLabelColor = Color(0xFF1A237E),
                        unfocusedLabelColor = Color(0xFF616161),
                        focusedBorderColor = Color(0xFF1A237E),
                        unfocusedBorderColor = Color(0xFF1A237E).copy(alpha = 0.3f)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Họ và tên") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = textFieldColors,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = textFieldColors,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = MaterialTheme.shapes.small,
                        colors = textFieldColors,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Xác nhận mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = MaterialTheme.shapes.small,
                        colors = textFieldColors,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { navController?.navigate("onboarding") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A237E), // Nút bấm màu xanh Indigo đậm sang trọng
                            contentColor = Color.White
                        ),
                        enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && (password == confirmPassword) && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Đăng ký ngay", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W800)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sửa dòng chữ điều hướng sang tone xanh đậm để tương phản 100% với nền sáng
            TextButton(
                onClick = { navController?.navigate("login") }
            ) {
                Text(
                    "Đã có tài khoản? Đăng nhập tại đây",
                    color = Color(0xFF1A237E),
                    fontWeight = FontWeight.W800
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "RegisterScreen")
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = null, viewModel = null)
}