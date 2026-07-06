package com.example.mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mobile.data.network.dto.MicroInsight
import com.example.mobile.ui.theme.*

@Composable
fun MicroInsightDialog(
    insight: MicroInsight,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!insight.shouldShow) return

    val isDark = isSystemInDarkTheme()

    // Màu sắc chủ đạo theo tone (Cảnh báo vs Gợi ý thông thường)
    val themeColor = when (insight.tone) {
        "WARNING" -> RedDanger
        else -> Color(0xFF1A237E) // Indigo sang trọng cho AI
    }
    val accentColor = when (insight.tone) {
        "WARNING" -> Color(0xFFE24B4A)
        else -> Blue40
    }

    // Nền modal sáng rõ, không bị xỉn màu mờ nhạt khi chồng lên lớp tối (scrim) của Dialog
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val contentColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    val boxBg = if (isDark) Color(0xFF0F172A).copy(alpha = 0.6f) else Color(0xFFF8FAFC)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.15f) else themeColor.copy(alpha = 0.15f)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = cardBg,
            shadowElevation = 24.dp,
            border = BorderStroke(1.5.dp, borderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- 1. AI BADGE HEADER ---
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = themeColor.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, themeColor.copy(alpha = 0.25f)),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "TRỢ LÝ TÀI CHÍNH AI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.W900,
                            color = themeColor,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // --- 2. ICON GLOW ---
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(themeColor.copy(alpha = 0.25f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = themeColor,
                        modifier = Modifier.size(48.dp),
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Lightbulb,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 3. TITLE ---
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W900,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- 4. MESSAGE QUOTE BOX ---
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = boxBg,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = parseMarkdownBold(insight.message, accentColor, contentColor),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.W500,
                            color = contentColor.copy(alpha = 0.9f),
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 5. PREMIUM ACTION BUTTON ---
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Đã hiểu, cảm ơn Trợ lý!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.W800
                        )
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

fun parseMarkdownBold(text: String, highlightColor: Color, defaultColor: Color): AnnotatedString {
    val parts = text.split("**")
    return buildAnnotatedString {
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                // Đoạn nằm giữa cặp dấu ** -> Tô đậm với màu nổi bật (Indigo / Red)
                pushStyle(
                    SpanStyle(
                        fontWeight = FontWeight.W900, 
                        color = highlightColor,
                        fontSize = 15.sp
                    )
                )
                append(part)
                pop()
            } else {
                // Đoạn text bình thường
                pushStyle(SpanStyle(color = defaultColor))
                append(part)
                pop()
            }
        }
    }
}
