package com.example.mobile.ui.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyCashEstimateSheet(
    onDismiss: () -> Unit,
    onConfirm: (amount: BigDecimal, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var amountInput by remember { mutableStateOf("") }
    // Danh mục mặc định là "Ăn uống" (khớp với nhánh logic rải đều)
    var selectedCategory by remember { mutableStateOf("food") }
    val categories = listOf(
        "food" to "🍚 Ăn uống",
        "transport" to "🚗 Đi lại",
        "shopping" to "🛍️ Mua sắm",
        "other" to "💼 Khác"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Ước tính tiền mặt cuối tuần 💰",
                fontSize = 20.sp,
                fontWeight = FontWeight.W800,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Hệ thống AI sẽ tự động phân bổ đều con số tổng này vào 7 ngày trong tuần để giữ các chỉ số sức khỏe tài chính của bạn cân bằng.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            // Ô nhập số tiền mặt dạng Outline tinh tế
            OutlinedTextField(
                value = amountInput,
                onValueChange = { input -> 
                    // Chỉ cho phép nhập số tự nhiên
                    if (input.all { it.isDigit() }) amountInput = input 
                },
                label = { Text("Tổng tiền mặt đã tiêu tuần này") },
                suffix = { Text("đ", fontWeight = FontWeight.W700) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Text(
                text = "Chọn nhóm chi tiêu chiếm phần lớn:",
                fontSize = 14.sp,
                fontWeight = FontWeight.W700
            )

            // Hàng chọn nhanh danh mục (Filter Chips)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (key, label) ->
                    val isSelected = selectedCategory == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = key },
                        label = { Text(label) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nút xác nhận gửi dữ liệu lên Server thông qua ViewModel
            Button(
                onClick = {
                    val parsedAmount = amountInput.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    if (parsedAmount > BigDecimal.ZERO) {
                        onConfirm(parsedAmount, selectedCategory)
                    }
                },
                enabled = amountInput.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Xác nhận & Phân bổ ngầm", fontSize = 16.sp, fontWeight = FontWeight.W700)
            }
        }
    }
}
