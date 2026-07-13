package com.finance.api.controller;

import com.finance.api.dto.request.TransactionRequestDTO;
import com.finance.api.dto.response.ApiResponse;
import com.finance.api.dto.response.TransactionResponseDTO;
import com.finance.api.dto.response.SpendingTrendResponseDTO;
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

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = ((User) userDetails).getId();
        List<com.finance.api.entity.Transaction> transactions = transactionService.getTransactionsEntityByUser(userId);

        StringBuilder csv = new StringBuilder();
        csv.append("Id,Ngay giao dich,So tien,Danh muc,Loai,Trang thai\n");

        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (com.finance.api.entity.Transaction tx : transactions) {
            String timeStr = tx.getTransactionAt() != null ? tx.getTransactionAt().format(dtf) : "";
            csv.append(tx.getId()).append(",")
               .append(timeStr).append(",")
               .append(tx.getAmount()).append(",")
               .append(tx.getCategory() != null ? tx.getCategory() : "").append(",")
               .append(tx.getType() != null ? tx.getType().name() : "").append(",")
               .append(tx.getIsConfirmed() ? "Da xac nhan" : "Cho xac nhan").append("\n");
        }

        byte[] bytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=FinanceReport.csv");
        headers.set(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return new ResponseEntity<>(bytes, headers, org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/trends")
    public ResponseEntity<ApiResponse<SpendingTrendResponseDTO>> getSpendingTrends(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UUID userId = ((User) userDetails).getId();
        SpendingTrendResponseDTO trends = transactionService.getSpendingTrends(userId);
        
        ApiResponse<SpendingTrendResponseDTO> response = new ApiResponse<>(
                200,
                "Lấy xu hướng chi tiêu thành công",
                trends
        );
        return ResponseEntity.ok(response);
    }
}
