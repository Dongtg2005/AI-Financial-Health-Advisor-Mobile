package com.finance.api.scheduler;

import com.finance.api.entity.Debt;
import com.finance.api.repository.DebtRepository;
import com.finance.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.util.List;

@Component
public class SmartDebtScheduler {

    private static final Logger log = LoggerFactory.getLogger(SmartDebtScheduler.class);
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

    public SmartDebtScheduler(DebtRepository debtRepository, UserRepository userRepository) {
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
    }

    /**
     * TỰ ĐỘNG CHẠY VÀO 7H00 SÁNG MỖI NGÀY ĐỂ QUÉT RỦI RO NỢ
     */
    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional(readOnly = true)
    public void scanAndWarnSmartDebt() {
        log.info("🚀 Khởi chạy tiến trình quét nợ thông minh Safety Debt (7h sáng VN)...");

        LocalDate today = LocalDate.now(VN_ZONE);
        LocalDate targetDate = today.plusDays(12); // Quét trước mốc hạn định 12 ngày theo đặc tả

        List<Debt> upcomingDebts = debtRepository.findUpcomingCreditCardDebts(today, targetDate);

        if (upcomingDebts.isEmpty()) {
            log.info("💤 Không phát hiện khoản nợ thẻ tín dụng nào rơi vào vùng rủi ro 12 ngày.");
            return;
        }

        DecimalFormat df = new DecimalFormat("#,###");

        for (Debt debt : upcomingDebts) {
            var user = debt.getUser();
            
            // 1. Lấy thu nhập dự kiến hàng tháng của user (đã khai báo ở Onboarding)
            BigDecimal monthlyIncome = userRepository.getMonthlyIncomeByUserId(user.getId());
            
            // Giả lập logic MVP: Thu nhập dự kiến còn lại có thể phân bổ cho nợ bằng khoảng 35% thu nhập tổng
            BigDecimal expectedAvailableCash = monthlyIncome.multiply(BigDecimal.valueOf(0.35)); 

            // 2. Thuật toán phát hiện thiếu hụt: Nếu Dư nợ thẻ hiện tại (balance) > Số tiền khả dụng có thể trả
            if (debt.getBalance().compareTo(expectedAvailableCash) > 0) {
                BigDecimal shortageAmount = debt.getBalance().subtract(expectedAvailableCash);

                // 3. Tính số ngày còn lại đến hạn chính xác tuyệt đối
                long daysLeft = ChronoUnit.DAYS.between(today, debt.getDueDate());

                // 4. Đóng gói thông điệp với TONE bình tĩnh, đồng hành, hướng giải quyết
                String notificationTitle = "Cảnh báo hạn thanh toán thẻ 💳";
                String notificationMessage = String.format(
                    "Thẻ tín dụng của bạn sẽ đến hạn sau %d ngày. Dựa trên chi tiêu hiện tại, bạn có thể thiếu khoảng %sđ. Xem gợi ý xử lý ngay nhé ->",
                    daysLeft, df.format(shortageAmount)
                );

                log.info("🔔 [DEBT WARN TRIGGER] -> User [{}]: {}", user.getId(), notificationMessage);
            }
        }
        log.info("✓ Hoàn thành kiểm tra rủi ro nợ.");
    }
}
