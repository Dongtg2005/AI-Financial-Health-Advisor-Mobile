package com.finance.api.service;

import com.finance.api.entity.User;
import com.finance.api.repository.DebtRepository;
import com.finance.api.repository.TransactionRepository;
import com.finance.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class HealthScoreService {

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final DebtRepository debtRepository;

    public HealthScoreService(UserRepository userRepository, 
                              TransactionRepository transactionRepository, 
                              DebtRepository debtRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.debtRepository = debtRepository;
    }

    private LocalDateTime toSystemDefault(LocalDateTime vnDateTime) {
        if (vnDateTime == null) return null;
        return vnDateTime.atZone(VN_ZONE).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Transactional
    public User updateAndGetHealthScore(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal monthlyIncome = user.getMonthlyIncome() != null ? user.getMonthlyIncome() : BigDecimal.ZERO;
        BigDecimal suggestedBudget = user.getSuggestedBudget() != null ? user.getSuggestedBudget() : BigDecimal.ZERO;

        if (monthlyIncome.compareTo(BigDecimal.ZERO) == 0 || suggestedBudget.compareTo(BigDecimal.ZERO) == 0) {
            return user; // Chưa onboarding xong, giữ nguyên điểm mặc định
        }

        // 1. TÍNH ĐIỂM SPENDING (Mốc tối đa 35)
        LocalDate todayVn = LocalDate.now(VN_ZONE);
        LocalDate startOfMonthVn = todayVn.withDayOfMonth(1);
        
        LocalDateTime startDateTime = toSystemDefault(startOfMonthVn.atStartOfDay());
        LocalDateTime endDateTime = toSystemDefault(todayVn.atTime(java.time.LocalTime.MAX));
        
        // Lấy tổng chi tiêu thực tế từ đầu tháng đến nay trong DB
        BigDecimal totalSpentThisMonth = transactionRepository.sumSpentByUserIdAndPeriod(userId, startDateTime, endDateTime);
        
        int scoreSpending = 0;
        if (totalSpentThisMonth.compareTo(suggestedBudget) <= 0) {
            BigDecimal spendingRatio = totalSpentThisMonth.divide(suggestedBudget, 4, RoundingMode.HALF_UP);
            BigDecimal earnedPoints = BigDecimal.valueOf(35).multiply(BigDecimal.ONE.subtract(spendingRatio));
            scoreSpending = earnedPoints.setScale(0, RoundingMode.HALF_UP).intValue();
        }

        // 2. TÍNH ĐIỂM DEBT (Mốc tối đa 35 - Kế thừa quy tắc tử huyệt)
        long overdueCount = debtRepository.countOverdueDebts(userId);
        int scoreDebt = 35;
        if (overdueCount > 0) {
            scoreDebt = 0; // Quy tắc tử huyệt: Về 0 lập tức nếu trễ hạn nợ
        } else {
            long upcomingCount = debtRepository.countUpcomingDebtsInWeek(userId, todayVn, todayVn.plusDays(7));
            scoreDebt = Math.max(10, 35 - (int) (upcomingCount * 5));
        }

        // 3. TÍNH ĐIỂM SAVING (Mốc tối đa 20)
        BigDecimal expectedSaving = monthlyIncome.subtract(totalSpentThisMonth);
        int scoreSaving = 0;
        if (expectedSaving.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal idealSavingTarget = monthlyIncome.multiply(BigDecimal.valueOf(0.20)); // Mục tiêu tiết kiệm 20%
            if (expectedSaving.compareTo(idealSavingTarget) >= 0) {
                scoreSaving = 20;
            } else {
                scoreSaving = expectedSaving.divide(idealSavingTarget, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(20))
                        .setScale(0, RoundingMode.HALF_UP).intValue();
            }
        }

        // 4. TÍNH ĐIỂM AWARENESS (Mốc tối đa 10)
        // Đếm số ngày có mở app confirm giao dịch trong 5 ngày qua
        LocalDateTime startOfAwareness = toSystemDefault(todayVn.minusDays(5).atStartOfDay());
        LocalDateTime endOfAwareness = toSystemDefault(todayVn.atTime(java.time.LocalTime.MAX));
        
        long activeDaysCount = transactionRepository.countDaysWithConfirmedTransactions(userId, startOfAwareness, endOfAwareness);
        int scoreAwareness = Math.min(10, (int) activeDaysCount * 2);

        // TỔNG HỢP LẠI ĐIỂM SỨC KHỎE TỔNG
        int finalHealthScore = scoreSpending + scoreDebt + scoreSaving + scoreAwareness;

        user.setScoreSpending(scoreSpending);
        user.setScoreDebt(scoreDebt);
        user.setScoreSaving(scoreSaving);
        user.setScoreAwareness(scoreAwareness);
        user.setHealthScore(finalHealthScore);

        return userRepository.save(user);
    }
}
