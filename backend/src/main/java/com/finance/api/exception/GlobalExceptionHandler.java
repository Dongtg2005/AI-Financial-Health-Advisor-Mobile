package com.finance.api.exception;

import com.finance.api.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔔 XỬ LÝ CHƯƠNG 3: Bắt lỗi xung đột phiên bản dữ liệu (Optimistic Lock)
    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLockingFailure(org.springframework.orm.ObjectOptimisticLockingFailureException ex) {
        ApiResponse<Void> response = new ApiResponse<>(
                409, // Conflict status
                "Hành động tài chính bị từ chối do có giao tác khác đang xử lý đồng thời. Vui lòng thử lại sau vài giây!",
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 🔔 XỬ LÝ CHƯƠNG 4: Phục hồi khi dính hàng đợi khóa quá lâu (Deadlock/Lock Timeout)
    @ExceptionHandler(jakarta.persistence.LockTimeoutException.class)
    public ResponseEntity<ApiResponse<Void>> handleLockTimeout(jakarta.persistence.LockTimeoutException ex) {
        ApiResponse<Void> response = new ApiResponse<>(
                408, // Request Timeout
                "Hệ thống Database đang bận xử lý chuỗi giao dịch an toàn (Lock Timeout). Giao tác đã được phục hồi để tránh bế tắc!",
                null
        );
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<String> response = new ApiResponse<>(
                500,
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        ApiResponse<String> response = new ApiResponse<>(
                500,
                "Có lỗi xảy ra trong hệ thống: " + ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
