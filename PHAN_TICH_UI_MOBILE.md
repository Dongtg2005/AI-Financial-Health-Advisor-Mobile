# PHÂN TÍCH CHI TIẾT HỆ THỐNG GIAO DIỆN & THÀNH PHẦN DI ĐỘNG (MOBILE)

## DỰ ÁN: AI FINANCIAL HEALTH ADVISOR (KOTLIN / JETPACK COMPOSE)

---

## 1. Tổng quan hệ thống UI

- Ứng dụng được xây dựng bằng Android Kotlin với Jetpack Compose.
- Sử dụng `Material3` và theme tùy chỉnh qua `FinanceAppTheme`.
- Giao diện sử dụng phong cách glassmorphism, palette chủ đạo:
  - Xanh dương đậm/indigo: `Blue40`, `Blue10`, `Blue20`
  - Gradient Aurora và các nền sáng nhạt: `AuroraBlue`, `AuroraPurple`, `AuroraTeal`
  - Màu phản hồi trạng thái: `GreenSuccess`, `AmberWarning`, `RedDanger`
- App bắt đầu từ `MainActivity.kt`, gọi `FinanceAppTheme { AppNavigation() }`.

## 2. Navigation và luồng màn hình

File chính: `app/src/main/java/com/example/mobile/ui/navigation/AppNavigation.kt`

Routes chính:
- `login` -> `LoginScreen`
- `register` -> `RegisterScreen`
- `onboarding` -> `OnboardingScreen`
- `dashboard` -> `DashboardScreen`
- `transactions` -> `TransactionsScreen`
- `debts` -> `DebtScreen`
- `profile` -> `ProfileScreen`
- `settings` -> `SettingsScreen`
- `score_history` -> `ScoreHistoryScreen`

Luồng khởi tạo:
- Kiểm tra token trong `TokenManager`.
- Nếu đã đăng nhập -> `dashboard`.
- Nếu chưa -> `login`.

## 3. Theme và hệ thống màu

Files chính:
- `app/src/main/java/com/example/mobile/ui/theme/Theme.kt`
- `app/src/main/java/com/example/mobile/ui/theme/Color.kt`
- `app/src/main/java/com/example/mobile/ui/theme/Typography.kt`
- `app/src/main/java/com/example/mobile/ui/theme/Shape.kt`

Điểm chính:
- Light/Dark theme đều dùng background trong suốt để hiển thị `AuroraBackground`.
- `GlassCard` dùng container màu `GlassWhite40` và border mờ.
- Màu surface và onSurface được điều chỉnh nhẹ để phù hợp với interface glass.
- Typography tùy chỉnh dùng nhiều `W700`, `W800`, `W900` cho tiêu đề và thông tin quan trọng.

## 4. Thành phần UI chung

### 4.1 `AuroraBackground`
File: `app/src/main/java/com/example/mobile/ui/components/AuroraBackground.kt`
- Canvas vẽ nhiều vòng radial gradient tạo hiệu ứng aurora.
- Nền chủ đạo là `Color(0xFFF8F9FE)`.
- Hiệu ứng áp dụng cho tất cả màn hình chính.

### 4.2 `GlassCard`
File: `app/src/main/java/com/example/mobile/ui/components/GlassCard.kt`
- Thẻ chứa nội dung với nền bán trong suốt và viền trắng mờ.
- Sử dụng `CardDefaults.cardColors(containerColor = GlassWhite40)`.
- Padding nội bộ cố định 20dp.

### 4.3 `AppLogo`
File: `app/src/main/java/com/example/mobile/ui/components/AppLogo.kt`
- Canvas vẽ biểu tượng đơn giản theo dạng wallet/card.
- Dùng brush đơn màu `Blue40`.

### 4.4 `BottomNav`
File: `app/src/main/java/com/example/mobile/ui/components/BottomNav.kt`
- Navigation bar kiểu glass, bo tròn 28dp.
- 5 item: Tổng quan, Giao dịch, Nợ nần, Cá nhân, Cài đặt.
- Màu active/inactive đồng bộ Indigo, có indicator nội dung.

## 5. Phân tích từng màn hình

### 5.1 LoginScreen
File: `app/src/main/java/com/example/mobile/ui/auth/LoginScreen.kt`

- Layout: `Box` chứa `AuroraBackground` và `Column` chính.
- Component chính:
  - `AppLogo`
  - Tiêu đề `Finance App`
  - Text phụ `Quản lý tài chính thông minh`
  - `GlassCard` chứa 2 `OutlinedTextField` và `Button` đăng nhập.
- Hành vi:
  - `Button` chỉ enable khi email + password không trống.
  - `viewModel.login(email, password)` gọi backend.
  - Điều hướng tới `dashboard` sau khi login thành công.
- Điều hướng phụ: `TextButton` tới `register`.

### 5.2 RegisterScreen
File: `app/src/main/java/com/example/mobile/ui/auth/RegisterScreen.kt`

- Layout tương tự Login, có `verticalScroll` để phù hợp với màn hình nhỏ.
- Component nhập liệu: Họ và tên, Email, Mật khẩu, Xác nhận mật khẩu.
- Button `Đăng ký ngay` dùng `Color(0xFF1A237E)`.
- Điều kiện enable: tất cả trường có nội dung, mật khẩu trùng khớp.
- Điều hướng thành công -> `onboarding`.

### 5.3 OnboardingScreen
File: `app/src/main/java/com/example/mobile/ui/onboarding/OnboardingScreen.kt`

- Màn hình setup ban đầu cho AI:
  - Thu nhập hàng tháng
  - Ngân sách chi tiêu mong muốn
- Component custom: `OnboardingInputField`.
- Input chỉ nhận số và thêm hậu tố `VNĐ`.
- Nút `Bắt đầu hành trình` điều hướng sang `dashboard`.

### 5.4 DashboardScreen
File: `app/src/main/java/com/example/mobile/ui/dashboard/DashboardScreen.kt`

- Layout chính: `Box` + `AuroraBackground` + `Scaffold`.
- `TopAppBar` hiển thị avatar logo, lời chào, tên user.
- `BottomNav` xuất hiện ở thanh chân.
- `ExtendedFloatingActionButton` thêm giao dịch.
- Dùng `LazyColumn` với padding nội bộ để trình bày nhiều section.

Các thành phần chính:
- `GlassCard` score tổng hợp với `HealthScoreRing` và breakdown điểm:
  - Chi tiêu
  - Nợ (DTI)
  - Tiết kiệm
  - Cảnh giác
- `AlertCard` thông báo quan trọng.
- `WeeklyCashEstimateSheet` popup dự báo tiền mặt.
- `AddTransactionSheet` form thêm giao dịch.

### 5.5 TransactionsScreen
File: `app/src/main/java/com/example/mobile/ui/transactions/TransactionsScreen.kt`

- `Scaffold` chứa `TopAppBar` có chế độ tìm kiếm.
- Nút `Search` thay thế tiêu đề bằng `OutlinedTextField` khi bật.
- `FloatingActionButton` thêm giao dịch mới.
- `CategoryFilterRow` lọc danh mục.
- `LazyColumn` hiển thị giao dịch theo nhóm ngày.
- Khi không có giao dịch, hiển thị placeholder state.
- Component item giao dịch: `TransactionItem`.

### 5.6 DebtScreen
File: `app/src/main/java/com/example/mobile/ui/debts/DebtScreen.kt`

- `Scaffold` với `BottomNav` và FAB thêm nợ.
- Header hiển thị tổng dư nợ hiện tại và số khoản quá hạn / sắp đến hạn.
- Danh sách `DebtCard` hiển thị từng khoản nợ.
- Màu sắc nợ theo trạng thái:
  - Quá hạn: đỏ
  - Sắp đến hạn: vàng
  - Bình thường: nền trắng mờ
- Dialog thêm nợ: `AddDebtDialog`.

### 5.7 ProfileScreen
File: `app/src/main/java/com/example/mobile/ui/profile/ProfileScreen.kt`

- Layout gồm avatar, tên user và card ngân sách.
- Dùng `TokenManager` lấy thông tin userName và suggestedBudget.
- Mục menu chức năng: thông tin tài khoản, tài khoản liên kết, hạn mức chi tiêu.
- Mục tiện ích: cài đặt thông báo, xuất báo cáo, cài đặt sao kê.
- Nút `Đăng xuất` tách riêng.

### 5.8 ScoreHistoryScreen
File: `app/src/main/java/com/example/mobile/ui/scores/ScoreHistoryScreen.kt`

- Màn hình lịch sử điểm sức khỏe tài chính.
- `TopAppBar` có back button.
- Chart cột custom `ScoreChart` trình bày 7 tuần gần nhất.
- Danh sách `ScoreHistoryItem` hiển thị chi tiết tuần.
- Xử lý state:
  - loading
  - lỗi
  - không có dữ liệu

### 5.9 SettingsScreen
File: `app/src/main/java/com/example/mobile/ui/settings/SettingsScreen.kt`

- `Scaffold` với `TopAppBar` và `BottomNav`.
- Các item cài đặt nằm trong `GlassCard`.
- Dialog nội dung:
  - Giao diện
  - Ngôn ngữ
  - Bảo mật
  - Thông tin ứng dụng
- Các dialog hiện tại đều hiển thị tuỳ chọn tĩnh, chưa lưu persist.

## 6. Patterns / UX design

- Sử dụng `AuroraBackground` làm chủ đạo cho mọi màn hình.
- Thẻ `GlassCard` đóng vai trò layout block, dễ tùy biến.
- Tonal consistency:
  - Màu chủ đạo Indigo + gradient xanh tím
  - Accent đỏ/vàng/xanh cho trạng thái
- `Scaffold` kết hợp `TopAppBar`, `BottomNav`, `FAB` để định nghĩa cấu trúc màn hình chính.
- Chế độ tìm kiếm inline trên `TransactionsScreen` là pattern tương tác tinh tế.

## 7. Kiến trúc trạng thái và dữ liệu

- Nhiều màn hình dùng `viewModel = androidx.lifecycle.viewmodel.compose.viewModel()`.
- `collectAsState()` để chuyển flow sang state Compose.
- `DashboardScreen` gọi `viewModel.fetchDashboardData()` trong `LaunchedEffect(Unit)`.
- `ScoreHistoryScreen` gọi `viewModel.fetchScoreHistory()` trong `LaunchedEffect(Unit)`.
- `DebtScreen` gọi `viewModel.syncDebtsFromServer()` trong `LaunchedEffect(Unit)`.
- `TokenManager` quản lý dữ liệu auth/local, dùng để quyết định route khởi tạo.

## 8. Ghi chú kỹ thuật thêm

- `AppNavigation` dùng `rememberNavController` và `popUpTo` để xử lý back stack.
- `DashboardScreen` dùng `drawBehind` + `drawIntoCanvas` để tạo shadow và hiệu ứng custom cho FAB.
- `HealthScoreRing` dùng Canvas + animation `animateFloatAsState`.
- `GlassCard` có `border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))` tạo cảm giác kính.
- `OnboardingInputField` giới hạn chỉ nhận ký tự số và thêm `VNĐ` suffix.

---

## 9. Những file chính liên quan

- `MainActivity.kt`
- `ui/navigation/AppNavigation.kt`
- `ui/theme/Theme.kt`
- `ui/theme/Color.kt`
- `ui/components/AuroraBackground.kt`
- `ui/components/GlassCard.kt`
- `ui/components/BottomNav.kt`
- `ui/auth/LoginScreen.kt`
- `ui/auth/RegisterScreen.kt`
- `ui/onboarding/OnboardingScreen.kt`
- `ui/dashboard/DashboardScreen.kt`
- `ui/dashboard/components/HealthScoreRing.kt`
- `ui/dashboard/components/TransactionItem.kt`
- `ui/transactions/TransactionsScreen.kt`
- `ui/debts/DebtScreen.kt`
- `ui/debts/components/DebtCard.kt`
- `ui/debts/components/AddDebtDialog.kt`
- `ui/profile/ProfileScreen.kt`
- `ui/scores/ScoreHistoryScreen.kt`
- `ui/settings/SettingsScreen.kt`

---

## 10. Kết luận

Ứng dụng có hệ thống giao diện rõ ràng, tách biệt màn hình theo route và tái sử dụng component glass/aurora. Thiết kế hướng đến trải nghiệm tài chính hiện đại, dùng nhiều hiệu ứng gradient, glassmorphism và hiển thị trạng thái sức khỏe tài chính qua điểm số.
