package com.example.mobile.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.mobile.ui.theme.Blue40

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(40.dp)) {
        val w = size.width
        val h = size.height

        // M
        drawRoundRect(
            color = Blue40,
            size = size.copy(height = h * 0.7f),
            topLeft = androidx.compose.ui.geometry.Offset(0f, h * 0.15f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.15f),
            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
        )
        

        drawLine(
            color = Blue40,
            start = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.45f),
            end = androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.45f),
            strokeWidth = w * 0.08f,
            cap = StrokeCap.Round
        )
    }
}
