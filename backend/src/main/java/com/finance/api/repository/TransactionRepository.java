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

    // Đếm tổng số giao dịch chi tiêu trong ngày hôm nay của User
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.type = com.finance.api.entity.TransactionType.EXPENSE AND t.transactionAt >= :startOfDay")
    long countTodayExpenses(@Param("userId") UUID userId, @Param("startOfDay") LocalDateTime startOfDay);

    // Tính tổng số tiền đã chi trong ngày hôm nay của User
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = com.finance.api.entity.TransactionType.EXPENSE AND t.transactionAt >= :startOfDay")
    BigDecimal sumTodayExpensesAmount(@Param("userId") UUID userId, @Param("startOfDay") LocalDateTime startOfDay);

    // Tính tổng chi tiêu của User trong một khoảng thời gian
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = com.finance.api.entity.TransactionType.EXPENSE AND t.transactionAt BETWEEN :startDateTime AND :endDateTime")
    BigDecimal sumSpentByUserIdAndPeriod(@Param("userId") UUID userId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    // Đếm số ngày duy nhất người dùng đã xác nhận giao dịch trong khoảng thời gian
    @Query("SELECT COUNT(DISTINCT CAST(t.transactionAt AS date)) FROM Transaction t WHERE t.user.id = :userId AND t.isConfirmed = true AND t.transactionAt BETWEEN :startDateTime AND :endDateTime")
    long countDaysWithConfirmedTransactions(@Param("userId") UUID userId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
}
