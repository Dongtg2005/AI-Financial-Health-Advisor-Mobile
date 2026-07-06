package com.finance.api.controller;

import com.finance.api.dto.request.DebtCreateRequest;
import com.finance.api.dto.response.ApiResponse;
import com.finance.api.dto.response.DebtDetailsResponse;
import com.finance.api.dto.response.DebtSummaryResponse;
import com.finance.api.entity.User;
import com.finance.api.repository.UserRepository;
import com.finance.api.service.DebtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/debts")
public class DebtController {

    private final DebtService debtService;
    private final UserRepository userRepository;

    public DebtController(DebtService debtService, UserRepository userRepository) {
        this.debtService = debtService;
        this.userRepository = userRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DebtSummaryResponse>> getDebtSummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        UUID userId = user.getId();
        
        DebtSummaryResponse summary = debtService.getDebtSummary(userId);

        ApiResponse<DebtSummaryResponse> response = new ApiResponse<>(
                200,
                "Lấy thông tin tổng hợp nợ thành công",
                summary
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DebtDetailsResponse>>> getDebtsList(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        UUID userId = user.getId();
        
        List<DebtDetailsResponse> debts = debtService.getDebtsList(userId);

        ApiResponse<List<DebtDetailsResponse>> response = new ApiResponse<>(
                200,
                "Lấy danh sách các khoản nợ thành công",
                debts
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DebtDetailsResponse>> createDebt(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DebtCreateRequest request) {
        
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        UUID userId = user.getId();
        
        DebtDetailsResponse createdDebt = debtService.createDebt(userId, request);

        ApiResponse<DebtDetailsResponse> response = new ApiResponse<>(
                201,
                "Tạo khoản nợ mới thành công",
                createdDebt
        );

        return ResponseEntity.status(201).body(response);
    }
}
