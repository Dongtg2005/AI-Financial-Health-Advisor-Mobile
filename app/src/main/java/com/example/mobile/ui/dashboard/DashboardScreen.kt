package com.example.mobile.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
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

import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController = rememberNavController(),
    onNavigateToTransactions: () -> Unit = {},
    viewModel: DashboardViewModel? = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val state = if (viewModel != null) {
        viewModel.uiState.collectAsState().value
    } else {
        DashboardUiState(userName = "Trần Ghi Đông")
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
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppLogo(modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Xin chào,",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF1A237E).copy(alpha = 0.6f), // Đổi màu Indigo tương phản cao
                                    fontWeight = FontWeight.W700
                                )
                                Text(
                                    state.userName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.W900,
                                    color = Color(0xFF1A237E) // Đổi màu Indigo sẫm
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.Notifications, contentDescription = "Thông báo", tint = Color(0xFF1A237E))
                        }
                        Spacer(Modifier.width(4.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = { BottomNav(navController) },
            floatingActionButton = {
                Box(
                    modifier = Modifier.drawBehind {
                        drawIntoCanvas { canvas ->
                            val paint = Paint().asFrameworkPaint()
                            paint.color = Color(0xFF1A237E).copy(alpha = 0.3f).toArgb()
                            paint.setShadowLayer(24.dp.toPx(), 0f, 8.dp.toPx(), Color(0xFF1A237E).copy(alpha = 0.4f).toArgb())
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
                        text = { Text("Thêm giao dịch", fontWeight = FontWeight.W800) },
                        containerColor = Color(0xFF1A237E), // Đồng bộ FAB sang Indigo
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                if (!state.adminNote.isNullOrBlank()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFC62828).copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC62828).copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFC62828),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "NHẮC NHỞ TỪ BAN QUẢN TRỊ AI",
                                        fontWeight = FontWeight.W900,
                                        color = Color(0xFFC62828),
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.adminNote,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF37474F),
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel?.clearAdminWarning() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFC62828),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.align(Alignment.End),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Đã hiểu", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("score_history") }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // 🔄 ĐẤU NỐI COMPONENT MỚI: Sử dụng HealthScoreRing xịn thay thế cho Circle lỗi màu cũ
                            HealthScoreRing(
                                score = state.healthScore,
                                ringSize = 200.dp,
                                strokeWidth = 14.dp
                            )

                            Text(
                                text = "Tài chính đang ổn định, tiếp tục phát huy nhé!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1A237E).copy(alpha = 0.8f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontWeight = FontWeight.W700,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth(),
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

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("spending_trends") },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A237E).copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A237E).copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.TrendingUp,
                                    contentDescription = null,
                                    tint = Color(0xFF1A237E),
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = "Phân tích xu hướng chi tiêu 📊",
                                        fontWeight = FontWeight.W800,
                                        color = Color(0xFF1A237E),
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "So sánh chi tiêu theo tháng, quý, năm",
                                        color = Color(0xFF1A237E).copy(alpha = 0.6f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Rounded.ArrowForwardIos,
                                contentDescription = null,
                                tint = Color(0xFF1A237E),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

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
                            fontWeight = FontWeight.W800,
                            color = Color(0xFF1A237E)
                        )
                        TextButton(onClick = onNavigateToTransactions) {
                            Text("Xem tất cả", color = Color(0xFF1A237E), fontWeight = FontWeight.W800)
                        }
                    }
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
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
                                        color = Color(0xFF1A237E).copy(alpha = 0.1f),
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
    // Tinh chỉnh Surface thành dạng kính mờ thực thụ, tăng độ tương phản của chữ
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.25f), // Giảm alpha xuống để tăng độ xuyên thấu kính mờ
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A237E).copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF1A237E).copy(alpha = 0.6f), // Đổi sang màu sẫm dễ đọc
                fontWeight = FontWeight.W700,
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W900,
                    color = Color(0xFF1A237E) // Điểm số hiện rõ mồn một
                )
                Text(
                    " / $max",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF1A237E).copy(alpha = 0.4f),
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
