package com.example.mobile.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.GlassCard
import com.example.mobile.ui.dashboard.components.TransactionItem

// 1. Tạo cấu trúc dữ liệu bọc theo nhóm ngày
data class DateGroupedTransactions(
    val dateLabel: String,
    val transactions: List<TransactionItemData>,
)

data class TransactionItemData(
    val name: String,
    val time: String,
    val amount: Long,
    val isIncome: Boolean,
    val category: String
)

@Preview(showBackground = true, showSystemUi = true, name = "TransactionsScreen")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(navController: NavController? = null) {
    // Dữ liệu mock cấu trúc động (Sau này sẽ lấy từ TransactionsViewModel)
    val groupedData = remember {
        listOf(
            DateGroupedTransactions(
                dateLabel = "Hôm nay",
                transactions = listOf(
                    TransactionItemData("Tiền hoa hồng tháng 6", "09:00", 12000000, isIncome = true, "income")
                )
            ),
            DateGroupedTransactions(
                dateLabel = "Hôm qua",
                transactions = listOf(
                    TransactionItemData("Vé xe giường nằm về Phú Tân", "20:15", 320000, false, "transport")
                )
            )
        )
    }

    var selectedMonthIndex by remember { mutableIntStateOf(0) }
    val months = listOf("Tháng 6", "Tháng 5", "Tháng 4", "Tháng 3")

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
                            fontWeight = FontWeight.W800
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController?.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Search Action */ }) {
                            Icon(Icons.Rounded.Search, contentDescription = "Search")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
            // Đã lược bỏ bottomBar vì đây là màn hình chi tiết (Detail) từ Dashboard sang
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Premium Month Selector
                SecondaryScrollableTabRow(
                    selectedTabIndex = selectedMonthIndex,
                    containerColor = Color.Transparent,
                    edgePadding = 16.dp,
                    divider = {},
                    indicator = {}
                ) {
                    months.forEachIndexed { index, month ->
                        Tab(
                            selected = selectedMonthIndex == index,
                            onClick = { selectedMonthIndex = index },
                            text = {
                                Text(
                                    month,
                                    fontWeight = if (selectedMonthIndex == index) FontWeight.W800 else FontWeight.W500,
                                    color = if (selectedMonthIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }

                // 2. Sử dụng LazyColumn lặp động bọc qua từng nhóm ngày
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(groupedData) { group ->
                        // Header ngày (Hôm nay, Hôm qua...)
                        Text(
                            text = group.dateLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontWeight = FontWeight.W700,
                            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
                        )

                        // Hộp kính chứa các giao dịch của ngày đó
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                group.transactions.forEachIndexed { index, tx ->
                                    TransactionItem(
                                        name = tx.name,
                                        date = tx.time,
                                        amount = tx.amount,
                                        isIncome = tx.isIncome,
                                        icon = if (tx.category == "income") Icons.Rounded.AccountBalance else Icons.Rounded.DirectionsBus,
                                        iconBgColor = if (tx.isIncome) Color(0xFFEAF3DE) else Color(0xFFE6F1FB),
                                        iconTint = if (tx.isIncome) Color(0xFF3B6D11) else Color(0xFF185FA5)
                                    )

                                    // Chỉ vẽ đường phân cách nếu không phải item cuối cùng trong cụm kính
                                    if (index < group.transactions.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Vùng an toàn dưới đáy để cuộn không bị dính sát viền
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}
