package com.example.mobile.ui.dashboard.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobile.ui.theme.*
import com.example.mobile.ui.components.GlassCard

data class BudgetCategory(
    val name: String,
    val spent: Long,
    val limit: Long,
)

@Composable
fun BudgetCard(
    totalSpent: Long,
    totalBudget: Long,
    categories: List<BudgetCategory>,
    modifier: Modifier = Modifier,
) {
    val ratio = (totalSpent.toFloat() / totalBudget.toFloat()).coerceIn(0f, 1f)

    val barColor = when {
        ratio >= 0.9f -> ScoreDanger
        ratio >= 0.75f -> ScoreWarning
        else -> ScoreGood
    }

    val animatedRatio by animateFloatAsState(
        targetValue = ratio,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "budgetAnimation"
    )

    GlassCard(modifier = modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Ngân sách tháng này",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600
            )
            Surface(
                shape = MaterialTheme.shapes.extraSmall,
                color = SurfaceBlue
            ) {
                Text(
                    text = "${(ratio * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Blue40,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Số tiền
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    "Đã tiêu",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${"%,.0f".format(totalSpent.toDouble())}đ",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.W700
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Còn lại",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${"%,.0f".format((totalBudget - totalSpent).toDouble())}đ",
                    style = MaterialTheme.typography.titleMedium,
                    color = barColor,
                    fontWeight = FontWeight.W600
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Progress bar tổng
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedRatio)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(barColor)
            )
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 0.5.dp)
        Spacer(Modifier.height(12.dp))

        // Category breakdown
        categories.forEach { cat ->
            val catRatio = (cat.spent.toFloat() / cat.limit.toFloat()).coerceIn(0f, 1f)
            val animCat by animateFloatAsState(
                targetValue = catRatio,
                animationSpec = tween(800, easing = FastOutSlowInEasing),
                label = "cat_${cat.name}"
            )
            val catColor = when {
                catRatio >= 0.9f -> ScoreDanger
                catRatio >= 0.75f -> ScoreWarning
                else -> Blue40
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = cat.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(80.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animCat)
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(catColor)
                    )
                }
                Text(
                    text = "${(catRatio * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = catColor,
                    modifier = Modifier.width(32.dp),
                    fontWeight = FontWeight.W600
                )
            }
        }
    }
}
