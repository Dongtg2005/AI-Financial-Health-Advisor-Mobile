package com.example.mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
    onRetryClick: () -> Unit = {} // Lambda xử lý khi người dùng bấm nút Thử lại
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 1. NỀN HÀO QUANG ĐỒNG BỘ: Giữ vững DNA thị giác cao cấp
        AuroraBackground()

        // Lớp phủ tối nhẹ để tăng độ tương phản cho Text trắng
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.15f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 2. KHỐI KÍNH MỜ (Glassmorphism): Cảnh báo trực quan
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Icon Tín hiệu mất kết nối
                        Text(
                            text = "🌐📡",
                            fontSize = 50.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Mất kết nối Internet",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.W900,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Vui lòng kiểm tra lại Wi-Fi hoặc dữ liệu di động (3G/4G/5G) để tiếp tục cập nhật dòng chảy tài chính.",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 3. NÚT THỬ LẠC (RETRY BUTTON)
                        Button(
                            onClick = onRetryClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
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
