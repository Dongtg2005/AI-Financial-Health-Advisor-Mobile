package com.example.mobile.ui.dashboard.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mobile.ui.theme.Blue40
import com.example.mobile.ui.theme.SurfaceBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    onDismiss: () -> Unit,
    onConfirm: (amount: String, note: String, category: String) -> Unit, // Truyền dữ liệu ngược ra ngoài khi bấm Lưu
) {
    // 1. Quản lý trạng thái nhập liệu (Dynamic State)
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategoryCode by remember { mutableStateOf("food") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "Thêm giao dịch mới",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W800
            )

            // 2. Khu vực chọn danh mục (Clickable Category List)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val categories = listOf(
                    Triple(Icons.Rounded.Coffee, "Ăn uống", "food"),
                    Triple(Icons.Rounded.DirectionsBus, "Đi lại", "transport"),
                    Triple(Icons.Rounded.ShoppingCart, "Mua sắm", "shopping")
                )

                categories.forEach { (icon, label, code) ->
                    CategoryIcon(
                        icon = icon,
                        label = label,
                        isSelected = selectedCategoryCode == code,
                    ) { selectedCategoryCode = code }
                }
            }

            // 3. Ô nhập số tiền kèm bàn phím số
            OutlinedTextField(
                value = amount,
                onValueChange = { input -> 
                    // Chỉ cho phép nhập số
                    if (input.all { it.isDigit() }) amount = input 
                },
                label = { Text("Số tiền") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                prefix = { Text("đ ", fontWeight = FontWeight.W700) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Tự động bật bàn phím số
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue40,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                )
            )

            // 4. Ô nhập ghi chú
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue40,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                )
            )

            // 5. Nút Lưu
            Button(
                onClick = { onConfirm(amount, note, selectedCategoryCode) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = Blue40),
                enabled = amount.isNotEmpty() // Chỉ cho bấm Lưu khi đã điền số tiền
            ) {
                Text(
                    "Lưu giao dịch", 
                    fontWeight = FontWeight.W800, 
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun CategoryIcon(
    icon: ImageVector, 
    label: String, 
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Hiệu ứng chuyển màu mượt mà khi click (Micro-interaction)
    val bgColor by animateColorAsState(if (isSelected) Blue40 else SurfaceBlue, label = "bgColorAnim")
    val iconColor by animateColorAsState(if (isSelected) Color.White else Blue40, label = "iconColorAnim")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(onClick = onClick) // Thêm sự kiện click cho cả cụm
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor
            )
        }
        Text(
            label, 
            style = MaterialTheme.typography.labelSmall, 
            fontWeight = if (isSelected) FontWeight.W900 else FontWeight.W700,
            color = if (isSelected) Blue40 else Color.Gray
        )
    }
}
