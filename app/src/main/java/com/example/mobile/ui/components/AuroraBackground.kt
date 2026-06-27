package com.example.mobile.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.mobile.ui.theme.*

@Composable
fun AuroraBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        // Nền tối hơn một chút để làm nổi bật lớp kính
        drawRect(color = Color(0xFFF8F9FE))

        // Aurora 1: Top-Right (Sáng và rực rỡ hơn)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AuroraBlue.copy(alpha = 0.6f), Color.Transparent),
                center = Offset(size.width * 0.8f, size.height * 0.1f),
                radius = size.width * 1.5f
            ),
            center = Offset(size.width * 0.8f, size.height * 0.1f),
            radius = size.width * 1.5f
        )

        // Aurora 2: Top-Left (Tím sâu hơn)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AuroraPurple.copy(alpha = 0.5f), Color.Transparent),
                center = Offset(size.width * 0.2f, size.height * -0.05f),
                radius = size.width * 1.2f
            ),
            center = Offset(size.width * 0.2f, size.height * -0.05f),
            radius = size.width * 1.2f
        )

        // Aurora 3: Dải Teal ở giữa để tạo chiều sâu
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AuroraTeal.copy(alpha = 0.4f), Color.Transparent),
                center = Offset(size.width * 0.5f, size.height * 0.3f),
                radius = size.width * 0.8f
            ),
            center = Offset(size.width * 0.5f, size.height * 0.3f),
            radius = size.width * 0.8f
        )
    }
}
