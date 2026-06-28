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
}
