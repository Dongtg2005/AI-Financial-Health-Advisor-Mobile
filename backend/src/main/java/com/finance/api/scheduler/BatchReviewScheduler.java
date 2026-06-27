package com.finance.api.scheduler;

import com.finance.api.entity.BankAppDetect;
import com.finance.api.repository.BankAppDetectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Component
public class BatchReviewScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchReviewScheduler.class);
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    
    private final BankAppDetectRepository bankAppDetectRepository;

    public BatchReviewScheduler(BankAppDetectRepository bankAppDetectRepository) {
        this.bankAppDetectRepository = bankAppDetectRepository;
    }

    /**
     * PHIÊN 1 — 13h Trưa: Gom giao dịch từ 21h00 tối hôm trước đến 13h00 trưa hôm nay (liên tục gối đầu)
     */
    @Scheduled(cron = "0 0 13 * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void processMidDayBatchReview() {
        log.info("🚀 Bắt đầu tiến trình gom Batch Review phiên 13h trưa...");
        
        LocalDate today = LocalDate.now(VN_ZONE);
        LocalDateTime startTime = today.minusDays(1).atTime(21, 0, 0); // 21:00 tối qua
        LocalDateTime endTime = today.atTime(13, 0, 0); // 13:00 trưa nay
        
        executeBatchProcessing(startTime, endTime, "Sáng nay");
    }

    /**
     * PHIÊN 2 — 21h Tối: Gom giao dịch từ 13h00 trưa đến 21h00 tối hôm nay (liên tục gối đầu)
     */
    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void processNightBatchReview() {
        log.info("🚀 Bắt đầu tiến trình gom Batch Review phiên 21h tối...");
        
        LocalDate today = LocalDate.now(VN_ZONE);
        LocalDateTime startTime = today.atTime(13, 0, 0); // 13:00 trưa nay
        LocalDateTime endTime = today.atTime(21, 0, 0); // 21:00 tối nay
        
        executeBatchProcessing(startTime, endTime, "Chiều tối nay");
    }

    /**
     * TẦNG 3: SAFETY NET — 8h Sáng Hôm Sau
     * Chỉ trigger nếu người dùng có hành vi mở app ngân hàng phát sinh sau 21h đêm qua đến 8h sáng nay.
     */
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void processSafetyNetMorningReview() {
        log.info("🚀 Bắt đầu tiến trình kiểm tra Safety Net phiên 8h sáng (Giờ VN)...");
        
        LocalDate today = LocalDate.now(VN_ZONE);
        
        // Khung giờ đêm qua: Từ 21h01 tối hôm trước (sau khi phiên 21h kết thúc) đến 07h59 sáng hôm nay
        LocalDateTime startTime = today.minusDays(1).atTime(21, 1, 0);
        LocalDateTime endTime = today.atTime(7, 59, 59);
        
        log.info("🔍 Khung giờ quét dữ liệu Safety Net: từ {} đến {}", startTime, endTime);

        // 1. Tìm các User có phát sinh hành vi mở app ngân hàng ngầm trong đêm chưa xử lý
        List<Object[]> results = bankAppDetectRepository.countUnprocessedDetectsGroupByUser(startTime, endTime);
        
        if (results.isEmpty()) {
            log.info("💤 Lưới an toàn sạch: Không có giao dịch phát sinh sau 21h đêm qua. Hệ thống giữ im lặng hoàn toàn. 🤫");
            return;
        }

        for (Object[] row : results) {
            UUID userId = (UUID) row[0];
            Long detectCount = (Long) row[1];

            if (detectCount > 0) {
                // 2. Tạo nội dung thông điệp chuẩn xác theo tone đồng hành của tài liệu gốc
                String notificationTitle = "Nhắc nhở buổi sáng ☕";
                String notificationMessage = "Tối qua bạn có thêm vài khoản tiêu dùng chưa nhập đúng không? Review nhanh một chút để bắt đầu ngày mới nhé!";
                
                log.info("🔔 [SAFETY NET TRIGGER] -> User [{}]: {} -> {}", userId, notificationTitle, notificationMessage);
                
                // Trực quan hóa luồng gửi push: notificationService.sendPushNotification(userId, notificationTitle, notificationMessage);

                // 3. Đánh dấu toàn bộ bản ghi đêm qua đã được gom xong để không bị quét lại ở phiên 13h trưa
                List<BankAppDetect> detectsToUpdate = bankAppDetectRepository.findUnprocessedDetects(userId, startTime, endTime);
                for (BankAppDetect detect : detectsToUpdate) {
                    detect.setProcessed(true);
                }
                bankAppDetectRepository.saveAll(detectsToUpdate);
            }
        }
        log.info("✓ Hoàn thành kiểm tra và xử lý lưới an toàn Safety Net.");
    }

    private void executeBatchProcessing(LocalDateTime startTime, LocalDateTime endTime, String timeLabel) {
        // 1. Tìm các User có log mở app ngân hàng ngầm chưa xử lý trong khung giờ
        List<Object[]> results = bankAppDetectRepository.countUnprocessedDetectsGroupByUser(startTime, endTime);
        
        if (results.isEmpty()) {
            log.info("💤 Không có log mở ứng dụng ngân hàng nào cần xử lý trong khung giờ từ {} đến {}.", startTime, endTime);
            return;
        }

        for (Object[] row : results) {
            UUID userId = (UUID) row[0];
            Long detectCount = (Long) row[1];

            if (detectCount > 0) {
                // 2. Tạo nội dung thông điệp chuẩn xác theo đặc tả
                String notificationTitle = "Giao dịch điện tử mới? ⚡";
                String notificationMessage = String.format("%s bạn đã mở ứng dụng ngân hàng %d lần. Review nhanh để kiểm soát dòng tiền nhé!", timeLabel, detectCount);
                
                log.info("🔔 Gửi thông báo đến User [{}]: {} -> {}", userId, notificationTitle, notificationMessage);

                // 3. Đánh dấu các bản ghi này đã được xử lý (Gom thành công vào phiên review)
                List<BankAppDetect> detectsToUpdate = bankAppDetectRepository.findUnprocessedDetects(userId, startTime, endTime);
                for (BankAppDetect detect : detectsToUpdate) {
                    detect.setProcessed(true);
                }
                bankAppDetectRepository.saveAll(detectsToUpdate);
            }
        }
        log.info("✓ Hoàn thành xử lý đợt gom phiên.");
    }
}
