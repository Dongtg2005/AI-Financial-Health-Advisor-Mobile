package com.finance.api.controller;

import com.finance.api.dto.request.AuthRequest;
import com.finance.api.dto.request.RegisterRequest;
import com.finance.api.dto.response.ApiResponse;
import com.finance.api.dto.response.AuthResponse;
import com.finance.api.entity.User;
import com.finance.api.repository.UserRepository;
import com.finance.api.security.CustomUserDetailsService;
import com.finance.api.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, 
                          CustomUserDetailsService userDetailsService, 
                          JwtUtils jwtUtils, 
                          UserRepository userRepository, 
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        // Authenticate user credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Load user and generate token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtils.generateToken(userDetails);

        ApiResponse<AuthResponse> response = new ApiResponse<>(
                200,
                "Đăng nhập thành công",
                new AuthResponse(jwt)
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        // 1. Kiểm tra xem username đã tồn tại chưa
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }

        // 2. Khởi tạo thực thể User mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMonthlyIncome(request.getMonthlyIncome());
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // 3. Tạo JWT cho tài khoản vừa tạo
        final String jwt = jwtUtils.generateToken(savedUser);

        ApiResponse<AuthResponse> response = new ApiResponse<>(
                201,
                "Đăng ký tài khoản thành công",
                new AuthResponse(jwt)
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
