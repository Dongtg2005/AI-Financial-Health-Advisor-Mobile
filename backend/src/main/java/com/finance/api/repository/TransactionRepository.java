package com.finance.api.repository;

import com.finance.api.entity.Transaction;
import com.finance.api.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserIdOrderByTransactionAtDesc(UUID userId);
    List<Transaction> findByUserIdAndTransactionAtBetween(UUID userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    BigDecimal sumAmountByUserIdAndType(@Param("userId") UUID userId, @Param("type") TransactionType type);

    // Đếm tổng số giao dịch theo loại trong ngày hôm nay của User
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.transactionAt >= :startOfDay")
    long countTodayTransactionsByType(@Param("userId") UUID userId, @Param("type") TransactionType type, @Param("startOfDay") LocalDateTime startOfDay);

    default long countTodayExpenses(UUID userId, LocalDateTime startOfDay) {
        return countTodayTransactionsByType(userId, TransactionType.EXPENSE, startOfDay);
    }

    // Tính tổng số tiền giao dịch theo loại trong ngày hôm nay của User
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.transactionAt >= :startOfDay")
    BigDecimal sumTodayTransactionsAmountByType(@Param("userId") UUID userId, @Param("type") TransactionType type, @Param("startOfDay") LocalDateTime startOfDay);

    default BigDecimal sumTodayExpensesAmount(UUID userId, LocalDateTime startOfDay) {
        return sumTodayTransactionsAmountByType(userId, TransactionType.EXPENSE, startOfDay);
    }

    // Tính tổng số tiền giao dịch theo loại của User trong một khoảng thời gian
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.transactionAt BETWEEN :startDateTime AND :endDateTime")
    BigDecimal sumAmountByUserIdAndPeriodAndType(@Param("userId") UUID userId, @Param("type") TransactionType type, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    default BigDecimal sumSpentByUserIdAndPeriod(UUID userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return sumAmountByUserIdAndPeriodAndType(userId, TransactionType.EXPENSE, startDateTime, endDateTime);
    }

    // Đếm số ngày duy nhất người dùng đã xác nhận giao dịch trong khoảng thời gian
    @Query("SELECT COUNT(DISTINCT CAST(t.transactionAt AS date)) FROM Transaction t WHERE t.user.id = :userId AND t.isConfirmed = true AND t.transactionAt BETWEEN :startDateTime AND :endDateTime")
    long countDaysWithConfirmedTransactions(@Param("userId") UUID userId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
}
