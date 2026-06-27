package com.finance.api.service;

import com.finance.api.dto.response.FinancialScoreDTO;
import com.finance.api.entity.FinancialScore;
import com.finance.api.entity.DebtMode;
import com.finance.api.entity.User;
import com.finance.api.repository.UserRepository;
import com.finance.api.repository.FinancialScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ScoreSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(ScoreSchedulerService.class);
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final UserRepository userRepository;
    private final ScoreCalculationService scoreCalculationService;
    private final FinancialScoreRepository financialScoreRepository;

    public ScoreSchedulerService(UserRepository userRepository, 
                                 ScoreCalculationService scoreCalculationService, 
                                 FinancialScoreRepository financialScoreRepository) {
        this.userRepository = userRepository;
        this.scoreCalculationService = scoreCalculationService;
        this.financialScoreRepository = financialScoreRepository;
    }

    /**
     * Tự động chốt điểm Sức khỏe tài chính vào đúng 00:00:00 ngày Chủ Nhật hàng tuần (Giờ Việt Nam)
     */
    @Scheduled(cron = "0 0 0 * * SUN", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void performWeeklyScoreSnapshot() {
        log.info("--- BẮT ĐẦU TIẾN TRÌNH CHỐT ĐIỂM SỨC KHỎE TÀI CHÍNH HÀNG TUẦN ---");
        LocalDate todayInVn = LocalDate.now(VN_ZONE);

        // Lấy tất cả người dùng trong hệ thống (Ở quy mô lớn có thể tối ưu phân trang)
        List<User> allUsers = userRepository.findAll();
        log.info("Tìm thấy {} người dùng cần xử lý chốt điểm.", allUsers.size());

        int successCount = 0;
        for (User user : allUsers) {
            try {
                // Tính điểm Live hiện tại
                FinancialScoreDTO liveScore = scoreCalculationService.calculateHealthScore(user);

                // Map dữ liệu sang Entity Lịch sử khớp 100% với Schema DB hiện tại
                FinancialScore historyRecord = new FinancialScore();
                historyRecord.setUser(user);
                historyRecord.setWeekStartDate(todayInVn);
                historyRecord.setHealthScore(liveScore.getTotalScore());
                historyRecord.setSpendingScore(liveScore.getSpendingScore());
                historyRecord.setDebtScore(liveScore.getDebtOrReserveScore());
                historyRecord.setSavingScore(liveScore.getSavingScore());
                historyRecord.setAwarenessScore(liveScore.getAwarenessScore());
                
                // Ánh xạ trạng thái DTI / Emergency Fund vào Enum cột DB
                DebtMode mode = "DTI_MODE".equals(liveScore.getCurrentStage()) ? DebtMode.DTI : DebtMode.EMERGENCY_FUND;
                historyRecord.setDebtMode(mode);

                // Tính toán điểm chênh lệch (Progress Score) so với tuần trước đó
                Integer progress = 0;
                List<FinancialScore> previousScores = financialScoreRepository.findByUserIdOrderByWeekStartDateDesc(user.getId());
                if (!previousScores.isEmpty()) {
                    progress = liveScore.getTotalScore() - previousScores.get(0).getHealthScore();
                }
                historyRecord.setProgressScore(progress);

                // Đóng gói lý do phạt/thông báo thành định dạng JSON Array lưu vào cột Insights (JSONB)
                String insightsJson = "[]";
                if (liveScore.getPenaltyReason() != null) {
                    insightsJson = "[\"" + liveScore.getPenaltyReason().replace("\"", "\\\"") + "\"]";
                }
                historyRecord.setInsights(insightsJson);

                financialScoreRepository.save(historyRecord);
                successCount++;
            } catch (Exception e) {
                log.error("Lỗi khi chốt điểm tuần cho User ID: {}. Chi tiết: {}", user.getId(), e.getMessage());
            }
        }

        log.info("--- KẾT THÚC TIẾN TRÌNH: Chốt điểm thành công cho {}/{} người dùng ---", successCount, allUsers.size());
    }
}
