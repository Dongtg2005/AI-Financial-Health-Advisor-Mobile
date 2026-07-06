package com.finance.api.service;

import com.finance.api.dto.request.DebtCreateRequest;
import com.finance.api.dto.response.DebtAlertDTO;
import com.finance.api.dto.response.DebtDetailsResponse;
import com.finance.api.dto.response.DebtSummaryResponse;
import com.finance.api.entity.Debt;
import com.finance.api.entity.DebtType;
import com.finance.api.entity.Transaction;
import com.finance.api.entity.TransactionType;
import com.finance.api.entity.User;
import com.finance.api.repository.DebtRepository;
import com.finance.api.repository.UserRepository;
import com.finance.api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DebtService {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DebtService(DebtRepository debtRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public DebtSummaryResponse getDebtSummary(UUID userId) {
        // 1. Lấy ngày hiện tại múi giờ Việt Nam
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        
        // 2. Tính ranh giới thời gian cho tháng hiện tại
        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59, 999999999);

        // 3. Lấy thông tin User & thu nhập tháng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        BigDecimal monthlyIncome = user.getMonthlyIncome() != null ? user.getMonthlyIncome() : BigDecimal.ZERO;

        // 4. Lấy danh sách các khoản nợ đang hoạt động và tính tổng nợ bằng Java Stream
        List<Debt> activeDebts = debtRepository.findByUserIdAndIsActive(userId, true);
        BigDecimal totalActiveDebt = activeDebts.stream()
                .map(Debt::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Tính toán chi tiêu tháng này của user
        List<Transaction> currentMonthTransactions = transactionRepository
                .findByUserIdAndTransactionAtBetween(userId, startOfMonth, endOfMonth);
        
        BigDecimal totalExpenses = currentMonthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6. Áp dụng quy tắc "Chống lỗi hổng" (Edge Cases) cho thu nhập còn lại
        BigDecimal remainingIncome = monthlyIncome.subtract(totalExpenses);
        if (remainingIncome.compareTo(BigDecimal.ZERO) < 0) {
            remainingIncome = BigDecimal.ZERO;
        }

        // 7. Duyệt danh sách các khoản nợ và tính toán số ngày
        int overdueCount = 0;
        int upcomingCount = 0;
        List<DebtDetailsResponse> detailsList = new ArrayList<>();
        List<DebtAlertDTO> alerts = new ArrayList<>();

        for (Debt debt : activeDebts) {
            DebtDetailsResponse details = DebtDetailsResponse.fromEntity(debt, now);
            detailsList.add(details);

            // Kiểm tra trễ hạn
            if (debt.getOverdueSince() != null || debt.getDueDate().isBefore(now)) {
                overdueCount++;
            } 
            // Kiểm tra sắp đến hạn (trong vòng 7 ngày)
            else if (details.getDaysRemaining() >= 0 && details.getDaysRemaining() <= 7) {
                upcomingCount++;
                
                // Tích hợp thuật toán cảnh báo thiếu hụt tiền thông minh (WARNING)
                if (debt.getBalance().compareTo(remainingIncome) > 0) {
                    BigDecimal shortage = debt.getBalance().subtract(remainingIncome);
                    String debtTypeName = translateDebtType(debt.getType());
                    String msg = String.format("Khoản nợ %s đến hạn sau %d ngày. Dựa trên chi tiêu hiện tại, bạn có thể thiếu khoảng %sđ. Xem gợi ý xử lý ->",
                            debtTypeName, details.getDaysRemaining(), formatCurrency(shortage));
                    alerts.add(new DebtAlertDTO("WARNING", msg));
                }
            }
        }

        // Tạo cảnh báo nghiêm trọng nếu có khoản nợ nào trễ hạn (CRITICAL)
        if (overdueCount > 0) {
            alerts.add(new DebtAlertDTO(
                    "CRITICAL", 
                    String.format("Bạn đang có %d khoản nợ trễ hạn thanh toán. Thanh toán ngay để phục hồi điểm số!", overdueCount)
            ));
        }

        return new DebtSummaryResponse(totalActiveDebt, overdueCount, upcomingCount, alerts, detailsList);
    }

    public List<DebtDetailsResponse> getDebtsList(UUID userId) {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        List<Debt> debts = debtRepository.findByUserId(userId);
        return debts.stream()
                .map(d -> DebtDetailsResponse.fromEntity(d, now))
                .collect(Collectors.toList());
    }

    @Transactional
    public DebtDetailsResponse createDebt(UUID userId, DebtCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        Debt debt = new Debt(
                user,
                request.getType() != null ? request.getType() : DebtType.OTHER,
                request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO,
                request.getMinimumPayment() != null ? request.getMinimumPayment() : BigDecimal.ZERO,
                request.getDueDate() != null ? request.getDueDate() : LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusDays(30),
                null,
                true
        );
        Debt savedDebt = debtRepository.save(debt);
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        return DebtDetailsResponse.fromEntity(savedDebt, now);
    }

    // --- Helper Methods ---
    private String translateDebtType(DebtType type) {
        if (type == null) return "khác";
        switch (type) {
            case CREDIT_CARD: return "thẻ tín dụng";
            case SPAYLATER: return "SPayLater";
            case MOMO_PAYLATER: return "MoMo PayLater";
            default: return "khác";
        }
    }

    private String formatCurrency(BigDecimal amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public List<Debt> getActiveDebtsByUserId(UUID userId) {
        return debtRepository.findByUserIdAndIsActive(userId, true);
    }

    // 🔄 GHI ĐÈ GIAO TÁC: Hàm cập nhật/trả nợ bắt buộc phải ghi đè để kiểm soát @Version
    @Transactional
    public Debt saveDebt(Debt debt) {
        return debtRepository.save(debt);
    }
}
