package com.finance.api.repository;

import com.finance.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    // Lấy mức thu nhập đã setup ở giai đoạn Onboarding của user
    @Query("SELECT COALESCE(u.monthlyIncome, 0) FROM User u WHERE u.id = :userId")
    java.math.BigDecimal getMonthlyIncomeByUserId(@Param("userId") UUID userId);
}
