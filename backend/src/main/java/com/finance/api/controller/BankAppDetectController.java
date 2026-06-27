package com.finance.api.controller;

import com.finance.api.dto.request.AppDetectBatchRequest;
import com.finance.api.entity.User;
import com.finance.api.service.BankAppDetectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/detects")
public class BankAppDetectController {

    private final BankAppDetectService bankAppDetectService;

    public BankAppDetectController(BankAppDetectService bankAppDetectService) {
        this.bankAppDetectService = bankAppDetectService;
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> receiveBankAppDetectBatch(
            @AuthenticationPrincipal User currentUser,
            @RequestBody AppDetectBatchRequest request) {

        bankAppDetectService.saveDetectedBatch(currentUser, request);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Đã đồng bộ thành công danh sách log mở ứng dụng ngân hàng ngầm.");
        
        return ResponseEntity.ok(response);
    }
}
