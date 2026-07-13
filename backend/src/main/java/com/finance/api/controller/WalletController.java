package com.finance.api.controller;

import com.finance.api.dto.request.WalletRequestDTO;
import com.finance.api.dto.response.ApiResponse;
import com.finance.api.dto.response.WalletResponseDTO;
import com.finance.api.entity.User;
import com.finance.api.entity.Wallet;
import com.finance.api.repository.UserRepository;
import com.finance.api.repository.WalletRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletController(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WalletResponseDTO>>> getWallets(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = ((User) userDetails).getId();
        List<Wallet> wallets = walletRepository.findByUserId(userId);
        
        List<WalletResponseDTO> dtoList = wallets.stream()
                .map(w -> new WalletResponseDTO(w.getId(), w.getName(), w.getBalance()))
                .collect(Collectors.toList());

        ApiResponse<List<WalletResponseDTO>> response = new ApiResponse<>(
                200,
                "Lấy danh sách ví liên kết thành công",
                dtoList
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WalletResponseDTO>> createWallet(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody WalletRequestDTO request) {
        
        UUID userId = ((User) userDetails).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = new Wallet(user, request.getName(), request.getBalance());
        walletRepository.save(wallet);

        WalletResponseDTO responseData = new WalletResponseDTO(wallet.getId(), wallet.getName(), wallet.getBalance());

        ApiResponse<WalletResponseDTO> response = new ApiResponse<>(
                201,
                "Liên kết ví thành công",
                responseData
        );
        return ResponseEntity.status(201).body(response);
    }
}
