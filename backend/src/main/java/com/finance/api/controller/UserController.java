package com.finance.api.controller;

import com.finance.api.dto.request.OnboardingRequest;
import com.finance.api.dto.response.ApiResponse;
import com.finance.api.entity.User;
import com.finance.api.repository.UserRepository;
import com.finance.api.service.HealthScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final HealthScoreService healthScoreService;

    public UserController(UserRepository userRepository, HealthScoreService healthScoreService) {
        this.userRepository = userRepository;
        this.healthScoreService = healthScoreService;
    }

    @PostMapping("/onboarding")
    public ResponseEntity<ApiResponse<Void>> submitOnboarding(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OnboardingRequest request) {
        
        UUID userId = ((User) userDetails).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setMonthlyIncome(request.getMonthlyIncome());
        user.setSuggestedBudget(request.getSuggestedBudget());
        userRepository.save(user);

        // Kích hoạt tính điểm sức khoẻ tài chính ngay khi hoàn thành onboarding
        healthScoreService.updateAndGetHealthScore(userId);

        ApiResponse<Void> response = new ApiResponse<>(
                200,
                "Cập nhật thông tin onboarding thành công",
                null
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/clear-warning")
    public ResponseEntity<ApiResponse<Void>> clearWarning(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = ((User) userDetails).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAdminNote(null);
        userRepository.save(user);

        ApiResponse<Void> response = new ApiResponse<>(
                200,
                "Đã tắt cảnh báo từ quản trị viên",
                null
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<com.finance.api.dto.response.UserProfileResponse>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = ((User) userDetails).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        com.finance.api.dto.response.UserProfileResponse data = new com.finance.api.dto.response.UserProfileResponse(
                user.getUsername(),
                user.getCreatedAt(),
                user.getSuggestedBudget(),
                user.isDailyNotifEnabled(),
                user.isAiAlertsEnabled(),
                user.getBillingCycleDay()
        );

        ApiResponse<com.finance.api.dto.response.UserProfileResponse> response = new ApiResponse<>(
                200,
                "Lấy thông tin cá nhân thành công",
                data
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<Void>> updateSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody com.finance.api.dto.request.UpdateSettingsRequest request) {
        UUID userId = ((User) userDetails).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setDailyNotifEnabled(request.isDailyNotifEnabled());
        user.setAiAlertsEnabled(request.isAiAlertsEnabled());
        user.setBillingCycleDay(request.getBillingCycleDay());
        userRepository.save(user);

        ApiResponse<Void> response = new ApiResponse<>(
                200,
                "Cập nhật cài đặt thành công",
                null
        );
        return ResponseEntity.ok(response);
    }
}
