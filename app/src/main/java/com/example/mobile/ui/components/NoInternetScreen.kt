package com.example.mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoInternetScreen(
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AuroraBackground()

        // Tăng độ che phủ tối (alpha = 0.2f) để tạo chiều sâu và làm nổi khối GlassCard
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.2f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. THAY EMOJI BẰNG VECTOR ICON: Sang trọng và đồng bộ Material 3
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = "No Internet Icon",
                            tint = Color(0xFF1A237E),
                            modifier = Modifier.size(56.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // 2. ĐỔI SANG TONE MÀU SẪM: Giải quyết triệt để lỗi "cháy sáng" của text
                        Text(
                            text = "Kết nối bị gián đoạn",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.W900,
                            color = Color(0xFF1A237E),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Hệ thống không thể kết nối tới máy chủ. Vui lòng kiểm tra tín hiệu Wi-Fi hoặc dữ liệu di động (3G/4G/5G) của thiết bị.",
                            fontSize = 14.sp,
                            color = Color(0xFF1A237E).copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. NHUỘM MÀU NÚT BẤM (BUTTON): Tạo điểm nhấn hành động tương phản mạnh
                        Button(
                            onClick = onRetryClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A237E), // Nền nút xanh Indigo sẫm nổi bật
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Thử lại",
                                fontWeight = FontWeight.W800,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "NoInternetScreen Preview")
@Composable
fun NoInternetScreenPreview() {
    NoInternetScreen()
}
