package com.finance.api.service;

import com.finance.api.dto.request.CashWeeklyEstimateRequest;
import com.finance.api.dto.request.TransactionRequestDTO;
import com.finance.api.dto.response.TransactionResponseDTO;
import com.finance.api.dto.response.TransactionSaveResponse;
import com.finance.api.entity.EntryMethod;
import com.finance.api.entity.Transaction;
import com.finance.api.entity.TransactionType;
import com.finance.api.entity.User;
import com.finance.api.repository.TransactionRepository;
import com.finance.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final HealthScoreService healthScoreService;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, HealthScoreService healthScoreService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.healthScoreService = healthScoreService;
    }

    private LocalDateTime toSystemDefault(LocalDateTime vnDateTime) {
        if (vnDateTime == null) return null;
        return vnDateTime.atZone(VN_ZONE).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByUser(UUID userId) {
        // 1. Tìm danh sách Entity từ DB
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionAtDesc(userId);

        // 2. Chuyển đổi toàn bộ danh sách Entity sang DTO an toàn
        return transactions.stream()
                .map(TransactionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionSaveResponse createTransaction(UUID userId, TransactionRequestDTO request) {
        // 1. Tìm User trong DB
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với ID: " + userId));

        // 2. Lưu giao dịch thật vào Database
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setType(request.getType()); // EXPENSE hoặc INCOME
        
        // Đưa mốc thời gian VN về hệ múi giờ mặc định của JVM
        LocalDateTime txTimeVn = request.getTransactionAt() != null ? request.getTransactionAt() : LocalDateTime.now(VN_ZONE);
        transaction.setTransactionAt(toSystemDefault(txTimeVn));
        transaction.setIsConfirmed(true); // Nhập tay thì mặc định là đã xác nhận
        
        transactionRepository.save(transaction);

        // 3. Thiết lập cấu trúc Micro-insight mặc định (Ẩn)
        TransactionSaveResponse.MicroInsightDTO insight = new TransactionSaveResponse.MicroInsightDTO();
        insight.setShouldShow(false);

        // 4. Nếu là giao dịch CHI TIÊU (EXPENSE), tiến hành quét tần suất trong ngày
        if (com.finance.api.entity.TransactionType.EXPENSE.equals(request.getType())) {
            LocalDateTime startOfDayVn = LocalDate.now(VN_ZONE).atStartOfDay();
            LocalDateTime startOfDay = toSystemDefault(startOfDayVn);
            long expenseCount = transactionRepository.countTodayExpenses(userId, startOfDay);

            // Trigger NGAY LẦN NHẬP THỨ 3 theo đúng tài liệu đặc tả
            if (expenseCount == 3) {
                java.math.BigDecimal todayTotalAmount = transactionRepository.sumTodayExpensesAmount(userId, startOfDay);
                java.math.BigDecimal projectedMonthlyAmount = todayTotalAmount.multiply(java.math.BigDecimal.valueOf(30)); // Phép nhân 30 ngày

                java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
                String formattedToday = df.format(todayTotalAmount);
                String formattedProjected = df.format(projectedMonthlyAmount);

                insight.setShouldShow(true);
                insight.setType("DAILY_PROJECTION");
                insight.setTitle("Gợi ý phân tích từ Trợ lý AI 💡");
                insight.setTodayTotalAmount(todayTotalAmount.doubleValue());
                insight.setProjectedMonthlyAmount(projectedMonthlyAmount.doubleValue());
                
                // Trả chuỗi Markdown chuẩn mã hóa bôi đậm cho Mobile tự parse
                insight.setMessage(String.format(
                    "Hôm nay bạn đã chi **%sđ**. Nếu ngày nào cũng tương tự, tháng này bạn sẽ tiêu khoảng **%sđ** chỉ cho các khoản này.",
                    formattedToday, formattedProjected
                ));
                insight.setTone("NEUTRAL");
            }
        }

        // 5. Tính toán động điểm Health Score ngay khi phát sinh giao dịch
        healthScoreService.updateAndGetHealthScore(userId);

        // 6. Trả về Response hỗn hợp chuẩn hợp đồng
        TransactionSaveResponse.TransactionData data = new TransactionSaveResponse.TransactionData(
            transaction.getId(), transaction.getAmount(), transaction.getCategory()
        );
        return new TransactionSaveResponse(201, "Lưu giao dịch thành công.", data, insight);
    }

    /**
     * TẦNG 2: ƯỚC TÍNH CUỐI TUẦN - TỰ ĐỘNG PHÂN BỔ CHI TIÊU TIỀN MẶT
     */
    @Transactional
    public void processWeeklyCashEstimate(UUID userId, CashWeeklyEstimateRequest request) {
        // 1. Tìm User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với ID: " + userId));

        if (request.getTotalAmount() == null || request.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // 2. Thuật toán phân bổ: Chia tổng số tiền cho 7 ngày trong tuần
        BigDecimal dailyAmount = request.getTotalAmount().divide(BigDecimal.valueOf(7), 2, java.math.RoundingMode.HALF_UP);

        // 3. Xác định mốc thời gian Thứ Hai tuần này dựa trên múi giờ Việt Nam
        LocalDate today = LocalDate.now(VN_ZONE);
        // Trừ đi số ngày lệch để tìm về ngày Thứ Hai đầu tuần
        int dayOfWeekValue = today.getDayOfWeek().getValue(); // Thứ 2 = 1, ..., Chủ nhật = 7
        LocalDate mondayOfThisWeek = today.minusDays(dayOfWeekValue - 1);

        List<Transaction> cashTransactions = new ArrayList<>();

        // 4. Lặp 7 lần để rải đều dữ liệu từ Thứ Hai đến Chủ Nhật
        for (int i = 0; i < 7; i++) {
            LocalDate targetDate = mondayOfThisWeek.plusDays(i);
            
            Transaction cashTx = new Transaction();
            cashTx.setUser(user);
            cashTx.setType(TransactionType.EXPENSE); // Luôn là chi tiêu
            cashTx.setAmount(dailyAmount);
            cashTx.setCategory(request.getCategory());
            
            // Đưa mốc thời gian VN về hệ múi giờ mặc định của JVM để lưu vào DB
            LocalDateTime txTimeVn = targetDate.atTime(20, 0, 0);
            cashTx.setTransactionAt(toSystemDefault(txTimeVn));
            cashTx.setIsConfirmed(true); // User đã tự xác nhận ước tính
            cashTx.setConfirmedAt(toSystemDefault(LocalDateTime.now(VN_ZONE)));
            cashTx.setEntryMethod(EntryMethod.SUNDAY_ESTIMATE);
            
            cashTransactions.add(cashTx);
        }

        // 5. Sử dụng Batch Insert để lưu nhanh chóng 7 bản ghi cùng lúc vào Database
        transactionRepository.saveAll(cashTransactions);

        // 6. Tính toán động điểm Health Score ngay khi phân bổ xong
        healthScoreService.updateAndGetHealthScore(userId);
    }
}
