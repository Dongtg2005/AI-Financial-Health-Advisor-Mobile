package com.finance.api.repository;

import com.finance.api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserIdOrderByTransactionAtDesc(UUID userId);
    List<Transaction> findByUserIdAndTransactionAtBetween(UUID userId, LocalDateTime start, LocalDateTime end);
}
