package com.example.mobile.ui.dashboard.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.ui.dashboard.ScoreBreakdown

@Composable
fun HealthScoreCircle(
    score: Int,
    breakdown: ScoreBreakdown,
    modifier: Modifier = Modifier,
    sizeDp: Int = 200,
    strokeWidthDp: Float = 16f
) {
    // Trục lặp Animation quét từ 0 đến 1 (100%) cực mượt
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(breakdown) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    // Định nghĩa các dải màu Gradient Phát Sáng cho từng phân đoạn
    val spendingBrush = Brush.sweepGradient(listOf(Color(0xFF00C853), Color(0xFFB9F6CA)))
    val debtBrush = Brush.sweepGradient(listOf(Color(0xFFD50000), Color(0xFFFF8A80)))
    val savingBrush = Brush.sweepGradient(listOf(Color(0xFF00B8D4), Color(0xFF84FFFF)))
    val awarenessBrush = Brush.sweepGradient(listOf(Color(0xFFAA00FF), Color(0xFFEA80FC)))
    
    // Nền xám mờ làm bệ đỡ phía sau
    val trackColor = Color(0x1AFFFFFF) 

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(sizeDp.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = strokeWidthDp.dp.toPx(), cap = StrokeCap.Round)
            
            // 1. Vẽ vòng nền Track rỗng phía sau
            drawCircle(
                color = trackColor,
                radius = (size.minDimension - stroke.width) / 2,
                style = stroke
            )

            // Góc bắt đầu vẽ (Trục dọc hướng lên trên là -90 độ)
            var startAngle = -90f
            
            // Tính toán độ dài góc (Sweep Angle) thực tế của 4 yếu tố dựa trên điểm tối đa của hệ thống
            val maxSpendingAngle = (35f / 100f) * 360f // ~126 độ
            val maxDebtAngle = (35f / 100f) * 360f     // ~126 độ
            val maxSavingAngle = (20f / 100f) * 360f   // ~72 độ
            val maxAwarenessAngle = (10f / 100f) * 360f // ~36 độ

            // Tỷ lệ điểm thực tế đạt được của user
            val spendingSweep = (breakdown.spending / 35f) * maxSpendingAngle * animationProgress.value
            val debtSweep = (breakdown.debt / 35f) * maxDebtAngle * animationProgress.value
            val savingSweep = (breakdown.saving / 20f) * maxSavingAngle * animationProgress.value
            val awarenessSweep = (breakdown.awareness / 10f) * maxAwarenessAngle * animationProgress.value

            // Khoảng hở nhỏ giữa các cung để tạo cảm giác phân rã cơ khí cao cấp
            val gap = 3f

            // Vẽ Cung 1: Spending (Chi tiêu)
            if (spendingSweep > 0) {
                drawArc(
                    brush = spendingBrush,
                    startAngle = startAngle,
                    sweepAngle = spendingSweep - gap,
                    useCenter = false,
                    style = stroke
                )
            }
            startAngle += maxSpendingAngle

            // Vẽ Cung 2: Debt (Nợ nần)
            if (debtSweep > 0) {
                drawArc(
                    brush = debtBrush,
                    startAngle = startAngle,
                    sweepAngle = debtSweep - gap,
                    useCenter = false,
                    style = stroke
                )
            }
            startAngle += maxDebtAngle

            // Vẽ Cung 3: Saving (Tích lũy)
            if (savingSweep > 0) {
                drawArc(
                    brush = savingBrush,
                    startAngle = startAngle,
                    sweepAngle = savingSweep - gap,
                    useCenter = false,
                    style = stroke
                )
            }
            startAngle += maxSavingAngle

            // Vẽ Cung 4: Awareness (Nhận thức)
            if (awarenessSweep > 0) {
                drawArc(
                    brush = awarenessBrush,
                    startAngle = startAngle,
                    sweepAngle = awarenessSweep - gap,
                    useCenter = false,
                    style = stroke
                )
            }
        }

        // 2. Ruột Vòng Tròn: Hiển thị con số điểm tổng ở trung tâm
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = (score * animationProgress.value).toInt().toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.W900,
                color = Color.White
            )
            Text(
                text = "Health Score",
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
        }
    }
}
