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
    navController: NavController? = null, // Nullable giúp cô lập hệ thống Preview không bị crash bậy
    viewModel: AuthViewModel? = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    // Tận dụng toán tử Safe Call ?. để đọc trạng thái loading từ ViewModel thực tế nếu có
    val isLoading = viewModel?.isLoading?.collectAsState()?.value ?: false

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. NỀN HÀO QUANG: Đồng bộ DNA thị giác cao cấp toàn hệ thống
        AuroraBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()) // Chống vỡ layout (Oversized) khi bật bàn phím ảo
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppLogo(modifier = Modifier.size(72.dp))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text  = "Tạo tài khoản mới 🚀",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.W900
            )
            
            Text(
                text  = "Bắt đầu hành trình làm chủ sinh mệnh tài chính",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )
            
            // 2. KHỐI KÍNH MỜ (Glassmorphism): Bọc trọn gói form nhập dữ liệu bảo mật
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Trường 1: Họ tên người dùng
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Họ và tên") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f)
                        ),
                        singleLine = true
                    )
                    
                    // Trường 2: Email đăng ký
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f)
                        ),
                        singleLine = true
                    )
                    
                    // Trường 3: Mật khẩu ký tự ẩn
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f)
                        ),
                        singleLine = true
                    )

                    // Trường 4: Xác nhận mật khẩu
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Xác nhận mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f)
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Nút Đăng Ký hành động
                    Button(
                        onClick = { 
                            // Khi Đông đấu nối API, luồng đúng sẽ là bắn dữ liệu lên DB -> Thành công thì navigate sang Onboarding
                            navController?.navigate("onboarding") 
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.small,
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
            
            // Nút quay lại màn hình đăng nhập nếu đã có tài khoản
            TextButton(
                onClick = { navController?.navigate("login") }
            ) {
                Text(
                    "Đã có tài khoản? Đăng nhập tại đây", 
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.W700
                )
            }
        }
    }
}

// KHU VỰC PREVIEW: Độc lập hoàn toàn, ép Android Studio dựng hình mượt mà không lo crash
@Preview(showBackground = true, showSystemUi = true, name = "RegisterScreen")
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = null, viewModel = null)
}
