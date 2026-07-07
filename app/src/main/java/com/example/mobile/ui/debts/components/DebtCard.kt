package com.example.mobile.ui.debts.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.data.local.entity.DebtEntity
import com.example.mobile.ui.theme.*
import java.text.DecimalFormat

@Composable
fun DebtCard(
    debt: DebtEntity,
    modifier: Modifier = Modifier,
    onPayoffClick: (() -> Unit)? = null
) {
    val isOverdue = debt.overdueDays > 0 || isDatePast(debt.dueDate)
    val isUpcoming = !isOverdue && debt.daysRemaining in 0..7

    val (containerColor, borderColor, badgeColor, badgeTextColor, badgeText) = when {
        isOverdue -> {
            CardColorsBundle(
                container = SurfaceRed,
                border = RedDanger.copy(alpha = 0.5f),
                badgeBg = RedDanger.copy(alpha = 0.15f),
                badgeText = RedDanger,
                text = "Quá hạn ${debt.overdueDays} ngày"
            )
        }
        isUpcoming -> {
            CardColorsBundle(
                container = SurfaceAmber,
                border = AmberWarning.copy(alpha = 0.5f),
                badgeBg = AmberWarning.copy(alpha = 0.15f),
                badgeText = AmberWarning,
                text = "Sắp đến hạn (${debt.daysRemaining} ngày)"
            )
        }
        else -> {
            CardColorsBundle(
                container = GlassWhite70,
                border = Color.White.copy(alpha = 0.4f),
                badgeBg = Blue40.copy(alpha = 0.15f),
                badgeText = Blue40,
                text = "Còn ${debt.daysRemaining} ngày"
            )
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isOverdue) 4.dp else 0.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isOverdue) Icons.Rounded.Warning else Icons.Rounded.CreditCard,
                        contentDescription = null,
                        tint = if (isOverdue) RedDanger else Blue40,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = translateDebtType(debt.type),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Grey10
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeTextColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Dư nợ hiện tại",
                        style = MaterialTheme.typography.bodySmall,
                        color = Grey20
                    )
                    Text(
                        text = "${formatCurrency(debt.balance)} đ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isOverdue) RedDanger else Blue10
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Thanh toán tối thiểu",
                        style = MaterialTheme.typography.bodySmall,
                        color = Grey20
                    )
                    Text(
                        text = "${formatCurrency(debt.minimumPayment)} đ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Blue40
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = borderColor.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hạn thanh toán: ${debt.dueDate}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isOverdue) RedDanger else Grey20,
                    fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                if (onPayoffClick != null) {
                    TextButton(
                        onClick = onPayoffClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1A237E)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tất toán",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

private data class CardColorsBundle(
    val container: Color,
    val border: Color,
    val badgeBg: Color,
    val badgeText: Color,
    val text: String
)

private fun translateDebtType(type: String): String {
    return when (type.uppercase()) {
        "CREDIT_CARD" -> "Thẻ tín dụng"
        "SPAYLATER" -> "SPayLater"
        "MOMO_PAYLATER" -> "MoMo PayLater"
        else -> "Khoản nợ khác"
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(amount)
}

private fun isDatePast(dateStr: String): Boolean {
    return try {
        val date = java.time.LocalDate.parse(dateStr)
        date.isBefore(java.time.LocalDate.now())
    } catch (e: Exception) {
        false
    }
}
