package com.example.mobile.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.GlassCard
import com.example.mobile.ui.dashboard.components.TransactionItem
import com.example.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    navController: NavController? = null,
    viewModel: TransactionsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Lịch sử giao dịch",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.W800,
                            color = Color(0xFF1A237E)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController?.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1A237E)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Search Action */ }) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF1A237E)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Thanh chọn Bộ lọc Danh mục (Ăn uống, Đi lại, Mua sắm...)
                CategoryFilterRow(
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = { category -> viewModel.changeCategoryFilter(category) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1A237E))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (state.filteredTransactions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Không có giao dịch nào trong danh mục này",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF1A237E).copy(alpha = 0.6f),
                                        fontWeight = FontWeight.W600
                                    )
                                }
                            }
                        } else {
                            // Nhóm giao dịch theo ngày cho đẹp mắt giống thiết kế gốc
                            val groupedTransactions = state.filteredTransactions.groupBy { tx ->
                                try {
                                    val datePart = tx.transactionAt.split("T").first()
                                    val parts = datePart.split("-")
                                    "${parts[2]}/${parts[1]}"
                                } catch (e: Exception) {
                                    "Hôm nay"
                                }
                            }

                            groupedTransactions.forEach { (dateLabel, txList) ->
                                item {
                                    Text(
                                        text = dateLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color(0xFF1A237E).copy(alpha = 0.8f),
                                        fontWeight = FontWeight.W700,
                                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
                                    )

                                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                                        Column {
                                            txList.forEachIndexed { index, tx ->
                                                val iconConfig = when {
                                                    tx.type == "INCOME" -> Triple(Icons.Rounded.AccountBalance, SurfaceGreen, GreenSuccess)
                                                    tx.category.lowercase() == "food" -> Triple(Icons.Rounded.Coffee, SurfaceAmber, AmberWarning)
                                                    tx.category.lowercase() == "transport" -> Triple(Icons.Rounded.DirectionsBus, SurfaceBlue, Blue40)
                                                    tx.category.lowercase() == "shopping" -> Triple(Icons.Rounded.ShoppingCart, SurfaceAmber, AmberWarning)
                                                    tx.category.lowercase() == "debt" -> Triple(Icons.Rounded.CreditCard, SurfaceRed, RedDanger)
                                                    else -> Triple(Icons.Rounded.Receipt, SurfaceBlue, Blue40)
                                                }

                                                val timeFormatted = try {
                                                    val timePart = tx.transactionAt.split("T").last()
                                                    timePart.substring(0, 5) // "09:00"
                                                } catch (e: Exception) {
                                                    "00:00"
                                                }

                                                TransactionItem(
                                                    name = when (tx.category.lowercase()) {
                                                        "food" -> "Ăn uống"
                                                        "transport" -> "Đi lại"
                                                        "shopping" -> "Mua sắm"
                                                        "debt" -> "Trả nợ"
                                                        else -> "Khác"
                                                    },
                                                    date = timeFormatted,
                                                    amount = tx.amount.toLong(),
                                                    isIncome = tx.type == "INCOME",
                                                    icon = iconConfig.first,
                                                    iconBgColor = iconConfig.second,
                                                    iconTint = iconConfig.third
                                                )

                                                if (index < txList.lastIndex) {
                                                    HorizontalDivider(
                                                        modifier = Modifier.padding(vertical = 4.dp),
                                                        color = Color(0xFF1A237E).copy(alpha = 0.08f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Tất cả", "Ăn uống", "Đi lại", "Mua sắm", "Trả nợ")
    SecondaryScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
        containerColor = Color.Transparent,
        edgePadding = 16.dp,
        divider = {},
        indicator = {}
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                text = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF1A237E) else Color.White.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, Color(0xFF1A237E).copy(alpha = 0.15f)),
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.White else Color(0xFF1A237E),
                            fontWeight = if (isSelected) FontWeight.W800 else FontWeight.W600,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            fontSize = 13.sp
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransactionsScreenPreview() {
    TransactionsScreen(navController = rememberNavController())
}
