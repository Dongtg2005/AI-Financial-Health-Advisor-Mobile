package com.example.mobile.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile.ui.dashboard.components.*
import com.example.mobile.ui.theme.*
import com.example.mobile.ui.components.GlassCard
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.AppLogo
import com.example.mobile.ui.components.BottomNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController = rememberNavController(),
    onNavigateToTransactions: () -> Unit = {},
    viewModel: DashboardViewModel? = null,
) {
    val state = if (viewModel != null) {
        viewModel.uiState.collectAsState().value
    } else {
        DashboardUiState(userName = "Đông (Preview)")
    }
    var showSheet by remember { mutableStateOf(false) }
    var showCashEstimateSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel?.fetchDashboardData()
    }

    if (showSheet) {
        AddTransactionSheet(
            onDismiss = { showSheet = false },
            onConfirm = { amount, note, category ->
                showSheet = false
                viewModel?.saveTransaction(
                    amount = amount.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO,
                    note = note,
                    category = category,
                    type = com.example.mobile.data.network.dto.TransactionType.EXPENSE
                )
            }
        )
    }

    if (showCashEstimateSheet) {
        WeeklyCashEstimateSheet(
            onDismiss = { showCashEstimateSheet = false },
            onConfirm = { totalAmount, category ->
                viewModel?.submitWeeklyCashEstimate(
                    totalAmount = totalAmount,
                    category = category,
                    onSuccess = { showCashEstimateSheet = false }
                )
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. NỀN HÀO QUANG: Tràn viền hoàn toàn (Edge-to-edge)
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.statusBarsPadding(), // Đẩy nội dung xuống dưới Status Bar
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppLogo(modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Xin chào,",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                )
                                Text(
                                    state.userName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.W800,
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.Notifications, contentDescription = "Thông báo")
                        }
                        Spacer(Modifier.width(4.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = { BottomNav(navController) }, // Thêm thanh điều hướng dưới cùng
            floatingActionButton = {
                // 3. BÓNG PHÁT SÁNG: FAB với quầng sáng xanh dương
                Box(
                    modifier = Modifier.drawBehind {
                        drawIntoCanvas { canvas ->
                            val paint = Paint().asFrameworkPaint()
                            paint.color = Blue40.copy(alpha = 0.4f).toArgb()
                            paint.setShadowLayer(30.dp.toPx(), 0f, 10.dp.toPx(), Blue40.copy(alpha = 0.5f).toArgb())
                            canvas.nativeCanvas.drawRoundRect(
                                0f, 0f, size.width, size.height,
                                16.dp.toPx(), 16.dp.toPx(), paint
                            )
                        }
                    }
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { showSheet = true },
                        icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                        text = { Text("Thêm giao dịch") },
                        containerColor = Blue40,
                        contentColor = Color.White,
                        shape = MaterialTheme.shapes.medium,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
                    )
                }
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // 4. CHỒNG LẤP: Đưa Score Ring chèn lên Header mờ ảo
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            HealthScoreCircle(
                                score = state.healthScore,
                                breakdown = state.scoreBreakdown,
                                sizeDp = 220,
                                strokeWidthDp = 18f
                            )

                            Text(
                                "Tài chính đang ổn định, tiếp tục phát huy nhé!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.padding(horizontal = 32.dp) // Tăng padding để ngắt dòng đẹp hơn
                            )

                            // Bảng chỉ số được gom nhóm lại cho ngay ngắn
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val breakdown = state.scoreBreakdown
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ScoreFactor("Chi tiêu", breakdown.spending, 35, Modifier.weight(1f))
                                    ScoreFactor("Nợ (DTI)", breakdown.debt, 35, Modifier.weight(1f))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ScoreFactor("Tiết kiệm", breakdown.saving, 20, Modifier.weight(1f))
                                    ScoreFactor("Cảnh giác", breakdown.awareness, 10, Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                item {
                    AlertCard(
                        title = state.alertTitle,
                        message = state.alertMessage,
                        visible = state.hasAlert,
                        onClick = {
                            if (state.alertTitle.contains("cuối tuần") || state.alertTitle.contains("TIỀN MẶT") || state.alertTitle.contains("TỔNG KẾT")) {
                                showCashEstimateSheet = true
                            }
                        }
                    )
                }

                // BIỂU ĐỒ CỘT ĐÔI: So sánh động Chi tiêu vs Hạn mức từ dữ liệu live Backend
                item {
                    CategoryCompareChart(
                        categories = state.budgetCategories,
                        heightDp = 180
                    )
                }

                item {
                    BudgetCard(
                        totalSpent = state.totalSpent,
                        totalBudget = state.totalBudget,
                        categories = state.budgetCategories.map {
                            BudgetCategory(it.name, it.spent, it.limit)
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Giao dịch gần đây",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.W800
                        )
                        TextButton(onClick = onNavigateToTransactions) {
                            Text("Xem tất cả", color = Blue40, fontWeight = FontWeight.W700)
                        }
                    }
                }

                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            state.recentTransactions.forEachIndexed { index, tx ->
                                val config = iconForCategory(tx.category, tx.isIncome)
                                TransactionItem(
                                    name = tx.name,
                                    date = tx.date,
                                    amount = tx.amount,
                                    isIncome = tx.isIncome,
                                    icon = config.icon,
                                    iconBgColor = config.bgColor,
                                    iconTint = config.tint
                                )
                                if (index < state.recentTransactions.lastIndex) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        thickness = 0.5.dp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (state.showInsightPopup && state.currentInsightData != null) {
            com.example.mobile.ui.components.MicroInsightDialog(
                insight = state.currentInsightData!!,
                onDismiss = { viewModel?.dismissInsightPopup() }
            )
        }
    }
}

@Composable
private fun ScoreFactor(
    label: String,
    value: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = Color.White.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontWeight = FontWeight.W600,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W800,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    " / $max",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 2.dp, start = 2.dp),
                    fontWeight = FontWeight.W700
                )
            }
        }
    }
}

private data class IconConfig(
    val icon: ImageVector,
    val bgColor: Color,
    val tint: Color
)

private fun iconForCategory(
    category: String,
    isIncome: Boolean
): IconConfig = when {
    isIncome -> IconConfig(Icons.Rounded.AccountBalance, SurfaceBlue, Blue40)
    category == "food" -> IconConfig(Icons.Rounded.Coffee, SurfaceAmber, AmberWarning)
    category == "transport" -> IconConfig(Icons.Rounded.DirectionsBus, SurfaceBlue, Blue40)
    category == "shopping" -> IconConfig(Icons.Rounded.ShoppingCart, SurfaceAmber, AmberWarning)
    category == "debt" -> IconConfig(Icons.Rounded.CreditCard, SurfaceRed, RedDanger)
    else -> IconConfig(Icons.Rounded.Receipt, SurfaceBlue, Blue40)
}

@Preview(showBackground = true, showSystemUi = true, name = "DashboardScreen")
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(
        navController = rememberNavController(),
        onNavigateToTransactions = {},
        viewModel = null
    )
}
