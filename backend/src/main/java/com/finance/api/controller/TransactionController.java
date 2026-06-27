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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> createTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TransactionRequestDTO request) {
        
        UUID userId = ((User) userDetails).getId();
        
        // Gọi Service xử lý
        TransactionResponseDTO responseDTO = transactionService.createTransaction(userId, request);

        // Bọc kết quả vào ApiResponse chuẩn hóa
        ApiResponse<TransactionResponseDTO> response = new ApiResponse<>(
                201, 
                "Tạo giao dịch thành công", 
                responseDTO
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
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
}
