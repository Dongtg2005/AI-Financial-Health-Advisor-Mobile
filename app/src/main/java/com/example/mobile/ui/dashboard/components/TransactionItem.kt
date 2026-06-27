package com.example.mobile.ui.dashboard.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mobile.ui.theme.*

@Composable
fun TransactionItem(
    name: String,
    date: String,
    amount: Long,
    isIncome: Boolean,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(MaterialTheme.shapes.small)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = iconTint,
                modifier           = Modifier.size(22.dp)
            )
        }

        // Tên + ngày
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text     = name,
                style    = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.W500,
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = date,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Số tiền
        Text(
            text  = "${if (isIncome) "+" else "-"}${"%,.0f".format(amount.toDouble())}đ",
            style = MaterialTheme.typography.titleMedium,
            color = if (isIncome) GreenSuccess else RedDanger,
            fontWeight = FontWeight.W600
        )
    }
}
