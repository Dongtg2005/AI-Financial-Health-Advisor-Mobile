# Danh Sách Các Tính Năng Đã Triển Khai (Project Progress Checklist)

Tài liệu này tổng hợp toàn bộ các phần việc, cấu trúc và API đã được xây dựng trong dự án Backend Spring Boot.

---

## 1. Cơ sở dữ liệu & Cấu hình Migration (Flyway)
- [x] **[V1__init_schema.sql](file:///d:/backend/src/main/resources/db/migration/V1__init_schema.sql)**: Thiết lập cấu trúc database ban đầu gồm 6 bảng cốt lõi (`users`, `budgets`, `transactions`, `debts`, `bank_app_detect`, `financial_score`).
- [x] **[V2__seed_test_user.sql](file:///d:/backend/src/main/resources/db/migration/V2__seed_test_user.sql)**: Chèn dữ liệu mẫu cho user test có ID `c81d4e2e-bcf2-11ed-afa1-0242ac120002`.
- [x] **[V3__add_user_auth_fields.sql](file:///d:/backend/src/main/resources/db/migration/V3__add_user_auth_fields.sql)**: Nâng cấp bảng `users` thêm các cột phục vụ xác thực (`username` và `password`) và cập nhật mật khẩu đã mã hóa BCrypt của tài khoản test (`admin` / `password`).

---

## 2. Tầng Entity (JPA Models)
Tất cả các thực thể nằm trong package `com.finance.api.entity` được ánh xạ chuẩn JPA và cấu hình tương thích với PostgreSQL:
- [x] **[User.java](file:///d:/backend/src/main/java/com/finance/api/entity/User.java)**:
  - Kế thừa và triển khai interface `UserDetails` của Spring Security.
  - Sử dụng `@ColumnTransformer` mã hóa cột `monthly_income` bằng hàm `pgp_sym_encrypt` và giải mã bằng `pgp_sym_decrypt` với secret key `'FINANCE_SECRET_KEY'`.
- [x] **[Budget.java](file:///d:/backend/src/main/java/com/finance/api/entity/Budget.java)**: Ánh xạ bảng `budgets` quản lý hạn mức chi tiêu.
- [x] **[Transaction.java](file:///d:/backend/src/main/java/com/finance/api/entity/Transaction.java)**: Ánh xạ bảng `transactions` ghi lại lịch sử giao dịch.
- [x] **[Debt.java](file:///d:/backend/src/main/java/com/finance/api/entity/Debt.java)**: Ánh xạ bảng `debts`. Sử dụng `@ColumnTransformer` để mã hóa và giải mã số dư khoản nợ (`balance`).
- [x] **[BankAppDetect.java](file:///d:/backend/src/main/java/com/finance/api/entity/BankAppDetect.java)**: Ánh xạ bảng `bank_app_detect` nhận diện SMS biến động số dư.
- [x] **[FinancialScore.java](file:///d:/backend/src/main/java/com/finance/api/entity/FinancialScore.java)**: Ánh xạ bảng `financial_score` chấm điểm tài chính.
- [x] **Các Enum nghiệp vụ**:
  - `[x]` [FinancialStage.java](file:///d:/backend/src/main/java/com/finance/api/entity/FinancialStage.java)
  - `[x]` [TransactionType.java](file:///d:/backend/src/main/java/com/finance/api/entity/TransactionType.java)
  - `[x]` [EntryMethod.java](file:///d:/backend/src/main/java/com/finance/api/entity/EntryMethod.java)
  - `[x]` [DebtMode.java](file:///d:/backend/src/main/java/com/finance/api/entity/DebtMode.java)
  - `[x]` [DebtType.java](file:///d:/backend/src/main/java/com/finance/api/entity/DebtType.java)

---

## 3. Tầng Repository (Spring Data JPA)
Các Interface Repository kế thừa `JpaRepository` nằm trong package `com.finance.api.repository`:
- [x] **[UserRepository.java](file:///d:/backend/src/main/java/com/finance/api/repository/UserRepository.java)**: Bổ sung tìm kiếm user theo `username` (`Optional<User> findByUsername(String username)`).
- [x] **[TransactionRepository.java](file:///d:/backend/src/main/java/com/finance/api/repository/TransactionRepository.java)**: Bổ sung truy vấn danh sách giao dịch theo ID người dùng và sắp xếp mới nhất lên trước (`List<Transaction> findByUserIdOrderByTransactionAtDesc(UUID userId)`).
- [x] **[BudgetRepository.java](file:///d:/backend/src/main/java/com/finance/api/repository/BudgetRepository.java)**
- [x] **[DebtRepository.java](file:///d:/backend/src/main/java/com/finance/api/repository/DebtRepository.java)**
- [x] **[BankAppDetectRepository.java](file:///d:/backend/src/main/java/com/finance/api/repository/BankAppDetectRepository.java)**
- [x] **[FinancialScoreRepository.java](file:///d:/backend/src/main/java/com/finance/api/repository/FinancialScoreRepository.java)**

---

## 4. Tầng DTO (Data Transfer Objects)
Các lớp bọc dữ liệu để tăng tính bảo mật và định dạng API chuẩn xác:
- **Request DTOs**:
  - `[x]` **[AuthRequest.java](file:///d:/backend/src/main/java/com/finance/api/dto/request/AuthRequest.java)**: Nhận thông tin đăng nhập (`username`, `password`).
  - `[x]` **[RegisterRequest.java](file:///d:/backend/src/main/java/com/finance/api/dto/request/RegisterRequest.java)**: Nhận thông tin đăng ký thành viên mới (`username`, `password`, `monthly_income`).
  - `[x]` **[TransactionRequestDTO.java](file:///d:/backend/src/main/java/com/finance/api/dto/request/TransactionRequestDTO.java)**: Nhận thông tin tạo mới giao dịch.
- **Response DTOs**:
  - `[x]` **[ApiResponse.java](file:///d:/backend/src/main/java/com/finance/api/dto/response/ApiResponse.java)**: Chuẩn hóa định dạng trả về chung của API (`status`, `message`, `data`).
  - `[x]` **[AuthResponse.java](file:///d:/backend/src/main/java/com/finance/api/dto/response/AuthResponse.java)**: Trả về chuỗi JWT token sau khi đăng nhập/đăng ký thành công.
  - `[x]` **[TransactionResponseDTO.java](file:///d:/backend/src/main/java/com/finance/api/dto/response/TransactionResponseDTO.java)**: Định dạng dữ liệu giao dịch trả về cho Client, chỉ chứa các trường an toàn (ẩn thông tin nhạy cảm của `User`).

---

## 5. Tầng Service (Nghiệp vụ)
Xử lý các logic nghiệp vụ chính của ứng dụng:
- [x] **[TransactionService.java](file:///d:/backend/src/main/java/com/finance/api/service/TransactionService.java)**:
  - Tạo mới giao dịch kiểm tra tính hợp lệ của User (sử dụng `userId` được xác thực an toàn).
  - Lấy danh sách giao dịch của User và chuyển đổi tự động sang danh sách DTO an toàn thông qua Java Stream.
- [x] **[CustomUserDetailsService.java](file:///d:/backend/src/main/java/com/finance/api/security/CustomUserDetailsService.java)**: Triển khai `UserDetailsService` để nạp thông tin người dùng từ cơ sở dữ liệu khi Spring Security thực hiện xác thực.

---

## 6. Bảo mật & JWT Authentication (Spring Security)
Tích hợp hệ thống xác thực Stateless thông qua JWT:
- [x] **[JwtUtils.java](file:///d:/backend/src/main/java/com/finance/api/security/JwtUtils.java)**: Sinh mã JWT token khi đăng nhập/đăng ký thành công, giải mã và kiểm tra tính hợp lệ của token (hạn dùng, tính đúng đắn của chữ ký).
- [x] **[JwtAuthenticationFilter.java](file:///d:/backend/src/main/java/com/finance/api/security/JwtAuthenticationFilter.java)**: Bộ lọc intercept mọi request để trích xuất JWT từ Header `Authorization: Bearer <token>`, xác thực thông tin và lưu trữ ngữ cảnh bảo mật vào `SecurityContextHolder`.
- [x] **[SecurityConfig.java](file:///d:/backend/src/main/java/com/finance/api/config/SecurityConfig.java)**:
  - Cấu hình tắt CSRF.
  - Phân quyền endpoint: Cho phép gọi công khai các API xác thực `/api/v1/auth/**`, yêu cầu xác thực đối với tất cả các API nghiệp vụ khác.
  - Cấu hình Session Stateless.
  - Cung cấp Bean mã hóa mật khẩu `BCryptPasswordEncoder`.

---

## 7. Tầng Controller (REST APIs)
Các API Endpoint cung cấp cho phía Client:
- [x] **[AuthController.java](file:///d:/backend/src/main/java/com/finance/api/controller/AuthController.java)**:
  - `POST /api/v1/auth/login`: Xác thực thông tin tài khoản và trả về mã JWT token.
  - `POST /api/v1/auth/register`: Đăng ký tài khoản người dùng mới (BCrypt mật khẩu và tự động trả về JWT token để đăng nhập luôn).
- [x] **[TransactionController.java](file:///d:/backend/src/main/java/com/finance/api/controller/TransactionController.java)**:
  - `POST /api/v1/transactions`: Thêm mới một giao dịch (yêu cầu JWT, tự động lấy `userId` từ context xác thực).
  - `GET /api/v1/transactions`: Lấy danh sách lịch sử giao dịch (yêu cầu JWT, tự động lấy `userId` từ context xác thực).

---

## 8. Xử lý Lỗi Toàn Cục (Exception Handling)
- [x] **[GlobalExceptionHandler.java](file:///d:/backend/src/main/java/com/finance/api/exception/GlobalExceptionHandler.java)**: Bắt các ngoại lệ cấp độ Runtime/General để bọc thành định dạng JSON chuẩn của `ApiResponse` với mã lỗi `500` rõ ràng.

---

## 9. Kiểm thử & Chất lượng Code
- [x] **[BackendApplicationTests.java](file:///d:/backend/src/test/java/com/finance/backend/BackendApplicationTests.java)**:
  - Kiểm thử quá trình khởi chạy ứng dụng (`contextLoads`).
  - Kiểm thử tích hợp kiểm tra độ chính xác của cơ chế mã hóa mật khẩu BCrypt đã lưu trong DB.
