package com.finance.api.service;

import com.finance.api.dto.request.AppDetectBatchRequest;
import com.finance.api.entity.BankAppDetect;
import com.finance.api.entity.User;
import com.finance.api.repository.BankAppDetectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankAppDetectService {

    private final BankAppDetectRepository bankAppDetectRepository;

    public BankAppDetectService(BankAppDetectRepository bankAppDetectRepository) {
        this.bankAppDetectRepository = bankAppDetectRepository;
    }

    @Transactional
    public void saveDetectedBatch(User user, AppDetectBatchRequest request) {
        if (request.getDetects() == null || request.getDetects().isEmpty()) {
            return;
        }

        List<BankAppDetect> entities = request.getDetects().stream()
                .map(item -> {
                    BankAppDetect entity = new BankAppDetect();
                    entity.setUser(user);
                    entity.setAppPackageName(item.getAppPackageName());
                    // Nếu Mobile để trống tên, hệ thống tự động gán nhãn dự phòng từ package
                    entity.setBankName(item.getBankName() != null ? item.getBankName() : "Ứng dụng Ngân hàng");
                    entity.setDetectedAt(item.getDetectedAt());
                    entity.setProcessed(false);
                    return entity;
                })
                .collect(Collectors.toList());

        bankAppDetectRepository.saveAll(entities);
    }
}
