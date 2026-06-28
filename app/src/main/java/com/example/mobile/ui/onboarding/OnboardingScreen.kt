package com.example.mobile.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.GlassCard
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

@Preview(showBackground = true, showSystemUi = true, name = "OnboardingScreen")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: (income: Double) -> Unit = {}, // Default lambda để Preview tự khởi chạy
    viewModel: OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    // 3 steps setup: Step 1 (Income), Step 2 (Debts), Step 3 (Notification description)
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    
    var incomeInput by remember { mutableStateOf("") }
    var hasDebt by remember { mutableStateOf(false) }
    var debtBalance by remember { mutableStateOf("") }
    var debtDueDate by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {
        AuroraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header spacing or App Name
            Text(
                text = "FINANCE ADVISOR",
                fontSize = 18.sp,
                fontWeight = FontWeight.W800,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 28.dp)
            )

            // Center Pager inside GlassCard for premium feel
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 32.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false // Khóa vuốt tay để bắt buộc validate qua từng bước
                ) { page ->
                    when (page) {
                        0 -> StepIncomeLayout(
                            income = incomeInput,
                            onIncomeChange = { incomeInput = it }
                        )
                        1 -> StepDebtLayout(
                            hasDebt = hasDebt,
                            onHasDebtChange = { hasDebt = it },
                            debtBalance = debtBalance,
                            onBalanceChange = { debtBalance = it },
                            debtDueDate = debtDueDate,
                            onDueDateChange = { debtDueDate = it }
                        )
                        2 -> StepNotificationLayout()
                    }
                }
            }

            // Navigation Controls at the bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Step Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(3) { index ->
                        val active = pagerState.currentPage == index
                        val color = if (active) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (active) 18.dp else 8.dp, height = 8.dp)
                                .background(
                                    color = color,
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            // Gọi ngầm API gợi ý ngân sách ngay sau khi nhập thu nhập ở bước 0
                            if (pagerState.currentPage == 0 && incomeInput.isNotEmpty()) {
                                val incomeValue = incomeInput.toDoubleOrNull() ?: 0.0
                                viewModel.fetchBudgetSuggestion(incomeValue)
                            }
                            
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            // Hoàn thành onboarding
                            val finalIncome = incomeInput.toDoubleOrNull() ?: 0.0
                            onOnboardingComplete(finalIncome)
                        }
                    },
                    enabled = when(pagerState.currentPage) {
                        0 -> incomeInput.trim().isNotEmpty()
                        1 -> !hasDebt || (debtBalance.trim().isNotEmpty() && debtDueDate.trim().isNotEmpty())
                        else -> true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage == 2) "Bắt đầu ngay" else "Tiếp tục",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W800,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun StepIncomeLayout(income: String, onIncomeChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Xin chào! 👋",
            fontSize = 28.sp,
            fontWeight = FontWeight.W800,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Mỗi tháng bạn kiếm được khoảng bao nhiêu?",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(36.dp))
        
        OutlinedTextField(
            value = income,
            onValueChange = onIncomeChange,
            label = { Text("Thu nhập hàng tháng") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            suffix = { Text("đ", fontWeight = FontWeight.Bold) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StepDebtLayout(
    hasDebt: Boolean,
    onHasDebtChange: (Boolean) -> Unit,
    debtBalance: String,
    onBalanceChange: (String) -> Unit,
    debtDueDate: String,
    onDueDateChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quản lý khoản nợ 💳",
            fontSize = 24.sp,
            fontWeight = FontWeight.W800,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Để AI hỗ trợ bạn tính toán cảnh báo nợ đến hạn thông minh.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Checkbox(
                checked = hasDebt,
                onCheckedChange = onHasDebtChange,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "Tôi đang có một vài khoản nợ cần trả (Thẻ tín dụng, SPayLater, MoMo...)",
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        AnimatedVisibility(
            visible = hasDebt,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                OutlinedTextField(
                    value = debtBalance,
                    onValueChange = onBalanceChange,
                    label = { Text("Tổng số dư nợ hiện tại (đ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = debtDueDate,
                    onValueChange = onDueDateChange,
                    label = { Text("Ngày đến hạn trả nợ hàng tháng (Ví dụ: 25)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun StepNotificationLayout() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Nhắc nhở thông minh 🔔",
            fontSize = 24.sp,
            fontWeight = FontWeight.W800,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Cam kết bảo mật & Tránh làm phiền:",
            fontSize = 15.sp,
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Ứng dụng chỉ gửi tối đa 2 nhắc nhở/ngày (13h trưa & 21h tối) để bạn review nhanh các giao dịch. Chúng tôi cam kết tuyệt đối KHÔNG đọc lén tin nhắn hay dữ liệu ngân hàng của bạn.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.W500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}
