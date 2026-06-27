package com.finance.api.repository;

import com.finance.api.entity.FinancialScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface FinancialScoreRepository extends JpaRepository<FinancialScore, UUID> {
}
