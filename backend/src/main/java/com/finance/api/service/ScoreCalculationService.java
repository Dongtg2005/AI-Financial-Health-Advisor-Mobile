package com.finance.api.service;

import com.finance.api.dto.response.FinancialScoreDTO;
import com.finance.api.entity.Debt;
import com.finance.api.entity.Transaction;
import com.finance.api.entity.TransactionType;
import com.finance.api.entity.User;
import com.finance.api.entity.Budget;
import com.finance.api.repository.DebtRepository;
import com.finance.api.repository.TransactionRepository;
import com.finance.api.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class ScoreCalculationService {

    private final DebtRepository debtRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public ScoreCalculationService(DebtRepository debtRepository, 
                                   TransactionRepository transactionRepository, 
                                   BudgetRepository budgetRepository) {
        this.debtRepository = debtRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    public FinancialScoreDTO calculateHealthScore(User user) {
        UUID userId = user.getId();
        LocalDate today = LocalDate.now(VN_ZONE);
        
        // Mốc thời gian cho tháng hiện tại
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);

        BigDecimal monthlyIncome = user.getMonthlyIncome() != null ? user.getMonthlyIncome() : BigDecimal.ZERO;
        List<Debt> allDebts = debtRepository.findByUserId(userId);
        List<Transaction> currentMonthTx = transactionRepository.findByUserIdAndTransactionAtBetween(userId, startOfMonth, endOfMonth);

        // 1. Tính tổng chi tiêu và tiết kiệm tháng này
        BigDecimal totalExpenses = currentMonthTx.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSavings = currentMonthTx.stream()
                .filter(t -> t.getType() == TransactionType.SAVINGS)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --- YẾU TỐ 1: KIỂM SOÁT CHI TIÊU (Max 35đ) ---
        int spendingScore = 35;
        
        // Lấy hạn mức ngân sách tháng này của User từ bảng budgets
        LocalDate startOfMonthDate = today.withDayOfMonth(1);
        BigDecimal budgetLimit = budgetRepository.findByUserIdAndMonth(userId, startOfMonthDate)
                .map(Budget::getTargetAmount)
                .orElse(BigDecimal.ZERO);
        
        if (budgetLimit.compareTo(BigDecimal.ZERO) > 0) {
            if (totalExpenses.compareTo(budgetLimit) > 0) {
                BigDecimal excessRatio = totalExpenses.subtract(budgetLimit).divide(budgetLimit, 2, RoundingMode.HALF_UP);
                if (excessRatio.compareTo(new BigDecimal("0.10")) <= 0) spendingScore = 25;
                else if (excessRatio.compareTo(new BigDecimal("0.30")) <= 0) spendingScore = 15;
                else spendingScore = 0;
            }
        }

        // --- YẾU TỐ 2: TỰ ĐỘNG CHUYỂN CHẾ ĐỘ DTI / DỰ PHÒNG + TỬ HUYỆT (Max 35đ) ---
        int debtOrReserveScore = 0;
        String currentStage = "RESERVE_MODE";
        String penaltyReason = null;

        // Quét quy tắc tử huyệt: Kiểm tra nợ trễ hạn thực tế (bao gồm cả nợ cũ lẫn active)
        long overdueCount = allDebts.stream()
                .filter(d -> d.getIsActive() && (d.getOverdueSince() != null || d.getDueDate().isBefore(today)))
                .count();

        // Kiểm tra xem có khoản nợ nào hoạt động hoặc mới sạch nợ chưa đủ 90 ngày (3 tháng) hay không
        LocalDate threeMonthsAgo = today.minusMonths(3);
        boolean hasActiveDebts = allDebts.stream().anyMatch(Debt::getIsActive);
        boolean recentlyPaidOff = user.getDebtFreeSince() != null && user.getDebtFreeSince().isAfter(threeMonthsAgo);

        if (overdueCount > 0) {
            debtOrReserveScore = 0; // TỬ HUYỆT LẬP TỨC
            currentStage = "DTI_MODE";
            penaltyReason = String.format("Phạt tử huyệt: Có %d khoản nợ đang trễ hạn thanh toán.", overdueCount);
        } else if (hasActiveDebts || recentlyPaidOff) {
            // CHẾ ĐỘ 1: TÍNH ĐIỂM DTI (Nếu đang có nợ HOẶC mới sạch nợ chưa đủ 3 tháng)
            currentStage = "DTI_MODE";
            List<Debt> currentlyActiveDebts = allDebts.stream().filter(Debt::getIsActive).toList();
            
            if (currentlyActiveDebts.isEmpty()) {
                // Đã trả hết nợ nhưng đang trong giai đoạn thử thách 3 tháng -> Cho điểm thưởng khích lệ tối đa của DTI
                debtOrReserveScore = 35;
                penaltyReason = "Trạng thái: Đang theo dõi thử thách sạch nợ 3 tháng trước khi chuyển sang Quỹ dự phòng.";
            } else {
                BigDecimal totalMonthlyDebtPayment = currentlyActiveDebts.stream()
                        .map(Debt::getMinimumPayment)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal dtiRatio = totalMonthlyDebtPayment.divide(monthlyIncome, 2, RoundingMode.HALF_UP);
                    if (dtiRatio.compareTo(new BigDecimal("0.20")) < 0) debtOrReserveScore = 35;
                    else if (dtiRatio.compareTo(new BigDecimal("0.30")) <= 0) debtOrReserveScore = 20;
                    else if (dtiRatio.compareTo(new BigDecimal("0.50")) <= 0) debtOrReserveScore = 10;
                    else debtOrReserveScore = 0;
                }
            }
        } else {
            // CHẾ ĐỘ 2: TÍNH ĐIỂM QUỸ DỰ PHÒNG KHẨN CẤP (Sạch nợ hoàn toàn > 3 tháng)
            currentStage = "RESERVE_MODE";
            
            // Tính số dư quỹ dự phòng khẩn cấp = tổng số tiền giao dịch tích lũy kiểu SAVINGS
            BigDecimal emergencyFund = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.SAVINGS);
            
            // Tính toán chi tiêu trung bình thực tế của 3 tháng gần nhất (Chuẩn luồng chi tiết)
            LocalDateTime threeMonthsAgoStart = today.minusMonths(3).withDayOfMonth(1).atStartOfDay();
            BigDecimal totalThreeMonthsExpense = transactionRepository.findByUserIdAndTransactionAtBetween(userId, threeMonthsAgoStart, endOfMonth)
                    .stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal avgExpense = totalThreeMonthsExpense.divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
            if (avgExpense.compareTo(BigDecimal.ZERO) <= 0) {
                avgExpense = budgetLimit.compareTo(BigDecimal.ZERO) > 0 ? budgetLimit : new BigDecimal("7000000");
            }

            if (avgExpense.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal monthsCovered = emergencyFund.divide(avgExpense, 1, RoundingMode.HALF_UP);
                if (monthsCovered.compareTo(new BigDecimal("6.0")) >= 0) debtOrReserveScore = 35;
                else if (monthsCovered.compareTo(new BigDecimal("3.0")) >= 0) debtOrReserveScore = 25;
                else if (monthsCovered.compareTo(new BigDecimal("1.0")) >= 0) debtOrReserveScore = 15;
                else debtOrReserveScore = 0;
            }
        }

        // --- YẾU TỐ 3: TỶ LỆ TIẾT KIỆM (Max 20đ) ---
        int savingScore = 0;
        if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0 && totalSavings.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal savingRatio = totalSavings.divide(monthlyIncome, 2, RoundingMode.HALF_UP);
            if (savingRatio.compareTo(new BigDecimal("0.20")) > 0) savingScore = 20;
            else if (savingRatio.compareTo(new BigDecimal("0.10")) >= 0) savingScore = 15;
            else savingScore = 10; // Cất 1 đồng cũng được 10 điểm để tạo thói quen
        }

        // --- YẾU TỐ 4: MỨC ĐỘ CẢNH GIÁC (Max 10đ) ---
        // Tính dựa trên các giao dịch chưa khớp lệnh của tuần hiện tại (Tránh dồn ứ phạt oan cuối tháng)
        int awarenessScore = 10;
        LocalDateTime oneWeekAgo = LocalDateTime.now(VN_ZONE).minusDays(7);
        long unconfirmedCountInWeek = currentMonthTx.stream()
                .filter(t -> t.getTransactionAt().isAfter(oneWeekAgo) && !t.getIsConfirmed())
                .count();

        if (unconfirmedCountInWeek > 5) awarenessScore = 5;
        if (unconfirmedCountInWeek > 15) awarenessScore = 0;

        int totalScore = spendingScore + debtOrReserveScore + savingScore + awarenessScore;

        return FinancialScoreDTO.builder()
                .totalScore(totalScore)
                .spendingScore(spendingScore)
                .debtOrReserveScore(debtOrReserveScore)
                .savingScore(savingScore)
                .awarenessScore(awarenessScore)
                .currentStage(currentStage)
                .penaltyReason(penaltyReason)
                .build();
    }
}
