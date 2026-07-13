package com.example.mobile.ui.trends

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingFlat
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobile.data.network.dto.TrendItem
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.GlassCard
import java.math.BigDecimal
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendingTrendsScreen(
    navController: NavController,
    viewModel: SpendingTrendsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Month, 1: Quarter, 2: Year

    val currentList = when (selectedTab) {
        0 -> uiState.monthlyTrends
        1 -> uiState.quarterlyTrends
        else -> uiState.yearlyTrends
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            "Phân tích xu hướng",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W900,
                            color = Color(0xFF1A237E)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = Color(0xFF1A237E)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1A237E))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tab Selector for Monthly / Quarterly / Yearly
                    item {
                        TabSelector(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    }

                    // Visual Bar Chart
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Biểu đồ so sánh chi tiêu",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A237E),
                                    modifier = Modifier.align(Alignment.Start)
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                if (currentList.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Chưa có đủ dữ liệu giao dịch để phân tích",
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    }
                                } else {
                                    SpendingBarChart(items = currentList)
                                }
                            }
                        }
                    }

                    // Comparison Insights Card
                    if (currentList.size >= 2) {
                        item {
                            ComparisonInsightCard(currentList = currentList, selectedTab = selectedTab)
                        }
                    }

                    // Detailed List Section Header
                    item {
                        Text(
                            text = "Chi tiết số liệu",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A237E),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Detailed List Items
                    items(currentList) { item ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.label,
                                    fontWeight = FontWeight.W800,
                                    color = Color(0xFF1A237E)
                                )
                                Text(
                                    text = formatVnCurrency(item.amount),
                                    fontWeight = FontWeight.W900,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabSelector(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A237E).copy(alpha = 0.05f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val tabs = listOf("Theo tháng", "Theo quý", "Theo năm")
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedTab
            val bgColor = if (isSelected) Color(0xFF1A237E) else Color.Transparent
            val textColor = if (isSelected) Color.White else Color(0xFF1A237E).copy(alpha = 0.7f)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun SpendingBarChart(items: List<TrendItem>) {
    val maxVal = items.maxOfOrNull { it.amount } ?: BigDecimal.ONE
    val maxValFloat = if (maxVal.compareTo(BigDecimal.ZERO) == 0) 1.0f else maxVal.toFloat()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        items.forEach { item ->
            val ratio = item.amount.toFloat() / maxValFloat
            val animatedRatio = animateFloatAsState(
                targetValue = ratio,
                animationSpec = tween(durationMillis = 800)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Shortened currency label for top of the bar
                val shortenedLabel = if (item.amount.compareTo(BigDecimal.ZERO) == 0) {
                    "0"
                } else if (item.amount >= BigDecimal(1_000_000)) {
                    val millions = item.amount.toDouble() / 1_000_000.0
                    DecimalFormat("#.#M").format(millions)
                } else {
                    val thousands = item.amount.toDouble() / 1000.0
                    DecimalFormat("#k").format(thousands)
                }

                Text(
                    text = shortenedLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxHeight(animatedRatio.value * 0.75f + 0.05f)
                        .width(28.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFD32F2F),
                                    Color(0xFFFF8A80)
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E).copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ComparisonInsightCard(currentList: List<TrendItem>, selectedTab: Int) {
    val currentPeriod = currentList.last()
    val previousPeriod = currentList[currentList.size - 2]
    
    val diff = currentPeriod.amount.subtract(previousPeriod.amount)
    val isIncrease = diff.compareTo(BigDecimal.ZERO) > 0
    val isFlat = diff.compareTo(BigDecimal.ZERO) == 0

    val percentString = if (previousPeriod.amount.compareTo(BigDecimal.ZERO) > 0) {
        val pct = diff.multiply(BigDecimal(100)).divide(previousPeriod.amount, 1, java.math.RoundingMode.HALF_UP).abs()
        "$pct%"
    } else {
        "N/A"
    }

    val periodTypeName = when (selectedTab) {
        0 -> "tháng trước"
        1 -> "quý trước"
        else -> "năm trước"
    }

    val insightText = if (isFlat) {
        "Chi tiêu của bạn giữ nguyên so với $periodTypeName."
    } else if (isIncrease) {
        "Bạn đã chi tiêu **tăng thêm $percentString** so với $periodTypeName. Cân đối lại nhé!"
    } else {
        "Tuyệt vời! Bạn đã cắt giảm chi tiêu **$percentString** so với $periodTypeName."
    }

    val tintColor = if (isFlat) Color.Gray else if (isIncrease) Color(0xFFD32F2F) else Color(0xFF2E7D32)
    val icon = if (isFlat) Icons.Rounded.TrendingFlat else if (isIncrease) Icons.Rounded.TrendingUp else Icons.Rounded.TrendingDown

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tintColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tintColor
                )
            }
            Column {
                Text(
                    text = "Nhận định xu hướng 💡",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                // Helper standard parser for markdown bold formatting
                val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
                    val parts = insightText.split("**")
                    parts.forEachIndexed { idx, part ->
                        if (idx % 2 == 1) {
                            withStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(part)
                            }
                        } else {
                            append(part)
                        }
                    }
                }
                Text(
                    text = annotatedString,
                    fontSize = 11.sp,
                    color = Color(0xFF37474F)
                )
            }
        }
    }
}

fun formatVnCurrency(amount: BigDecimal): String {
    return DecimalFormat("#,###đ").format(amount)
}
