package com.example.mobile.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobile.data.local.TokenManager
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.BottomNav
import com.example.mobile.ui.components.GlassCard
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier,
    onLogoutClick: (() -> Unit)? = null,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by profileViewModel.uiState.collectAsState()
    val tokenManager = remember { TokenManager(context) }

    val actualLogoutClick = onLogoutClick ?: {
        tokenManager.clearAuthData()
        navController.navigate("login") {
            popUpTo("dashboard") { inclusive = true }
        }
    }

    val userName = tokenManager.getUserName()
    val formattedBudget = remember(uiState.suggestedBudget) {
        val formatter = DecimalFormat("#,###")
        formatter.format(uiState.suggestedBudget) + " VNĐ"
    }

    // Interactive Dialog States
    var showAccountInfoDialog by remember { mutableStateOf(false) }
    var showWalletsDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showNotificationSettingsDialog by remember { mutableStateOf(false) }
    var showExportExcelDialog by remember { mutableStateOf(false) }
    var showStatementCycleDialog by remember { mutableStateOf(false) }

    // Account & Wallets temporary inputs
    var newWalletName by remember { mutableStateOf("") }
    var newWalletBalance by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { BottomNav(navController) }
        ) { padding ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1A237E))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // TITLE MÀN HÌNH
                    Text(
                        text = "Cá nhân",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.W900,
                        color = Color(0xFF1A237E),
                        modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                    )

                    // AVATAR & TÊN
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1A237E).copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = Color(0xFF1A237E),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = userName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.W800,
                                color = Color(0xFF1A237E)
                            )
                            Text(
                                text = "Thành viên tài chính thông minh",
                                fontSize = 13.sp,
                                color = Color(0xFF1A237E).copy(alpha = 0.6f)
                            )
                        }
                    }

                    // CARD TỔNG QUAN NGÂN SÁCH
                    var isBalanceVisible by remember { mutableStateOf(true) }
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ngân sách tháng này",
                                fontSize = 13.sp,
                                color = Color(0xFF1A237E).copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBalanceVisible) formattedBudget else "•••••• VNĐ",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.W900,
                                    color = Color(0xFF1A237E)
                                )
                                IconButton(onClick = { isBalanceVisible = !isBalanceVisible }) {
                                    Icon(
                                        imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Ẩn số dư",
                                        tint = Color(0xFF1A237E)
                                    )
                                }
                            }
                        }
                    }

                    // MENU QUẢN LÝ TÀI KHOẢN
                    Text(
                        text = "Quản lý tài khoản",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W800,
                        color = Color(0xFF1A237E).copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
                        Column {
                            ProfileMenuItem(
                                icon = Icons.Default.AccountCircle,
                                title = "Thông tin tài khoản",
                                badge = "Đã xác thực",
                                onClick = { showAccountInfoDialog = true }
                            )
                            HorizontalDivider(color = Color(0xFF1A237E).copy(alpha = 0.08f), thickness = 1.dp)
                            ProfileMenuItem(
                                icon = Icons.Default.ShoppingCart,
                                title = "Tài khoản liên kết / Ví",
                                badge = "${uiState.walletsList.size} Ví",
                                onClick = { showWalletsDialog = true }
                            )
                            HorizontalDivider(color = Color(0xFF1A237E).copy(alpha = 0.08f), thickness = 1.dp)
                            ProfileMenuItem(
                                icon = Icons.Default.Star,
                                title = "Hạn mức chi tiêu tháng",
                                badge = "Đang chạy",
                                onClick = { showBudgetDialog = true }
                            )
                        }
                    }

                    // MENU TIỆN ÍCH ỨNG DỤNG
                    Text(
                        text = "Tiện ích ứng dụng",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W800,
                        color = Color(0xFF1A237E).copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Column {
                            ProfileMenuItem(
                                icon = Icons.Default.Notifications,
                                title = "Cài đặt thông báo",
                                onClick = { showNotificationSettingsDialog = true }
                            )
                            HorizontalDivider(color = Color(0xFF1A237E).copy(alpha = 0.08f), thickness = 1.dp)
                            ProfileMenuItem(
                                icon = Icons.Default.Share,
                                title = "Xuất báo cáo tài chính (Excel)",
                                badge = "Mới",
                                onClick = { showExportExcelDialog = true }
                            )
                            HorizontalDivider(color = Color(0xFF1A237E).copy(alpha = 0.08f), thickness = 1.dp)
                            ProfileMenuItem(
                                icon = Icons.Default.Settings,
                                title = "Cài đặt chu kỳ sao kê",
                                onClick = { showStatementCycleDialog = true }
                            )
                        }
                    }

                    // ĐĂNG XUẤT TÀI KHOẢN
                    Button(
                        onClick = actualLogoutClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F).copy(alpha = 0.1f),
                            contentColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Đăng xuất tài khoản", fontWeight = FontWeight.W800, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // ================= DIALOGS IMPLEMENTATION =================

        // 1. Dialog: Thông tin tài khoản
        if (showAccountInfoDialog) {
            AlertDialog(
                onDismissRequest = { showAccountInfoDialog = false },
                title = { Text("Thông tin tài khoản 👤", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("• Tên tài khoản: $userName", fontWeight = FontWeight.Medium)
                        Text("• Vai trò hệ thống: Thành viên", fontWeight = FontWeight.Medium)
                        Text("• Trạng thái: Đã xác thực bảo mật", fontWeight = FontWeight.Medium, color = Color(0xFF2E7D32))
                        Text("• Múi giờ mặc định: Asia/Ho_Chi_Minh (GMT+7)", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAccountInfoDialog = false }) {
                        Text("Đóng", color = Color(0xFF1A237E), fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // 2. Dialog: Ví liên kết
        if (showWalletsDialog) {
            AlertDialog(
                onDismissRequest = { showWalletsDialog = false },
                title = { Text("Ví & Tài khoản liên kết 💳", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        uiState.walletsList.forEach { wallet ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1A237E).copy(alpha = 0.05f))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CreditCard, null, tint = Color(0xFF1A237E), modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(wallet.first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Text(DecimalFormat("#,###đ").format(wallet.second), fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                            }
                        }

                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                        Text("Thêm ví liên kết mới", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E), fontSize = 13.sp)
                        OutlinedTextField(
                            value = newWalletName,
                            onValueChange = { newWalletName = it },
                            label = { Text("Tên ví / Ngân hàng") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newWalletBalance,
                            onValueChange = { newWalletBalance = it },
                            label = { Text("Số dư ban đầu") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val bal = newWalletBalance.toDoubleOrNull()
                            if (newWalletName.isNotBlank() && bal != null) {
                                profileViewModel.addWallet(newWalletName, bal) { success ->
                                    if (success) {
                                        newWalletName = ""
                                        newWalletBalance = ""
                                        Toast.makeText(context, "Đã liên kết ví thành công!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Lỗi liên kết ví", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Vui lòng điền đúng thông tin ví", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                    ) {
                        Text("Liên kết")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showWalletsDialog = false }) {
                        Text("Hủy", color = Color.Gray)
                    }
                }
            )
        }

        // 3. Dialog: Cài đặt hạn mức ngân sách tháng
        if (showBudgetDialog) {
            var budgetInput by remember { mutableStateOf(uiState.suggestedBudget.toInt().toString()) }
            AlertDialog(
                onDismissRequest = { showBudgetDialog = false },
                title = { Text("Cấu hình hạn mức chi tiêu 🎯", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Thay đổi hạn mức chi tiêu tháng này của bạn. Hệ thống AI sẽ tự động phân bổ ngân sách cho từng danh mục dựa trên hạn mức này.", fontSize = 13.sp)
                        OutlinedTextField(
                            value = budgetInput,
                            onValueChange = { budgetInput = it },
                            label = { Text("Ngân sách (VNĐ)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newB = budgetInput.toDoubleOrNull()
                            if (newB != null && newB > 0) {
                                profileViewModel.updateBudget(newB) { success ->
                                    if (success) {
                                        showBudgetDialog = false
                                        Toast.makeText(context, "Đã cập nhật hạn mức ngân sách thành công!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Lỗi cập nhật hạn mức trên máy chủ", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                    ) {
                        Text("Cập nhật")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBudgetDialog = false }) {
                        Text("Hủy", color = Color.Gray)
                    }
                }
            )
        }

        // 4. Dialog: Cài đặt thông báo
        if (showNotificationSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showNotificationSettingsDialog = false },
                title = { Text("Cài đặt thông báo 🔔", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Nhắc nhở chi tiêu", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Nhận thông báo ghi chép giao dịch hàng ngày.", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = uiState.dailyNotificationsEnabled,
                                onCheckedChange = { checked ->
                                    profileViewModel.updateSettings(
                                        dailyNotif = checked,
                                        aiAlerts = uiState.aiAlertsEnabled,
                                        cycleDay = uiState.billingCycleDay
                                    ) { success ->
                                        if (!success) {
                                            Toast.makeText(context, "Lỗi cập nhật cấu hình", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF1A237E))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Cảnh báo AI", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Nhận các gợi ý tối ưu và cảnh báo rủi ro từ AI.", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = uiState.aiAlertsEnabled,
                                onCheckedChange = { checked ->
                                    profileViewModel.updateSettings(
                                        dailyNotif = uiState.dailyNotificationsEnabled,
                                        aiAlerts = checked,
                                        cycleDay = uiState.billingCycleDay
                                    ) { success ->
                                        if (!success) {
                                            Toast.makeText(context, "Lỗi cập nhật cấu hình", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF1A237E))
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showNotificationSettingsDialog = false
                            Toast.makeText(context, "Đã lưu cài đặt thông báo!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Xong", color = Color(0xFF1A237E), fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // 5. Dialog: Xuất báo cáo tài chính
        if (showExportExcelDialog) {
            AlertDialog(
                onDismissRequest = { if (!uiState.isExporting) showExportExcelDialog = false },
                title = { Text("Xuất báo cáo tài chính 📊", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E)) },
                text = {
                    if (uiState.isExporting) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1A237E))
                            Text("Đang tải dữ liệu và kết xuất tệp CSV...", fontSize = 13.sp, color = Color.Gray)
                        }
                    } else {
                        Text("Tải xuống toàn bộ dữ liệu giao dịch của bạn định dạng tệp Excel/CSV (.csv) để quản lý hoặc nhập liệu ngoại tuyến.", fontSize = 14.sp)
                    }
                },
                confirmButton = {
                    if (!uiState.isExporting) {
                        Button(
                            onClick = {
                                profileViewModel.exportTransactionsExcel(
                                    onSuccess = { filePath ->
                                        showExportExcelDialog = false
                                        Toast.makeText(context, "Đã tải thành công tệp: $filePath", Toast.LENGTH_LONG).show()
                                    },
                                    onFailure = { error ->
                                        Toast.makeText(context, "Lỗi xuất báo cáo: $error", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                        ) {
                            Text("Tải xuống")
                        }
                    }
                },
                dismissButton = {
                    if (!uiState.isExporting) {
                        TextButton(onClick = { showExportExcelDialog = false }) {
                            Text("Hủy", color = Color.Gray)
                        }
                    }
                }
            )
        }

        // 6. Dialog: Chu kỳ sao kê
        if (showStatementCycleDialog) {
            AlertDialog(
                onDismissRequest = { showStatementCycleDialog = false },
                title = { Text("Cài đặt ngày chu kỳ sao kê 📅", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Chọn ngày bắt đầu chốt sao kê / tổng kết tài chính định kỳ hàng tháng:", fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))

                        val days = listOf(1, 5, 15, 25)
                        days.forEach { day ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        profileViewModel.updateSettings(
                                            dailyNotif = uiState.dailyNotificationsEnabled,
                                            aiAlerts = uiState.aiAlertsEnabled,
                                            cycleDay = day
                                        ) { success ->
                                            if (!success) {
                                                Toast.makeText(context, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = uiState.billingCycleDay == day,
                                    onClick = {
                                        profileViewModel.updateSettings(
                                            dailyNotif = uiState.dailyNotificationsEnabled,
                                            aiAlerts = uiState.aiAlertsEnabled,
                                            cycleDay = day
                                        ) { success ->
                                            if (!success) {
                                                Toast.makeText(context, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1A237E))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ngày $day hàng tháng", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showStatementCycleDialog = false
                            Toast.makeText(context, "Đã lưu cài đặt ngày chốt chu kỳ sao kê!", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                    ) {
                        Text("Đóng")
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF1A237E), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.W700, color = Color(0xFF212121))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Text(
                    text = badge,
                    fontSize = 11.sp,
                    color = Color(0xFF1A237E).copy(alpha = 0.6f),
                    fontWeight = FontWeight.W800,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF1A237E).copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
