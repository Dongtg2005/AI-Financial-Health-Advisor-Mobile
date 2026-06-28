package com.example.mobile.ui.dashboard.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HealthScoreRing(
    score: Int,
    modifier: Modifier = Modifier,
    ringSize: Dp = 220.dp,
    strokeWidth: Dp = 16.dp
) {
    val ringColor = when {
        score >= 70 -> ScoreGood
        score >= 50 -> ScoreWarning
        else        -> ScoreDanger
    }

    val statusText = when {
        score >= 85 -> "Vững vàng"
        score >= 70 -> "Ổn định"
        score >= 50 -> "Cần chú ý"
        score >= 30 -> "Đáng lo ngại"
        else        -> "Nguy hiểm"
    }

    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(
            durationMillis = 1400,
            easing = FastOutSlowInEasing
        ),
        label = "scoreAnimation"
    )

    val sweepAngle = (animatedScore / 100f) * 300f
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)

    val ringBrush = remember(ringColor) {
        Brush.linearGradient(
            colors = listOf(ringColor.copy(alpha = 0.7f), ringColor)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(ringSize)
            .padding(16.dp)
            .drawBehind {
                // 🛡️ SỬA LỖI ĐỘC LẬP DENSITY: Gọi toPx() an toàn tuyệt đối bên trong DrawScope
                val blurRadius = 20.dp.toPx()
                val radius = (ringSize.toPx() / 2) - strokeWidth.toPx() * 1.5f
                
                if (radius > 0) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().asFrameworkPaint()
                        paint.color = ringColor.copy(alpha = 0.18f).toArgb()
                        
                        // Khởi tạo bộ lọc mờ an toàn không sợ crash môi trường preview
                        paint.maskFilter = android.graphics.BlurMaskFilter(
                            blurRadius, 
                            android.graphics.BlurMaskFilter.Blur.NORMAL
                        )
                        canvas.nativeCanvas.drawCircle(
                            center.x,
                            center.y,
                            radius,
                            paint
                        )
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val diameter = size.minDimension - strokePx
            
            val topLeft = androidx.compose.ui.geometry.Offset(
                x = (size.width - diameter) / 2f,
                y = (size.height - diameter) / 2f
            )
            val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)

            // Vẽ thanh ray nền (Track) góc 120 độ quét 300 độ để hở góc đáy thanh thoát
            drawArc(
                color       = trackColor,
                startAngle  = 120f,
                sweepAngle  = 300f,
                useCenter   = false,
                topLeft     = topLeft,
                size        = arcSize,
                style       = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Vẽ vòng tiến độ live động theo điểm số của Đông
            drawArc(
                brush       = ringBrush,
                startAngle  = 120f,
                sweepAngle  = sweepAngle,
                useCenter   = false,
                topLeft     = topLeft,
                size        = arcSize,
                style       = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        // Khối hiển thị điểm số trung tâm
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text       = animatedScore.toInt().toString(),
                fontSize   = 58.sp,
                fontWeight = FontWeight.W900,
                color      = MaterialTheme.colorScheme.onBackground,
                lineHeight = 58.sp,
                letterSpacing = (-1).sp
            )
            Text(
                text  = "/ 100",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = FontWeight.W700
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text  = statusText,
                style = MaterialTheme.typography.titleMedium,
                color = ringColor,
                fontWeight = FontWeight.W800
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthScoreRingPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            HealthScoreRing(score = 75)
        }
    }
}
