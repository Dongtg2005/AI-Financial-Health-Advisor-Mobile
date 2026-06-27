package com.finance.api.scheduler;

import com.finance.api.entity.User;
import com.finance.api.repository.BankAppDetectRepository;
import com.finance.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Component
public class NotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final UserRepository userRepository;
    private final BankAppDetectRepository bankAppDetectRepository;

    public NotificationScheduler(UserRepository userRepository, BankAppDetectRepository bankAppDetectRepository) {
        this.userRepository = userRepository;
        this.bankAppDetectRepository = bankAppDetectRepository;
    }

    private LocalDateTime toSystemDefault(LocalDateTime vnDateTime) {
        if (vnDateTime == null) return null;
        return vnDateTime.atZone(VN_ZONE).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * TỰ ĐỘNG CHẠY VÀO 21H00 MỖI TỐI
     */
    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional(readOnly = true)
    public void dispatchNightlyNotification() {
        log.info("🚀 Khởi chạy tiến trình kiểm tra và phát hành thông báo tối (21h VN)...");

        LocalDate today = LocalDate.now(VN_ZONE);
        DayOfWeek currentDay = today.getDayOfWeek();

        // TRƯỜNG HỢP 1: Tối Chủ Nhật -> Kích hoạt Ước tính tiền mặt cuối tuần cho TOÀN BỘ USER
        if (currentDay == DayOfWeek.SUNDAY) {
            List<User> allUsers = userRepository.findAll();
            if (allUsers.isEmpty()) return;

            String title = "Tổng kết cuối tuần cùng AI 💰";
            String message = "Tuần này bạn có dùng tiền mặt không? Ước tính tổng khoảng bao nhiêu để AI cân bằng dòng tiền giúp bạn nhé!";
            
            log.info("🔄 [SUNDAY MODE] Phát hành thông báo ước tính tiền mặt diện rộng cho {} người dùng.", allUsers.size());
            
            for (User user : allUsers) {
                log.info("🔔 [PUSH] -> User ID [{}]: {} - {}", user.getId(), title, message);
            }
        } 
        // TRƯỜNG HỢP 2: Tối ngày thường (Thứ 2 đến Thứ 6/Bảy) -> Tuân thủ nghiêm ngặt "IM LẶNG HOÀN TOÀN" nếu không có giao dịch
        else {
            // Xác định khung giờ chiều tối (13h01 - 21h00) giống như BatchReviewScheduler
            LocalDateTime startTimeVn = today.atTime(13, 1, 0);
            LocalDateTime endTimeVn = today.atTime(21, 0, 0);
            
            LocalDateTime startTime = toSystemDefault(startTimeVn);
            LocalDateTime endTime = toSystemDefault(endTimeVn);

            // Tìm danh sách ID người dùng THỰC SỰ có mở app ngân hàng ngầm trong chiều tối nay
            List<Object[]> results = bankAppDetectRepository.countUnprocessedDetectsGroupByUser(startTime, endTime);

            if (results.isEmpty()) {
                log.info("💤 [SILENT MODE] Không có user nào phát sinh giao dịch điện tử chiều nay. Hệ thống giữ im lặng hoàn toàn để chống fatigue! 🤫");
                return;
            }

            String title = "Ghi chép cuối ngày thôi 📝";

            for (Object[] row : results) {
                UUID userId = (UUID) row[0];
                Long detectCount = (Long) row[1];

                if (detectCount > 0) {
                    String message = String.format("Chiều tối nay bạn đã mở ứng dụng ngân hàng %d lần. Dành ra 30 giây review nhanh để giữ điểm sức khỏe tài chính nhé! ⚡", detectCount);
                    log.info("🔔 [TARGETED PUSH] -> User ID [{}]: {} - {}", userId, title, message);
                }
            }
        }
        log.info("✓ Hoàn thành chu kỳ phát hành thông báo tối.");
    }
}
