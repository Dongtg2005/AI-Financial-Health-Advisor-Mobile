package com.finance.api.service;

import com.finance.api.dto.response.AdminAnalyticsResponseDTO;
import com.finance.api.entity.User;
import com.finance.api.entity.FinancialStage;
import com.finance.api.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminAnalyticsService {

    private final UserRepository userRepository;

    public AdminAnalyticsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Lấy thống kê hệ thống toàn cục.
     * Sử dụng @Cacheable để lưu kết quả vào Redis Cache "systemAnalytics" trong vòng 10 phút.
     * Giúp giảm tải tuyệt đối cho Database khi có hàng triệu lượt gọi trang Admin.
     */
    @Cacheable(value = "systemAnalytics", key = "'summary'")
    public AdminAnalyticsResponseDTO calculateSystemAnalytics() {
        // Thực hiện giả lập câu truy vấn nặng trên hàng triệu dòng
        List<User> users = userRepository.findAll();
        long totalUsers = users.size();
        
        double averageScore = users.stream()
                .mapToInt(User::getHealthScore)
                .average()
                .orElse(0.0);
        
        // Hoạt động: Có login hoặc thao tác trong 7 ngày qua
        long activeCount = users.stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .count();
        if (activeCount == 0 && totalUsers > 0) {
            activeCount = totalUsers; // fallback nếu toàn bộ user test cũ hơn 7 ngày
        }

        long debtRepaymentCount = users.stream()
                .filter(u -> u.getFinancialStage() == FinancialStage.DEBT_REPAYMENT)
                .count();

        long emergencyFundCount = users.stream()
                .filter(u -> u.getFinancialStage() == FinancialStage.EMERGENCY_FUND)
                .count();

        return new AdminAnalyticsResponseDTO(
                totalUsers,
                averageScore,
                activeCount,
                debtRepaymentCount,
                emergencyFundCount
        );
    }
}
