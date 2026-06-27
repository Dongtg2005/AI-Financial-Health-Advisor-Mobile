package com.finance.api.controller;

import com.finance.api.dto.response.DashboardResponse;
import com.finance.api.dto.response.DebtSummaryResponse;
import com.finance.api.entity.Transaction;
import com.finance.api.entity.TransactionType;
import com.finance.api.entity.User;
import com.finance.api.repository.UserRepository;
import com.finance.api.repository.TransactionRepository;
import com.finance.api.service.DebtService;
import com.finance.api.service.HealthScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final DebtService debtService;
    private final TransactionRepository transactionRepository;
    private final HealthScoreService healthScoreService;

    public DashboardController(UserRepository userRepository, DebtService debtService, 
                               TransactionRepository transactionRepository, HealthScoreService healthScoreService) {
        this.userRepository = userRepository;
        this.debtService = debtService;
        this.transactionRepository = transactionRepository;
        this.healthScoreService = healthScoreService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> getDashboardSummary(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        UUID userId = user.getId();

        // 1. Tự động tính toán cập nhật điểm sức khoẻ trước khi trả về
        user = healthScoreService.updateAndGetHealthScore(userId);

        // 2. Tính toán ngân sách
        BigDecimal suggestedBudget = user.getSuggestedBudget() != null ? user.getSuggestedBudget() : BigDecimal.valueOf(10000000);

        // 3. Tính toán chi tiêu thực tế tháng này
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59, 999999999);
        
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionAtBetween(userId, startOfMonth, endOfMonth);
        
        long totalSpent = 0;
        long spentFood = 0;
        long spentTransport = 0;
        long spentShopping = 0;
        long spentOther = 0;

        for (Transaction tx : transactions) {
            if (tx.getType() == TransactionType.EXPENSE) {
                long amt = tx.getAmount().longValue();
                totalSpent += amt;
                String cat = tx.getCategory() != null ? tx.getCategory().toLowerCase() : "";
                if ("food".equals(cat)) {
                    spentFood += amt;
                } else if ("transport".equals(cat)) {
                    spentTransport += amt;
                } else if ("shopping".equals(cat)) {
                    spentShopping += amt;
                } else {
                    spentOther += amt;
                }
            }
        }

        // 4. Lấy cảnh báo nợ từ DebtService
        DebtSummaryResponse debtSummary = debtService.getDebtSummary(userId);

        // 5. Phân bổ ngân sách danh mục
        List<DashboardResponse.BudgetCategoryUiDTO> categories = new ArrayList<>();
        categories.add(new DashboardResponse.BudgetCategoryUiDTO("Ăn uống", spentFood, suggestedBudget.multiply(BigDecimal.valueOf(0.35)).longValue()));
        categories.add(new DashboardResponse.BudgetCategoryUiDTO("Đi lại", spentTransport, suggestedBudget.multiply(BigDecimal.valueOf(0.10)).longValue()));
        categories.add(new DashboardResponse.BudgetCategoryUiDTO("Mua sắm", spentShopping, suggestedBudget.multiply(BigDecimal.valueOf(0.15)).longValue()));
        categories.add(new DashboardResponse.BudgetCategoryUiDTO("Khác", spentOther, suggestedBudget.multiply(BigDecimal.valueOf(0.40)).longValue()));

        DashboardResponse.DashboardData data = new DashboardResponse.DashboardData(
                user.getUsername(),
                user.getHealthScore(),
                user.getScoreSpending(),
                user.getScoreDebt(),
                user.getScoreSaving(),
                user.getScoreAwareness(),
                totalSpent,
                suggestedBudget.longValue(),
                debtSummary.getAlerts(),
                categories
        );

        return ResponseEntity.ok(new DashboardResponse(200, "Lấy thông tin dashboard thành công", data));
    }
}
