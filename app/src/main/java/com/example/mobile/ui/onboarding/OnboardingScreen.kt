package com.example.mobile.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.GlassCard

@Composable
fun OnboardingScreen(
    onOnboardingComplete: (income: Double) -> Unit = {}, // Default lambda để Preview tự khởi chạy
    viewModel: OnboardingViewModel? = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    var income by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    val isLoading = viewModel?.isLoading?.collectAsState()?.value ?: false

    Box(modifier = modifier.fillMaxSize()) {
        AuroraBackground() // Nền Aurora đồng bộ DNA

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Thiết lập nền móng 🏗️",
                fontSize = 32.sp,
                fontWeight = FontWeight.W900,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Text(
                text = "Để AI có thể tính toán chính xác sức khỏe tài chính của bạn.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.Start).padding(bottom = 32.dp)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    OnboardingInputField(
                        value = income,
                        onValueChange = { income = it },
                        label = "Thu nhập hàng tháng",
                        placeholder = "Ví dụ: 20000000"
                    )

                    OnboardingInputField(
                        value = budget,
                        onValueChange = { budget = it },
                        label = "Ngân sách chi tiêu mong muốn",
                        placeholder = "Ví dụ: 12000000"
                    )

                    Button(
                        onClick = { 
                            if (viewModel != null) {
                                viewModel.submitOnboarding(income, budget) {
                                    onOnboardingComplete(income.toDoubleOrNull() ?: 0.0)
                                }
                            } else {
                                onOnboardingComplete(income.toDoubleOrNull() ?: 0.0)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = income.isNotEmpty() && budget.isNotEmpty() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Bắt đầu hành trình", fontWeight = FontWeight.W800, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, fontWeight = FontWeight.W700, fontSize = 14.sp, color = Color.White)
        OutlinedTextField(
            value = value,
            onValueChange = { if (it.all { char -> char.isDigit() }) onValueChange(it) },
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.3f)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
            ),
            suffix = { Text("VNĐ", color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold) }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "OnboardingScreen")
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(
        onOnboardingComplete = {},
        viewModel = null
    )
}
