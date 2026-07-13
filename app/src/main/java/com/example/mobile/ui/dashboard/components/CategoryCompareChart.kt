package com.example.mobile.ui.dashboard.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import com.example.mobile.ui.components.GlassCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.ui.dashboard.BudgetCategoryUi

@Composable
fun CategoryCompareChart(
    categories: List<BudgetCategoryUi>,
    modifier: Modifier = Modifier,
    heightDp: Int = 200
) {
    if (categories.isEmpty()) return

    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val textColor = if (isDark) Color.White else Color(0xFF1A237E)
    val limitBarColor = if (isDark) Color.White.copy(alpha = 0.4f) else Color(0xFF1A237E).copy(alpha = 0.3f)

    // Animation mượt mà đẩy cột mọc từ dưới lên trên
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(categories) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    // Tìm hạn mức cao nhất để làm đỉnh quy chiếu trần biểu đồ (Tránh tràn khung Canvas)
    val maxLimit = remember(categories) {
        categories.maxOfOrNull { maxOf(it.limit, it.spent) }?.toFloat()?.coerceAtLeast(1f) ?: 1_000_000f
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Chi Tiêu vs Ngân Sách Danh Mục 📊",
                fontSize = 15.sp,
                fontWeight = FontWeight.W800,
                color = textColor
            )

            // Vùng không gian Canvas tự vẽ
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightDp.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val itemCount = categories.size
                val groupWidth = canvasWidth / itemCount
                val barWidth = 14.dp.toPx()
                val barSpacing = 4.dp.toPx()

                categories.forEachIndexed { index, item ->
                    // Tính tọa độ X tâm điểm của nhóm danh mục
                    val groupCenterX = (index * groupWidth) + (groupWidth / 2)

                    // Toàn bộ chiều cao cột quy đổi theo tỷ lệ trục đứng
                    val spentRatio = (item.spent.toFloat() / maxLimit).coerceIn(0f, 1f)
                    val limitRatio = (item.limit.toFloat() / maxLimit).coerceIn(0f, 1f)

                    val spentBarHeight = (canvasHeight * spentRatio * animationProgress.value)
                        .coerceAtLeast(if (item.spent > 0) 4.dp.toPx() else 0f)
                    val limitBarHeight = (canvasHeight * limitRatio * animationProgress.value)
                        .coerceAtLeast(if (item.limit > 0) 4.dp.toPx() else 0f)

                    // Xác định màu sắc cột thực tế: vượt hạn mức -> đổi sang đỏ cảnh báo
                    val spentColor = if (item.spent > item.limit) Color(0xFFEF5350) else Color(0xFF00E676)

                    // 1. Vẽ Cột THỰC TẾ (Spent) - Cột đặc phát sáng
                    val spentX = groupCenterX - barWidth - (barSpacing / 2)
                    if (spentBarHeight > 0) {
                        drawRoundRect(
                            color = spentColor.copy(alpha = 0.85f),
                            topLeft = Offset(spentX, canvasHeight - spentBarHeight),
                            size = Size(barWidth, spentBarHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }

                    // 2. Vẽ Cột HẠN MỨC (Limit) - Cột nét mảnh viền kính thanh lịch
                    val limitX = groupCenterX + (barSpacing / 2)
                    if (limitBarHeight > 0) {
                        drawRoundRect(
                            color = limitBarColor,
                            topLeft = Offset(limitX, canvasHeight - limitBarHeight),
                            size = Size(barWidth, limitBarHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
            }

            // Hàng hiển thị nhãn tên danh mục (Labels) nằm ngay dưới chân cột
            Row(modifier = Modifier.fillMaxWidth()) {
                categories.forEach { item ->
                    Text(
                        text = item.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W700,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Chú thích (Legend) nhỏ gọn nằm dưới nhãn
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Hình vuông màu cho Spent
                Canvas(modifier = Modifier.size(10.dp)) {
                    drawRoundRect(
                        color = Color(0xFF00E676),
                        cornerRadius = CornerRadius(2.dp.toPx())
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Thực tế",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Hình vuông viền cho Limit
                Canvas(modifier = Modifier.size(10.dp)) {
                    drawRoundRect(
                        color = limitBarColor,
                        cornerRadius = CornerRadius(2.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Ngân sách",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
