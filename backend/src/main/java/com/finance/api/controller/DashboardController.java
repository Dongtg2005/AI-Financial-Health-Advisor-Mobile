package com.finance.api.controller;

import com.finance.api.dto.response.DashboardResponse;
import com.finance.api.dto.response.DebtSummaryResponse;
import com.finance.api.dto.response.FinancialScoreDTO;
import com.finance.api.entity.Transaction;
import com.finance.api.entity.TransactionType;
import com.finance.api.entity.User;
import com.finance.api.repository.UserRepository;
import com.finance.api.repository.TransactionRepository;
import com.finance.api.service.DebtService;
import com.finance.api.service.ScoreCalculationService;
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

import com.finance.api.dto.response.ApiResponse;
import com.finance.api.dto.response.ScoreHistoryDTO;
import com.finance.api.repository.FinancialScoreRepository;
import com.finance.api.entity.FinancialScore;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final DebtService debtService;
    private final TransactionRepository transactionRepository;
    private final ScoreCalculationService scoreCalculationService;
    private final FinancialScoreRepository financialScoreRepository;

    public DashboardController(UserRepository userRepository, 
                               DebtService debtService, 
                               TransactionRepository transactionRepository, 
                               ScoreCalculationService scoreCalculationService,
                               FinancialScoreRepository financialScoreRepository) {
        this.userRepository = userRepository;
        this.debtService = debtService;
        this.transactionRepository = transactionRepository;
        this.scoreCalculationService = scoreCalculationService;
        this.financialScoreRepository = financialScoreRepository;
    }

    @GetMapping("/score-history")
    public ResponseEntity<ApiResponse<List<ScoreHistoryDTO>>> getScoreHistory(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        UUID userId = user.getId();

        List<FinancialScore> scores = financialScoreRepository.findByUserIdOrderByWeekStartDateDesc(userId);
        List<ScoreHistoryDTO> dtos = scores.stream().map(s -> new ScoreHistoryDTO(
                s.getId().toString(),
                s.getWeekStartDate(),
                s.getHealthScore(),
                s.getSpendingScore(),
                s.getDebtScore(),
                s.getSavingScore(),
                s.getAwarenessScore(),
                s.getDebtMode() != null ? s.getDebtMode().name() : "DTI",
                s.getProgressScore() != null ? s.getProgressScore() : 0,
                s.getInsights()
        )).collect(Collectors.toList());

        ApiResponse<List<ScoreHistoryDTO>> response = new ApiResponse<>(
                200,
                "Lấy lịch sử điểm thành công",
                dtos
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> getDashboardSummary(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        UUID userId = user.getId();

        // 1. Tự động tính toán điểm sức khoẻ bằng Service core tối ưu hóa mới (Snapshot)
        FinancialScoreDTO scoreDto = scoreCalculationService.calculateFinancialScore(userId);

        // 🛡️ CHƯƠNG 3 TỐI ƯU: ĐÃ BỎ LỆNH userRepository.save(user) ĐỂ TRIỆT TIÊU LỖI 409 CONFLICT VÔ LÝ!

        // 2. Tính toán ngân sách
        BigDecimal suggestedBudget = user.getSuggestedBudget() != null ? user.getSuggestedBudget() : BigDecimal.valueOf(10000000);

        // 3. Tính toán chi tiêu thực tế tháng này của miền múi giờ Việt Nam
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
                switch (cat) {
                    case "food":
                        spentFood += amt;
                        break;
                    case "transport":
                        spentTransport += amt;
                        break;
                    case "shopping":
                        spentShopping += amt;
                        break;
                    default:
                        spentOther += amt;
                        break;
                }
            }
        }

        // 4. Lấy cảnh báo nợ từ DebtService
        DebtSummaryResponse debtSummary = debtService.getDebtSummary(userId);

        // 5. Phân bổ ngân sách danh mục khớp chuẩn tỉ lệ hiển thị của Mobile UI
        List<DashboardResponse.BudgetCategoryUiDTO> categories = new ArrayList<>();
        categories.add(new DashboardResponse.BudgetCategoryUiDTO("Ăn uống", spentFood, suggestedBudget.multiply(BigDecimal.valueOf(0.35)).longValue()));
        categories.add(new DashboardResponse.BudgetCategoryUiDTO("Đi lại", spentTransport, suggestedBudget.multiply(BigDecimal.valueOf(0.10)).longValue()));
        categories.add(new DashboardResponse.BudgetCategoryUiDTO("Mua sắm", spentShopping, suggestedBudget.multiply(BigDecimal.valueOf(0.15)).longValue()));
        categories.add(new DashboardResponse.BudgetCategoryUiDTO("Khác", spentOther, suggestedBudget.multiply(BigDecimal.valueOf(0.40)).longValue()));

        // 6. ĐÓNG GÓI DỮ LIỆU: Đảm bảo khớp 100% với DashboardResponse phía Android Studio
        DashboardResponse.DashboardData data = new DashboardResponse.DashboardData(
                user.getUsername(),
                scoreDto.getTotalScore(),
                scoreDto.getSpendingScore(),
                scoreDto.getDebtOrReserveScore(),
                scoreDto.getSavingScore(),
                scoreDto.getAwarenessScore(),
                totalSpent,
                suggestedBudget.longValue(),
                debtSummary.getAlerts(),
                categories,
                user.getAdminNote()
        );

        return ResponseEntity.ok(new DashboardResponse(200, "Lấy thông tin dashboard thành công", data));
    }
}
