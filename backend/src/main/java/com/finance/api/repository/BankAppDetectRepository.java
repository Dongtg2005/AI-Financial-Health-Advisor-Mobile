package com.finance.api.repository;

import com.finance.api.entity.BankAppDetect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface BankAppDetectRepository extends JpaRepository<BankAppDetect, UUID> {
}
