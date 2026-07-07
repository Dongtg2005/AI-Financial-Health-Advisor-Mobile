package com.example.mobile.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.GlassCard
import com.example.mobile.ui.dashboard.components.TransactionItem
import com.example.mobile.ui.theme.*
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    navController: NavController? = null,
    viewModel: TransactionsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    // Bottom sheet thêm giao dịch
    if (showAddSheet) {
        TransactionAddSheet(
            onDismiss = { showAddSheet = false },
            onConfirm = { amount, category, type ->
                showAddSheet = false
                viewModel.saveTransaction(amount, category, type)
            }
        )
    }

    TransactionsScreenContent(
        state = state,
        isLoading = isLoading,
        navController = navController,
        onCategorySelected = { category -> viewModel.changeCategoryFilter(category) },
        onAddClick = { showAddSheet = true },
        onSearchQueryChanged = { query -> viewModel.changeSearchQuery(query) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreenContent(
    state: TransactionsUiState,
    isLoading: Boolean,
    navController: NavController? = null,
    onCategorySelected: (String) -> Unit = {},
    onAddClick: () -> Unit = {},
    onSearchQueryChanged: (String) -> Unit = {}
) {
    var isSearching by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        if (isSearching) {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = onSearchQueryChanged,
                                placeholder = { Text("Tìm kiếm giao dịch...", color = Color(0xFF1A237E).copy(alpha = 0.5f)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1A237E)),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1A237E),
                                    unfocusedBorderColor = Color(0xFF1A237E).copy(alpha = 0.3f)
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = null,
                                        tint = Color(0xFF1A237E)
                                    )
                                },
                                trailingIcon = {
                                    if (state.searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { onSearchQueryChanged("") }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = "Xóa tìm kiếm",
                                                tint = Color(0xFF1A237E)
                                            )
                                        }
                                    }
                                }
                            )
                        } else {
                            Text(
                                "Lịch sử giao dịch",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.W800,
                                color = Color(0xFF1A237E)
                            )
                        }
                    },
                    navigationIcon = {
                        if (isSearching) {
                            IconButton(onClick = {
                                isSearching = false
                                onSearchQueryChanged("")
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Đóng tìm kiếm",
                                    tint = Color(0xFF1A237E)
                                )
                            }
                        } else {
                            IconButton(onClick = { navController?.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFF1A237E)
                                )
                            }
                        }
                    },
                    actions = {
                        if (!isSearching) {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = "Tìm kiếm",
                                    tint = Color(0xFF1A237E)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = Color(0xFF1A237E),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Them giao dich",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                CategoryFilterRow(
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = onCategorySelected
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1A237E))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp, top = 8.dp),
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
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Receipt,
                                            contentDescription = null,
                                            tint = Color(0xFF1A237E).copy(alpha = 0.25f),
                                            modifier = Modifier.size(56.dp)
                                        )
                                        Text(
                                            text = "Chua co giao dich nao",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color(0xFF1A237E).copy(alpha = 0.6f),
                                            fontWeight = FontWeight.W700
                                        )
                                        Text(
                                            text = "Nhan + de them giao dich dau tien",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF1A237E).copy(alpha = 0.4f),
                                            fontWeight = FontWeight.W500
                                        )
                                    }
                                }
                            }
                        } else {
                            val groupedTransactions = state.filteredTransactions.groupBy { tx ->
                                try {
                                    val datePart = tx.transactionAt.split("T").first()
                                    val parts = datePart.split("-")
                                    "${parts[2]}/${parts[1]}"
                                } catch (e: Exception) {
                                    "Hom nay"
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
                                                    timePart.substring(0, 5)
                                                } catch (e: Exception) {
                                                    "00:00"
                                                }

                                                TransactionItem(
                                                    name = when (tx.category.lowercase()) {
                                                        "food" -> "An uong"
                                                        "transport" -> "Di lai"
                                                        "shopping" -> "Mua sam"
                                                        "debt" -> "Tra no"
                                                        "income" -> "Thu nhap"
                                                        "savings" -> "Tiet kiem"
                                                        else -> "Khac"
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

// ─── BOTTOM SHEET THEM GIAO DICH ───────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionAddSheet(
    onDismiss: () -> Unit,
    onConfirm: (amount: String, category: String, type: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("food") }
    var selectedType by remember { mutableStateOf("EXPENSE") }

    val expenseCategories = listOf(
        "food" to "An uong",
        "transport" to "Di lai",
        "shopping" to "Mua sam",
        "other" to "Khac"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Them giao dich moi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W800,
                color = Color(0xFF1A237E)
            )

            // Chon loai giao dich
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("EXPENSE" to "Chi tieu", "INCOME" to "Thu nhap").forEach { (value, label) ->
                    val isSelected = selectedType == value
                    Surface(
                        onClick = {
                            selectedType = value
                            selectedCategory = if (value == "INCOME") "income" else "food"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFF1A237E) else Color(0xFFF5F5F5),
                        border = BorderStroke(
                            1.5.dp,
                            if (isSelected) Color(0xFF1A237E) else Color(0xFFE0E0E0)
                        )
                    ) {
                        Text(
                            label,
                            modifier = Modifier
                                .padding(vertical = 14.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W700,
                            fontSize = 15.sp,
                            color = if (isSelected) Color.White else Color(0xFF424242)
                        )
                    }
                }
            }

            // Nhap so tien
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                label = { Text("So tien (VND)", fontWeight = FontWeight.W600) },
                placeholder = { Text("Vi du: 150000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1A237E),
                    focusedLabelColor = Color(0xFF1A237E)
                ),
                trailingIcon = {
                    Text("d", fontWeight = FontWeight.W700, color = Color(0xFF1A237E),
                        modifier = Modifier.padding(end = 8.dp))
                }
            )

            // Chon danh muc (chi hien khi Chi tieu)
            if (selectedType == "EXPENSE") {
                Text(
                    "Danh muc",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFF1A237E).copy(alpha = 0.8f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    expenseCategories.forEach { (value, label) ->
                        val isSelected = selectedCategory == value
                        Surface(
                            onClick = { selectedCategory = value },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF1A237E).copy(alpha = 0.1f) else Color(0xFFF5F5F5),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) Color(0xFF1A237E) else Color(0xFFE0E0E0)
                            )
                        ) {
                            Text(
                                label,
                                modifier = Modifier
                                    .padding(vertical = 10.dp, horizontal = 2.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontWeight = if (isSelected) FontWeight.W700 else FontWeight.W500,
                                fontSize = 11.sp,
                                color = if (isSelected) Color(0xFF1A237E) else Color(0xFF757575)
                            )
                        }
                    }
                }
            }

            // Nut Luu
            val isAmountValid = amountText.isNotBlank() &&
                amountText.toBigDecimalOrNull()?.let { it > BigDecimal.ZERO } == true

            Button(
                onClick = { if (isAmountValid) onConfirm(amountText, selectedCategory, selectedType) },
                enabled = isAmountValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Luu giao dich",
                    fontWeight = FontWeight.W800,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun CategoryFilterRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Tat ca", "An uong", "Di lai", "Mua sam", "Tra no")
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
    FinanceAppTheme {
        TransactionsScreenContent(
            state = TransactionsUiState(
                transactions = listOf(
                    com.example.mobile.data.network.dto.TransactionResponseDTO(
                        id = "1",
                        userId = "user_id",
                        type = "EXPENSE",
                        amount = BigDecimal(250000),
                        category = "food",
                        transactionAt = "2026-07-07T12:00:00",
                        isConfirmed = true
                    )
                ),
                filteredTransactions = listOf(
                    com.example.mobile.data.network.dto.TransactionResponseDTO(
                        id = "1",
                        userId = "user_id",
                        type = "EXPENSE",
                        amount = BigDecimal(250000),
                        category = "food",
                        transactionAt = "2026-07-07T12:00:00",
                        isConfirmed = true
                    )
                )
            ),
            isLoading = false,
            navController = null
        )
    }
}
