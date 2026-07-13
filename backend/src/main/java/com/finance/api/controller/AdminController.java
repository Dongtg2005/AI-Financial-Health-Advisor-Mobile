package com.finance.api.controller;

import com.finance.api.dto.response.AdminAnalyticsResponseDTO;
import com.finance.api.dto.response.AdminUserDetailResponseDTO;
import com.finance.api.dto.response.AdminUserResponseDTO;
import com.finance.api.dto.response.ApiResponse;
import com.finance.api.entity.Transaction;
import com.finance.api.entity.TransactionType;
import com.finance.api.entity.User;
import com.finance.api.repository.TransactionRepository;
import com.finance.api.repository.UserRepository;
import com.finance.api.repository.SystemSettingRepository;
import com.finance.api.entity.SystemSetting;
import com.finance.api.service.AdminAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_SYSTEM', 'ADMIN_SUPPORT', 'ADMIN_SECURITY')")
public class AdminController {

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final AdminAnalyticsService adminAnalyticsService;

    public AdminController(UserRepository userRepository, TransactionRepository transactionRepository, SystemSettingRepository systemSettingRepository, AdminAnalyticsService adminAnalyticsService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.adminAnalyticsService = adminAnalyticsService;
    }

    private LocalDateTime toSystemDefault(LocalDateTime vnDateTime) {
        if (vnDateTime == null) return null;
        return vnDateTime.atZone(VN_ZONE).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_SUPPORT', 'ADMIN_SECURITY')")
    public ResponseEntity<ApiResponse<List<AdminUserResponseDTO>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<AdminUserResponseDTO> dtoList = users.stream()
                .map(user -> new AdminUserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getRole(),
                        user.getFinancialStage(),
                        user.getMonthlyIncome(),
                        user.getSuggestedBudget(),
                        user.getHealthScore(),
                        user.getCreatedAt(),
                        user.getIsEnabled()
                ))
                .collect(Collectors.toList());

        ApiResponse<List<AdminUserResponseDTO>> response = new ApiResponse<>(
                200,
                "Lấy danh sách người dùng thành công",
                dtoList
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/toggle-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_SECURITY')")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(@PathVariable("id") UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        user.setIsEnabled(!user.getIsEnabled());
        userRepository.save(user);

        ApiResponse<Void> response = new ApiResponse<>(
                200,
                "Thay đổi trạng thái tài khoản thành công",
                null
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_SUPPORT', 'ADMIN_SYSTEM', 'ADMIN_SECURITY')")
    public ResponseEntity<ApiResponse<AdminAnalyticsResponseDTO>> getSystemAnalytics() {
        AdminAnalyticsResponseDTO analytics = adminAnalyticsService.calculateSystemAnalytics();

        ApiResponse<AdminAnalyticsResponseDTO> response = new ApiResponse<>(
                200,
                "Lấy thông tin phân tích hệ thống thành công",
                analytics
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_SUPPORT')")
    public ResponseEntity<ApiResponse<AdminUserDetailResponseDTO>> getUserDetails(@PathVariable("id") UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Lấy toàn bộ giao dịch tháng này để tính tỷ lệ chi tiêu các danh mục
        LocalDate todayVn = LocalDate.now(VN_ZONE);
        LocalDate startOfMonthVn = todayVn.withDayOfMonth(1);
        LocalDateTime startDateTime = toSystemDefault(startOfMonthVn.atStartOfDay());
        LocalDateTime endDateTime = toSystemDefault(todayVn.atTime(java.time.LocalTime.MAX));

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionAtBetween(userId, startDateTime, endDateTime);
        
        // Lọc giao dịch chi tiêu (EXPENSE) và nhóm theo category
        List<Transaction> expenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.toList());

        BigDecimal totalExpense = expenses.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Double> categoryPercentages = new HashMap<>();
        if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
            Map<String, BigDecimal> categorySums = expenses.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getCategory() != null ? t.getCategory().toLowerCase() : "khác",
                            Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                    ));

            for (Map.Entry<String, BigDecimal> entry : categorySums.entrySet()) {
                BigDecimal pct = entry.getValue()
                        .multiply(new BigDecimal("100"))
                        .divide(totalExpense, 2, RoundingMode.HALF_UP);
                categoryPercentages.put(entry.getKey(), pct.doubleValue());
            }
        }

        AdminUserDetailResponseDTO detail = new AdminUserDetailResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getHealthScore(),
                user.getScoreSpending(),
                user.getScoreDebt(),
                user.getScoreSaving(),
                user.getScoreAwareness(),
                categoryPercentages
        );

        ApiResponse<AdminUserDetailResponseDTO> response = new ApiResponse<>(
                200,
                "Lấy thông tin chi tiết người dùng thành công",
                detail
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/send-warning")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_SUPPORT')")
    public ResponseEntity<ApiResponse<Void>> sendWarning(
            @PathVariable("id") UUID userId,
            @RequestBody Map<String, String> payload) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        String message = payload.get("message");
        user.setAdminNote(message);
        userRepository.save(user);

        ApiResponse<Void> response = new ApiResponse<>(
                200,
                "Gửi cảnh báo tài chính thành công",
                null
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/settings")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_SYSTEM')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSettings() {
        List<SystemSetting> settings = systemSettingRepository.findAll();
        Map<String, String> settingsMap = settings.stream()
                .collect(Collectors.toMap(SystemSetting::getKey, SystemSetting::getValue));

        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                200,
                "Lấy cấu hình hệ thống thành công",
                settingsMap
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_SYSTEM')")
    public ResponseEntity<ApiResponse<Void>> updateSettings(@RequestBody Map<String, String> payload) {
        for (Map.Entry<String, String> entry : payload.entrySet()) {
            SystemSetting setting = systemSettingRepository.findByKey(entry.getKey())
                    .orElseGet(() -> new SystemSetting(entry.getKey(), entry.getValue()));
            setting.setValue(entry.getValue());
            systemSettingRepository.save(setting);
        }

        ApiResponse<Void> response = new ApiResponse<>(
                200,
                "Cập nhật cấu hình hệ thống thành công",
                null
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/batch-warning")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_SUPPORT')")
    public ResponseEntity<ApiResponse<Void>> sendBatchWarning(@RequestBody Map<String, String> payload) {
        String stageStr = payload.get("stage");
        String message = payload.get("message");

        List<User> users;
        if ("ALL".equalsIgnoreCase(stageStr) || stageStr == null) {
            users = userRepository.findAll();
        } else {
            try {
                com.finance.api.entity.FinancialStage stage = com.finance.api.entity.FinancialStage.valueOf(stageStr.toUpperCase());
                users = userRepository.findAll().stream()
                        .filter(u -> u.getFinancialStage() == stage)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                users = List.of();
            }
        }

        for (User user : users) {
            user.setAdminNote(message);
            userRepository.save(user);
        }

        ApiResponse<Void> response = new ApiResponse<>(
                200,
                "Gửi cảnh báo hàng loạt thành công",
                null
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/scan-risks")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_SYSTEM')")
    public ResponseEntity<ApiResponse<Void>> scanRisks() {
        List<User> users = userRepository.findAll();
        int count = 0;
        for (User user : users) {
            // Cảnh báo rủi ro 1: Điểm sức khỏe yếu (< 55đ)
            if (user.getHealthScore() < 55) {
                user.setAdminNote("[AI Tự Động] Cảnh báo: Điểm sức khỏe của bạn đang ở mức yếu (" + user.getHealthScore() + "đ). Hãy cân đối lại ngân sách của mình.");
                userRepository.save(user);
                count++;
            }
        }

        ApiResponse<Void> response = new ApiResponse<>(
                200,
                "AI quét rủi ro hoàn tất. Đã phát hiện và phát hành " + count + " cảnh báo.",
                null
        );
        return ResponseEntity.ok(response);
    }
}
