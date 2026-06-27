package com.finance.api.repository;

import com.finance.api.entity.BankAppDetect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BankAppDetectRepository extends JpaRepository<BankAppDetect, UUID> {

    // Lấy danh sách các bản ghi chưa xử lý trong khoảng thời gian chỉ định của từng user
    @Query("SELECT b.user.id, COUNT(b) FROM BankAppDetect b " +
           "WHERE b.processed = false AND b.detectedAt BETWEEN :startTime AND :endTime " +
           "GROUP BY b.user.id")
    List<Object[]> countUnprocessedDetectsGroupByUser(
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime
    );

    // Tìm tất cả các bản ghi cụ thể để cập nhật trạng thái sau khi đã quét xong
    @Query("SELECT b FROM BankAppDetect b WHERE b.user.id = :userId AND b.processed = false AND b.detectedAt BETWEEN :startTime AND :endTime")
    List<BankAppDetect> findUnprocessedDetects(
            @Param("userId") UUID userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
