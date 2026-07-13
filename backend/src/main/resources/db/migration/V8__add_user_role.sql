-- Thêm cột role vào bảng users
ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'USER';

-- Gán quyền ADMIN cho tài khoản admin test (nếu chưa tồn tại thì chèn mới)
INSERT INTO users (username, password, role, financial_stage)
VALUES ('admin@gmail.com', '$2a$10$OmbZsvpencmrTNgDDTycxe5JY4TMWTvlYj4yu.zkZ6mqGwnlUwNWe', 'ADMIN', 'EMERGENCY_FUND')
ON CONFLICT (username) DO UPDATE SET role = 'ADMIN';
