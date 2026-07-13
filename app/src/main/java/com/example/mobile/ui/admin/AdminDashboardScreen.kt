package com.example.mobile.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.mobile.data.network.AdminUserDTO
import com.example.mobile.data.network.AdminUserDetailDTO
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.GlassCard
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLogoutClick: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    val role = uiState.currentAdminRole

    // RBAC Permission Flags
    val canViewUsers = role in listOf("ADMIN", "ADMIN_SUPPORT", "ADMIN_SECURITY")
    val canManageWarnings = role in listOf("ADMIN", "ADMIN_SUPPORT")
    val canToggleStatus = role in listOf("ADMIN", "ADMIN_SECURITY")
    val canManageSettings = role in listOf("ADMIN", "ADMIN_SYSTEM")
    val canRunScan = role in listOf("ADMIN", "ADMIN_SYSTEM")

    // Filtered & Sorted Users List
    val filteredUsers = remember(uiState.users, uiState.searchQuery, uiState.sortOrder) {
        uiState.users.filter {
            it.username.contains(uiState.searchQuery, ignoreCase = true)
        }.sortedWith { u1, u2 ->
            when (uiState.sortOrder) {
                "SCORE_ASC" -> u1.healthScore.compareTo(u2.healthScore)
                "SCORE_DESC" -> u2.healthScore.compareTo(u1.healthScore)
                else -> u2.createdAt.compareTo(u1.createdAt) // DATE_DESC
            }
        }
    }

    // High risk users (Health Score < 60)
    val highRiskUsers = remember(uiState.users) {
        uiState.users.filter { it.healthScore < 60 }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Bảng Quản Trị AI",
                            fontWeight = FontWeight.W900,
                            color = Color(0xFF1A237E),
                            fontSize = 20.sp
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        IconButton(onClick = { viewModel.refreshDashboard() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Tải lại danh sách",
                                tint = Color(0xFF1A237E)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                // Header Panel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Quản lý hệ thống",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.W800,
                            color = Color(0xFF1A237E)
                        )
                        Text(
                            text = "Vai trò: $role (Phân quyền bảo mật RBAC)",
                            fontSize = 12.sp,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Logout Button
                    Button(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F).copy(alpha = 0.1f),
                            contentColor = Color(0xFFD32F2F)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Thoát", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // System Analytics Panel (Only if System or Support or Full Admin)
                if (canViewUsers || canManageSettings) {
                    uiState.analytics?.let { analytics ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Total Users Card
                            Box(modifier = Modifier.weight(1f)) {
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF1A237E).copy(alpha = 0.08f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Group,
                                                contentDescription = null,
                                                tint = Color(0xFF1A237E),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Column {
                                            Text("Tổng User", fontSize = 10.sp, color = Color(0xFF757575), maxLines = 1)
                                            Text(
                                                "${analytics.totalUsers}",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.W800,
                                                color = Color(0xFF1A237E),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }

                            // Avg Health Score Card
                            Box(modifier = Modifier.weight(1.1f)) {
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF4CAF50).copy(alpha = 0.08f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                                contentDescription = null,
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Column {
                                            Text("Điểm TB", fontSize = 10.sp, color = Color(0xFF757575), maxLines = 1)
                                            Text(
                                                "${"%.1f".format(analytics.averageHealthScore)}đ",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.W800,
                                                color = Color(0xFF1A237E),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }

                            // Stages ratio card
                            Box(modifier = Modifier.weight(1.2f)) {
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFFB300).copy(alpha = 0.08f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = Color(0xFFFFB300),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Column {
                                            Text("Nợ / Dự Phòng", fontSize = 10.sp, color = Color(0xFF757575), maxLines = 1)
                                            Text(
                                                "${analytics.debtRepaymentCount} / ${analytics.emergencyFundCount}",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.W800,
                                                color = Color(0xFF1A237E),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB NAVIGATION
                TabRow(
                    selectedTabIndex = uiState.currentTab,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF1A237E),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Tab(
                        selected = uiState.currentTab == 0,
                        onClick = { viewModel.updateTab(0) },
                        text = { Text("Người dùng", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = uiState.currentTab == 1,
                        onClick = { viewModel.updateTab(1) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Cảnh báo rủi ro", fontWeight = FontWeight.Bold)
                                if (highRiskUsers.isNotEmpty() && canManageWarnings) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE53935)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${highRiskUsers.size}",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    )
                    Tab(
                        selected = uiState.currentTab == 2,
                        onClick = { viewModel.updateTab(2) },
                        text = { Text("Cấu hình AI", fontWeight = FontWeight.Bold) }
                    )
                }

                if (uiState.isLoading && uiState.users.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1A237E))
                    }
                } else if (uiState.errorMessage != null) {
                    Snackbar(
                        modifier = Modifier.padding(vertical = 8.dp),
                        action = {
                            TextButton(onClick = { viewModel.refreshDashboard() }) {
                                Text("Thử lại", color = Color.White)
                            }
                        }
                    ) {
                        Text(text = uiState.errorMessage)
                    }
                }

                // TAB CONTENT
                when (uiState.currentTab) {
                    0 -> { // Users Tab
                        if (canViewUsers) {
                            UserListTabContent(
                                searchQuery = uiState.searchQuery,
                                onSearchChange = { viewModel.updateSearchQuery(it) },
                                sortOrder = uiState.sortOrder,
                                onSortChange = { viewModel.updateSortOrder(it) },
                                users = filteredUsers,
                                canViewDetails = canManageWarnings,
                                canToggleStatus = canToggleStatus,
                                onToggleStatus = { viewModel.toggleUserStatus(it.id) },
                                onDetailClick = { viewModel.fetchUserDetails(it.id) }
                            )
                        } else {
                            PermissionDeniedCard(requiredRole = "Support/Security Admin", currentRole = role)
                        }
                    }
                    1 -> { // Risk Alerts Tab
                        if (canManageWarnings || canRunScan) {
                            RiskAlertsTabContent(
                                currentRole = role,
                                highRiskUsers = if (canManageWarnings) highRiskUsers else emptyList(),
                                onSendWarning = { userId, msg, callback -> 
                                    viewModel.sendWarningMessage(userId, msg, callback) 
                                },
                                onRunScan = { callback ->
                                    viewModel.runRiskScan(callback)
                                },
                                onSendBatchWarning = { stage, msg, callback ->
                                    viewModel.sendBatchWarning(stage, msg, callback)
                                }
                            )
                        } else {
                            PermissionDeniedCard(requiredRole = "Support Admin/AI System Admin", currentRole = role)
                        }
                    }
                    2 -> { // Settings Tab
                        if (canManageSettings) {
                            SettingsTabContent(
                                settings = uiState.settings,
                                isSaving = uiState.isSettingsSaving,
                                saveSuccess = uiState.settingsSaveSuccess,
                                onSave = { viewModel.saveSystemSettings(it) }
                            )
                        } else {
                            PermissionDeniedCard(requiredRole = "AI System Admin", currentRole = role)
                        }
                    }
                }
            }
        }

        // Privacy-Preserving User Details Dialog
        if (uiState.showDetailDialog && uiState.selectedUserDetail != null) {
            PrivacyDetailDialog(
                userDetail = uiState.selectedUserDetail,
                onDismiss = { viewModel.dismissDetailDialog() }
            )
        }
    }
}

@Composable
fun PermissionDeniedCard(requiredRole: String, currentRole: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE).copy(alpha = 0.8f)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(36.dp))
                Text("Quyền truy cập bị từ chối", fontWeight = FontWeight.W900, color = Color(0xFFC62828), fontSize = 16.sp)
                Text(
                    text = "Quyền hạn tài khoản của bạn (Vai trò: $currentRole) không được phép truy cập tab này. Chức năng yêu cầu quyền: $requiredRole.",
                    color = Color(0xFF7F1D1D),
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun UserListTabContent(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    sortOrder: String,
    onSortChange: (String) -> Unit,
    users: List<AdminUserDTO>,
    canViewDetails: Boolean,
    canToggleStatus: Boolean,
    onToggleStatus: (AdminUserDTO) -> Unit,
    onDetailClick: (AdminUserDTO) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Search and Sort controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Tìm kiếm username...", fontSize = 13.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier
                    .weight(1.5f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1A237E),
                    unfocusedBorderColor = Color(0xFF1A237E).copy(alpha = 0.2f)
                ),
                singleLine = true
            )

            // Simple Sort Button Dropdown
            var showSortMenu by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1A237E)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (sortOrder) {
                            "SCORE_ASC" -> "Điểm thấp ↑"
                            "SCORE_DESC" -> "Điểm cao ↓"
                            else -> "Mới nhất"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Đăng ký mới nhất") },
                        onClick = {
                            onSortChange("DATE_DESC")
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Điểm tài chính: Thấp -> Cao") },
                        onClick = {
                            onSortChange("SCORE_ASC")
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Điểm tài chính: Cao -> Thấp") },
                        onClick = {
                            onSortChange("SCORE_DESC")
                            showSortMenu = false
                        }
                    )
                }
            }
        }

        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy người dùng phù hợp", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(users) { user ->
                    UserAdminCard(
                        user = user,
                        canViewDetails = canViewDetails,
                        canToggleStatus = canToggleStatus,
                        onToggleStatus = { onToggleStatus(user) },
                        onDetailClick = { onDetailClick(user) }
                    )
                }
            }
        }
    }
}

@Composable
fun RiskAlertsTabContent(
    currentRole: String,
    highRiskUsers: List<AdminUserDTO>,
    onSendWarning: (String, String, () -> Unit) -> Unit,
    onRunScan: ((String) -> Unit) -> Unit,
    onSendBatchWarning: (String, String, () -> Unit) -> Unit
) {
    var showScanResultDialog by remember { mutableStateOf(false) }
    var scanResultMessage by remember { mutableStateOf("") }
    
    // Batch broadcast state
    var broadcastMessage by remember { mutableStateOf("") }
    var selectedStage by remember { mutableStateOf("ALL") }
    var showStageMenu by remember { mutableStateOf(false) }

    val canManageWarnings = currentRole in listOf("ADMIN", "ADMIN_SUPPORT")
    val canRunScan = currentRole in listOf("ADMIN", "ADMIN_SYSTEM")

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // AI Risk Scan Button (System role or Admin only)
        if (canRunScan) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E).copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A237E).copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1.5f)) {
                            Text("AI Tự Động Quét Rủi Ro", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1A237E))
                            Text("Chạy quét và tự động tạo tin cảnh báo gửi cho tài khoản có điểm yếu.", fontSize = 11.sp, color = Color.Gray)
                        }
                        Button(
                            onClick = {
                                onRunScan { msg ->
                                    scanResultMessage = msg
                                    showScanResultDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Quét AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Batch Broadcast Warn panel (Support role or Admin only)
        if (canManageWarnings) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Phát Sóng Cảnh Báo Hàng Loạt", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A237E))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Giai đoạn tài chính:", fontSize = 12.sp, color = Color.Gray)
                            Box {
                                OutlinedButton(
                                    onClick = { showStageMenu = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = when (selectedStage) {
                                            "DEBT_REPAYMENT" -> "Đang Trả Nợ"
                                            "EMERGENCY_FUND" -> "Quỹ Dự Phòng"
                                            else -> "Tất cả người dùng"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                DropdownMenu(expanded = showStageMenu, onDismissRequest = { showStageMenu = false }) {
                                    DropdownMenuItem(text = { Text("Tất cả người dùng") }, onClick = { selectedStage = "ALL"; showStageMenu = false })
                                    DropdownMenuItem(text = { Text("Đang Trả Nợ") }, onClick = { selectedStage = "DEBT_REPAYMENT"; showStageMenu = false })
                                    DropdownMenuItem(text = { Text("Quỹ Dự Phòng") }, onClick = { selectedStage = "EMERGENCY_FUND"; showStageMenu = false })
                                }
                            }
                        }

                        OutlinedTextField(
                            value = broadcastMessage,
                            onValueChange = { broadcastMessage = it },
                            placeholder = { Text("Nhập tin nhắn phát sóng hàng loạt...", fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = {
                                if (broadcastMessage.isNotBlank()) {
                                    onSendBatchWarning(selectedStage, broadcastMessage) {
                                        broadcastMessage = ""
                                    }
                                }
                            },
                            enabled = broadcastMessage.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E)),
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gửi loạt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Show standard warning alert instructions
        if (canManageWarnings) {
            item {
                Text(
                    text = "Người dùng có điểm sức khỏe ở mức yếu (< 60đ).",
                    fontSize = 12.sp,
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            if (highRiskUsers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Chưa có tài khoản có nguy cơ tài chính cao.", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                items(highRiskUsers) { user ->
                    var warningMessage by remember(user.id) { mutableStateOf("") }

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFD32F2F).copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = user.username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = "Điểm hiện tại: ${user.healthScore}đ", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFFEBEE))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Yếu",
                                        color = Color(0xFFC62828),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = warningMessage,
                                onValueChange = { warningMessage = it },
                                placeholder = { Text("Nhập tin nhắn nhắc nhở/cảnh báo...", fontSize = 13.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD32F2F),
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (warningMessage.isNotBlank()) {
                                        onSendWarning(user.id, warningMessage) {
                                            warningMessage = ""
                                        }
                                    }
                                },
                                enabled = warningMessage.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD32F2F),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.align(Alignment.End),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gửi cảnh báo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showScanResultDialog) {
        AlertDialog(
            onDismissRequest = { showScanResultDialog = false },
            title = { Text("AI Quét Hoàn Tất", fontWeight = FontWeight.Bold) },
            text = { Text(scanResultMessage) },
            confirmButton = {
                TextButton(onClick = { showScanResultDialog = false }) {
                    Text("Đồng ý", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun SettingsTabContent(
    settings: Map<String, String>,
    isSaving: Boolean,
    saveSuccess: Boolean,
    onSave: (Map<String, String>) -> Unit
) {
    var savingRatio by remember(settings) {
        mutableStateOf(settings["SAVINGS_RATIO_TARGET"] ?: "20")
    }
    var emergencyMultiplier by remember(settings) {
        mutableStateOf(settings["EMERGENCY_FUND_MULTIPLIER"] ?: "6")
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Cấu hình thuật toán AI & Điểm số tài chính toàn cục",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
        )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Settings 1
                Column {
                    Text(
                        text = "Tỷ lệ tích lũy mục tiêu (%)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF424242)
                    )
                    Text(
                        text = "Tỷ lệ thu nhập cần thiết dùng để tích lũy/tiết kiệm hàng tháng. Mặc định là 20%.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = savingRatio,
                        onValueChange = { savingRatio = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Settings 2
                Column {
                    Text(
                        text = "Số tháng Quỹ khẩn cấp cần thiết",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF424242)
                    )
                    Text(
                        text = "Mục tiêu số tháng thu nhập cần tích trữ trong quỹ khẩn cấp để đảm bảo an toàn. Mặc định là 6 tháng.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = emergencyMultiplier,
                        onValueChange = { emergencyMultiplier = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onSave(
                            mapOf(
                                "SAVINGS_RATIO_TARGET" to savingRatio,
                                "EMERGENCY_FUND_MULTIPLIER" to emergencyMultiplier
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lưu cấu hình", fontWeight = FontWeight.Bold)
                    }
                }

                if (saveSuccess) {
                    Text(
                        text = "Cập nhật cấu hình AI thành công!",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun UserAdminCard(
    user: AdminUserDTO,
    canViewDetails: Boolean,
    canToggleStatus: Boolean,
    onToggleStatus: () -> Unit,
    onDetailClick: () -> Unit
) {
    val decimalFormat = remember { DecimalFormat("#,###") }
    val formattedIncome = decimalFormat.format(user.monthlyIncome ?: java.math.BigDecimal.ZERO) + "đ"
    val formattedBudget = decimalFormat.format(user.suggestedBudget ?: java.math.BigDecimal.ZERO) + "đ"

    val scoreColor = when {
        user.healthScore >= 80 -> Color(0xFF4CAF50)
        user.healthScore >= 50 -> Color(0xFFFFB300)
        else -> Color(0xFFE53935)
    }

    val scoreBg = when {
        user.healthScore >= 80 -> Color(0xFFE8F5E9)
        user.healthScore >= 50 -> Color(0xFFFFF8E1)
        else -> Color(0xFFFFEBEE)
    }

    val stageLabel = when (user.financialStage) {
        "DEBT_REPAYMENT" -> "Trả nợ"
        "EMERGENCY_FUND" -> "Quỹ dự phòng"
        else -> "Khác"
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: User Info & Health Score Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1A237E).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (user.isEnabled) Icons.Default.Person else Icons.Default.Lock,
                            contentDescription = "User Avatar",
                            tint = if (user.isEnabled) Color(0xFF1A237E) else Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.username,
                                fontWeight = FontWeight.W800,
                                fontSize = 15.sp,
                                color = if (user.isEnabled) Color(0xFF212121) else Color(0xFF757575)
                            )
                        }
                        Text(
                            text = "Ngày tạo: ${user.createdAt.substringBefore("T")}",
                            fontSize = 11.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

                // Health Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(scoreBg)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Score: ${user.healthScore}",
                        color = scoreColor,
                        fontWeight = FontWeight.W900,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF1A237E).copy(alpha = 0.08f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Body Info Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Thu nhập tháng",
                        fontSize = 11.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = formattedIncome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF212121),
                        maxLines = 1
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ngân sách tối đa",
                        fontSize = 11.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = formattedBudget,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF212121),
                        maxLines = 1
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Giai đoạn tài chính",
                        fontSize = 11.sp,
                        color = Color(0xFF757575)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1A237E).copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stageLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1A237E),
                            maxLines = 1
                        )
                    }
                }
            }

            // Only render button row if they have action permissions
            if (canViewDetails || canToggleStatus) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Info Button (Support & Full Admin)
                    if (canViewDetails) {
                        OutlinedButton(
                            onClick = onDetailClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF1A237E)
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chi tiết", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Lock Toggle Button (Security & Full Admin)
                    if (canToggleStatus) {
                        Button(
                            onClick = onToggleStatus,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.isEnabled) Color(0xFFD32F2F).copy(alpha = 0.1f) else Color(0xFF4CAF50).copy(alpha = 0.1f),
                                contentColor = if (user.isEnabled) Color(0xFFD32F2F) else Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(
                                imageVector = if (user.isEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (user.isEnabled) "Khóa" else "Mở khóa",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacyDetailDialog(
    userDetail: AdminUserDetailDTO,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.92f)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Dialog Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Phân Tích Ẩn Danh",
                        fontWeight = FontWeight.W900,
                        fontSize = 18.sp,
                        color = Color(0xFF1A237E)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = Color.Gray)
                    }
                }
                Text(
                    text = userDetail.username,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 1. Health Score Factors Breakdown
                Text(
                    text = "1. Cơ cấu điểm sức khỏe (Tổng: ${userDetail.healthScore}đ)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1A237E),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val scoreFactors = listOf(
                    Triple("Chi tiêu", userDetail.scoreSpending, 35),
                    Triple("Nợ & Dự phòng", userDetail.scoreDebt, 35),
                    Triple("Tiết kiệm", userDetail.scoreSaving, 20),
                    Triple("Cảnh giác", userDetail.scoreAwareness, 10)
                )

                scoreFactors.forEach { (label, value, max) ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, fontSize = 12.sp, color = Color(0xFF424242))
                            Text("$value/$max", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { value.toFloat() / max.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF1A237E),
                            trackColor = Color(0xFF1A237E).copy(alpha = 0.1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Spending Categories Distribution (Privacy Preserving)
                Text(
                    text = "2. Phân phối chi tiêu tháng này (%)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1A237E),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (userDetail.categoryPercentages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chưa phát sinh giao dịch chi tiêu trong tháng",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    userDetail.categoryPercentages.forEach { (category, percentage) ->
                        val catLabel = when (category.lowercase()) {
                            "food" -> "Ăn uống"
                            "transport" -> "Đi lại"
                            "shopping" -> "Mua sắm"
                            "debt" -> "Trả nợ"
                            else -> "Khác"
                        }
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(catLabel, fontSize = 12.sp, color = Color(0xFF424242))
                                Text("${"%.1f".format(percentage)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { percentage.toFloat() / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF4CAF50),
                                trackColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
