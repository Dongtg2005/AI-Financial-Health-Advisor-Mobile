package com.finance.api.service;

import com.finance.api.dto.request.TransactionRequestDTO;
import com.finance.api.dto.response.TransactionResponseDTO;
import com.finance.api.entity.Transaction;
import com.finance.api.entity.User;
import com.finance.api.repository.TransactionRepository;
import com.finance.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByUser(UUID userId) {
        // 1. Tìm danh sách Entity từ DB
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionAtDesc(userId);

        // 2. Chuyển đổi toàn bộ danh sách Entity sang DTO an toàn
        return transactions.stream()
                .map(TransactionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponseDTO createTransaction(UUID userId, TransactionRequestDTO request) {
        // 1. Tìm User trong DB
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với ID: " + userId));

        // 2. Chuyển đổi dữ liệu từ DTO sang Entity
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setTransactionAt(request.getTransactionAt());
        transaction.setIsConfirmed(true); // Nhập tay thì mặc định là đã xác nhận

        // 3. Lưu xuống Database
        Transaction savedTransaction = transactionRepository.save(transaction);

        // 4. BẢO MẬT: Chuyển Entity -> Response DTO trước khi trả về
        return TransactionResponseDTO.fromEntity(savedTransaction);
    }
}
