package com.example.mobile.ui.dashboard.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.ui.theme.*
import com.example.mobile.ui.components.GlassCard

@Composable
fun AlertCard(
    title: String,
    message: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val isCritical = title.contains("CẢNH BÁO") || title.contains("NGUY HIỂM")
    val isWarning = title.contains("THẺ TÍN DỤNG") || title.contains("ĐẾN HẠN")
    
    val themeColor = when {
        isCritical -> RedDanger
        isWarning -> AmberWarning
        else -> Blue40 // Xanh dương thanh lịch cho Gợi ý ngân sách
    }
    
    val icon = when {
        isCritical || isWarning -> Icons.Rounded.Warning
        else -> Icons.Rounded.Info
    }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn() + expandVertically(),
        exit    = fadeOut() + shrinkVertically()
    ) {
        // Sử dụng GlassCard với sắc đỏ/vàng/xanh nhẹ để đồng bộ DNA kính mờ
        GlassCard(
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(themeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = themeColor,
                        fontWeight = FontWeight.W800
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                        fontWeight = FontWeight.W600,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
