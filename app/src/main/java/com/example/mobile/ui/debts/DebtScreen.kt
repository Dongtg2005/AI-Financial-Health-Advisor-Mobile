package com.example.mobile.ui.debts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CreditScore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.BottomNav
import com.example.mobile.ui.components.GlassCard
import com.example.mobile.ui.debts.components.AddDebtDialog
import com.example.mobile.ui.debts.components.DebtCard
import com.example.mobile.ui.theme.*
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen(
    navController: NavController,
    viewModel: DebtViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.syncDebtsFromServer()
    }

    Scaffold(
        bottomBar = { BottomNav(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Blue40,
                contentColor = Color.White
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Thêm khoản nợ")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AuroraBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Quản lý Nợ nần",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Grey10
                        )
                        Text(
                            text = "Sức khỏe tín dụng & Cam kết trả nợ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Grey20
                        )
                    }

                    IconButton(
                        onClick = { showAddDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Blue40.copy(alpha = 0.15f),
                            contentColor = Blue40
                        )
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Summary GlassCard
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TỔNG DƯ NỢ HIỆN TẠI",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Grey20
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${formatCurrency(uiState.totalActiveDebt)} đ",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Blue10
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.CreditScore,
                            contentDescription = null,
                            tint = Blue40,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Quá hạn",
                                style = MaterialTheme.typography.bodySmall,
                                color = Grey20
                            )
                            Text(
                                text = "${uiState.overdueCount} khoản",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.overdueCount > 0) RedDanger else Grey10
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Sắp đến hạn (7 ngày)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Grey20
                            )
                            Text(
                                text = "${uiState.upcomingCount} khoản",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.upcomingCount > 0) AmberWarning else Grey10
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "DANH SÁCH KHOẢN NỢ (${uiState.debts.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Grey20
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.isLoading && uiState.debts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Blue40)
                    }
                } else if (uiState.debts.isEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Chưa có khoản nợ nào",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Grey10
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bạn đang không có dư nợ cần theo dõi. Thêm khoản nợ để AI quản lý lịch thanh toán tối thiểu giúp bạn!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Grey20,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(uiState.debts, key = { it.id }) { debt ->
                            DebtCard(
                                debt = debt,
                                onPayoffClick = { viewModel.payoffDebt(debt.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddDebtDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { type, balance, minPayment, dueDate ->
                viewModel.createDebt(type, balance, minPayment, dueDate) {
                    showAddDialog = false
                }
            }
        )
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(amount)
}
