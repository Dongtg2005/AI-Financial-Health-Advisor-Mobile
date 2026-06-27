package com.finance.api.controller;

import com.finance.api.dto.request.TransactionRequestDTO;
import com.finance.api.dto.response.ApiResponse;
import com.finance.api.dto.response.TransactionResponseDTO;
import com.finance.api.entity.User;
import com.finance.api.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.finance.api.dto.request.CashWeeklyEstimateRequest;
import com.finance.api.dto.response.TransactionSaveResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionSaveResponse> createTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TransactionRequestDTO request) {
        
        UUID userId = ((User) userDetails).getId();
        
        // Gọi Service xử lý và lấy Response hỗn hợp chuẩn hợp đồng
        TransactionSaveResponse saveResponse = transactionService.createTransaction(userId, request);

        return new ResponseEntity<>(saveResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UUID userId = ((User) userDetails).getId();
        
        // Gọi Service xử lý lấy danh sách
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByUser(userId);

        // Bọc kết quả vào ApiResponse chuẩn hóa
        ApiResponse<List<TransactionResponseDTO>> response = new ApiResponse<>(
                200, 
                "Lấy danh sách giao dịch thành công", 
                transactions
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cash-estimate")
    public ResponseEntity<Map<String, Object>> createWeeklyCashEstimate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CashWeeklyEstimateRequest request) {
        
        UUID userId = ((User) userDetails).getId();
        
        transactionService.processWeeklyCashEstimate(userId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Đã tiếp nhận ước tính cuối tuần. Hệ thống đã tự động phân bổ dòng tiền mặt.");
        
        return ResponseEntity.ok(response);
    }
}
