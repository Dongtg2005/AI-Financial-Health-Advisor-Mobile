package com.finance.api.controller;

import com.finance.api.dto.response.ApiResponse;
import com.finance.api.dto.response.BudgetSuggestionResponse;
import com.finance.api.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/suggestion")
    public ResponseEntity<ApiResponse<BudgetSuggestionResponse>> getBudgetSuggestion(
            @RequestParam("income") BigDecimal income) {
        
        BudgetSuggestionResponse suggestion = budgetService.getBudgetSuggestion(income);
        
        ApiResponse<BudgetSuggestionResponse> response = new ApiResponse<>(
                200,
                "Tạo gợi ý ngân sách onboarding thành công",
                suggestion
        );
        
        return ResponseEntity.ok(response);
    }
}
