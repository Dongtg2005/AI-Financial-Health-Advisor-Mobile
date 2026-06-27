package com.finance.api.repository;

import com.finance.api.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface DebtRepository extends JpaRepository<Debt, UUID> {

    List<Debt> findByUserId(UUID userId);

    List<Debt> findByUserIdAndIsActive(UUID userId, Boolean isActive);
}
