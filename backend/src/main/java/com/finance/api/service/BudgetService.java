package com.finance.api.service;

import com.finance.api.dto.response.BudgetSuggestionResponse;
import com.finance.api.repository.BudgetRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public BudgetSuggestionResponse getBudgetSuggestion(BigDecimal monthlyIncome) {
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return new BudgetSuggestionResponse(BigDecimal.ZERO, "Thu nhập không hợp lệ", Map.of());
        }

        // Tổng ngân sách chi tiêu gợi ý = 60% Thu nhập
        BigDecimal suggestedTotal = monthlyIncome.multiply(new BigDecimal("0.60"));

        // Phân bổ chi tiết từng danh mục dựa trên trọng số thực tế
        BigDecimal foodBudget = monthlyIncome.multiply(new BigDecimal("0.35"));      // 35% cho ăn uống
        BigDecimal transportBudget = monthlyIncome.multiply(new BigDecimal("0.10")); // 10% cho di chuyển
        BigDecimal shoppingBudget = monthlyIncome.multiply(new BigDecimal("0.15"));  // 15% cho mua sắm linh tinh

        Map<String, BigDecimal> distribution = Map.of(
                "food", foodBudget,
                "transport", transportBudget,
                "shopping", shoppingBudget
        );

        DecimalFormat formatter = new DecimalFormat("#,###");
        String message = String.format("Dựa trên thu nhập %sđ, bạn nên dành tối đa %sđ cho sinh hoạt tháng này để đảm bảo an toàn tài chính. Bạn có muốn sử dụng mức này không?",
                formatter.format(monthlyIncome), formatter.format(suggestedTotal));

        return new BudgetSuggestionResponse(suggestedTotal, message, distribution);
    }
}
