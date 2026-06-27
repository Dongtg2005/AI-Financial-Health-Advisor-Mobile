package com.finance.api.repository;

import com.finance.api.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DebtRepository extends JpaRepository<Debt, UUID> {

    List<Debt> findByUserId(UUID userId);

    List<Debt> findByUserIdAndIsActive(UUID userId, Boolean isActive);

    // Tìm các khoản nợ thẻ tín dụng sắp đến hạn trong vòng 12 ngày của tất cả user
    @Query("SELECT d FROM Debt d WHERE d.isActive = true " +
           "AND d.type = com.finance.api.entity.DebtType.CREDIT_CARD " +
           "AND d.dueDate BETWEEN :today AND :targetDate")
    List<Debt> findUpcomingCreditCardDebts(
            @Param("today") LocalDate today, 
            @Param("targetDate") LocalDate targetDate
    );

    // Đếm số khoản nợ trễ hạn thanh toán của user
    @Query("SELECT COUNT(d) FROM Debt d WHERE d.user.id = :userId AND d.isActive = true AND d.overdueSince IS NOT NULL")
    long countOverdueDebts(@Param("userId") UUID userId);

    // Đếm số khoản nợ sắp đến hạn trong tuần tới của user
    @Query("SELECT COUNT(d) FROM Debt d WHERE d.user.id = :userId AND d.isActive = true AND d.dueDate BETWEEN :startDate AND :endDate")
    long countUpcomingDebtsInWeek(@Param("userId") UUID userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
