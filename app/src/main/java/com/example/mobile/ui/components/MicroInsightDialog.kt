package com.example.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mobile.data.network.dto.MicroInsight
import com.example.mobile.ui.theme.AmberWarning
import com.example.mobile.ui.theme.Blue40

@Composable
fun MicroInsightDialog(
    insight: MicroInsight,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!insight.shouldShow) return

    // Tự động điều phối sắc thái màu sắc theo Tone dữ liệu trả về
    val themeColor = when (insight.tone) {
        "WARNING" -> AmberWarning
        else -> Blue40
    }

    Dialog(onDismissRequest = onDismiss) {
        // Tận dụng cấu trúc GlassCard Đông đã tối ưu để làm nền kính mờ cao cấp
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Biểu tượng bóng đèn phát sáng nhẹ trên nền kính mờ
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(themeColor.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lightbulb,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Tiêu đề của Insight
                Text(
                    text = insight.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W800,
                    color = themeColor,
                    textAlign = TextAlign.Center
                )

                // Nội dung thông điệp đã được bóc tách định dạng tô đậm các con số
                Text(
                    text = parseMarkdownBold(insight.message, MaterialTheme.colorScheme.onSurface),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Nút bấm tương tác đóng Popup mang phong cách tối giản
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                ) {
                    Text(
                        text = "Đã hiểu, cảm ơn Trợ lý! 👍",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W700,
                        color = Color.White
                    )
                }
            }
        }
    }
}

fun parseMarkdownBold(text: String, boldColor: Color): AnnotatedString {
    val parts = text.split("**")
    return buildAnnotatedString {
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                // Đoạn nằm giữa cặp dấu ** -> Tô đậm lên với màu tương phản tốt
                pushStyle(SpanStyle(fontWeight = FontWeight.W800, color = boldColor))
                append(part)
                pop()
            } else {
                // Đoạn text bình thường
                append(part)
            }
        }
    }
}
