-- Thêm trường ghi chú cảnh báo của Admin đối với User
ALTER TABLE users ADD COLUMN admin_note VARCHAR(500) DEFAULT NULL;

-- Tạo bảng cấu hình các thông số hệ thống và thuật toán AI tài chính
CREATE TABLE system_settings (
    id BIGSERIAL PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value VARCHAR(100) NOT NULL
);

-- Chèn các cấu hình mặc định ban đầu
INSERT INTO system_settings (setting_key, setting_value) VALUES ('SAVINGS_RATIO_TARGET', '20');
INSERT INTO system_settings (setting_key, setting_value) VALUES ('EMERGENCY_FUND_MULTIPLIER', '6');
