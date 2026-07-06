package com.example.mobile.ui.debts.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mobile.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtDialog(
    onDismiss: () -> Unit,
    onConfirm: (type: String, balance: Double, minimumPayment: Double, dueDate: String) -> Unit
) {
    var selectedType by remember { mutableStateOf("CREDIT_CARD") }
    var balanceText by remember { mutableStateOf("") }
    var minPaymentText by remember { mutableStateOf("") }
    var dueDateText by remember { 
        mutableStateOf(LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE)) 
    }

    val debtTypes = listOf(
        "CREDIT_CARD" to "Thẻ tín dụng",
        "SPAYLATER" to "SPayLater",
        "MOMO_PAYLATER" to "MoMo PayLater",
        "OTHER" to "Khác"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Thêm khoản nợ mới",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Blue10
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Loại khoản nợ", style = MaterialTheme.typography.labelMedium, color = Grey20)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    debtTypes.take(2).forEach { (code, label) ->
                        FilterChip(
                            selected = selectedType == code,
                            onClick = { selectedType = code },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Blue40,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    debtTypes.drop(2).forEach { (code, label) ->
                        FilterChip(
                            selected = selectedType == code,
                            onClick = { selectedType = code },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Blue40,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Dư nợ (VNĐ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = minPaymentText,
                    onValueChange = { minPaymentText = it },
                    label = { Text("Thanh toán tối thiểu (VNĐ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dueDateText,
                    onValueChange = { dueDateText = it },
                    label = { Text("Ngày đến hạn (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val balance = balanceText.toDoubleOrNull() ?: 0.0
                    val minPayment = minPaymentText.toDoubleOrNull() ?: 0.0
                    if (balance > 0) {
                        onConfirm(selectedType, balance, minPayment, dueDateText)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Blue40)
            ) {
                Text("Lưu khoản nợ", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = Grey20)
            }
        }
    )
}
