package com.example.mobile.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.BottomNav
import com.example.mobile.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier,
    onLogoutClick: (() -> Unit)? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val actualLogoutClick = onLogoutClick ?: {
        com.example.mobile.data.local.TokenManager(context).clearAuthData()
        navController.navigate("login") {
            popUpTo("dashboard") { inclusive = true }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { BottomNav(navController) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // TITLE MÀN HÌNH
                Text(
                    text = "Cá nhân",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.W900,
                    color = Color(0xFF1A237E),
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                )

                // KHỐI AVATAR & TÊN (Thiết kế lại gọn gàng)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1A237E).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color(0xFF1A237E),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Trần Ghi Đông",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.W800,
                            color = Color(0xFF1A237E)
                        )
                        Text(
                            text = "Thành viên tài chính thông minh",
                            fontSize = 13.sp,
                            color = Color(0xFF1A237E).copy(alpha = 0.6f)
                        )
                    }
                }

                // ⭐ KHỐI 1 MỚI BỔ SUNG: CARD TÓM TẮT SỐ DƯ TÀI SẢN (Quick Analytics)
                var isBalanceVisible by remember { mutableStateOf(true) }
                GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Column {
                        Text(
                            text = "Tổng tài sản tích lũy",
                            fontSize = 13.sp,
                            color = Color(0xFF1A237E).copy(alpha = 0.6f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBalanceVisible) "28.500.000 VNĐ" else "•••••• VNĐ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W900,
                                color = Color(0xFF1A237E)
                            )
                            IconButton(onClick = { isBalanceVisible = !isBalanceVisible }) {
                                Icon(
                                    imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Ẩn số dư",
                                    tint = Color(0xFF1A237E)
                                )
                            }
                        }
                    }
                }

                // KHỐI MENU CHỨC NĂNG: Gom nhóm rành mạch cho ra dáng Product thứ thiệt
                Text(
                    text = "Quản lý tài khoản",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W800,
                    color = Color(0xFF1A237E).copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
                    Column {
                        ProfileMenuItem(icon = Icons.Default.AccountCircle, title = "Thông tin tài khoản", badge = "Đã xác thực")
                        HorizontalDivider(color = Color(0xFF1A237E).copy(alpha = 0.08f), thickness = 1.dp)
                        ProfileMenuItem(icon = Icons.Default.ShoppingCart, title = "Tài khoản liên kết / Ví", badge = "2 Ví")
                        HorizontalDivider(color = Color(0xFF1A237E).copy(alpha = 0.08f), thickness = 1.dp)
                        ProfileMenuItem(icon = Icons.Default.Star, title = "Hạn mức chi tiêu tháng", badge = "Đang chạy")
                    }
                }

                Text(
                    text = "Tiện ích ứng dụng",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W800,
                    color = Color(0xFF1A237E).copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Column {
                        ProfileMenuItem(icon = Icons.Default.Notifications, title = "Cài đặt thông báo")
                        HorizontalDivider(color = Color(0xFF1A237E).copy(alpha = 0.08f), thickness = 1.dp)
                        ProfileMenuItem(icon = Icons.Default.Share, title = "Xuất báo cáo tài chính (Excel)", badge = "Mới")
                        HorizontalDivider(color = Color(0xFF1A237E).copy(alpha = 0.08f), thickness = 1.dp)
                        ProfileMenuItem(icon = Icons.Default.Settings, title = "Cài đặt chu kỳ sao kê")
                    }
                }

                // HÀNH ĐỘNG NGUY HIỂM (ĐĂNG XUẤT) ĐƯỢC TÁCH RIÊNG KHỎI NHÓM
                Button(
                    onClick = actualLogoutClick,
                    modifier = Modifier.fillMaxWidth().height(54.dp).padding(bottom = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F).copy(alpha = 0.1f),
                        contentColor = Color(0xFFD32F2F)
                    )
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất tài khoản", fontWeight = FontWeight.W800, fontSize = 15.sp)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    badge: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF1A237E), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.W700, color = Color(0xFF212121))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Text(
                    text = badge,
                    fontSize = 11.sp,
                    color = Color(0xFF1A237E).copy(alpha = 0.6f),
                    fontWeight = FontWeight.W800,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF1A237E).copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
