-- Thêm các tài khoản Admin phân quyền chuyên biệt
INSERT INTO users (username, password, role, financial_stage)
VALUES ('system_admin@gmail.com', '$2a$10$OmbZsvpencmrTNgDDTycxe5JY4TMWTvlYj4yu.zkZ6mqGwnlUwNWe', 'ADMIN_SYSTEM', 'EMERGENCY_FUND')
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, role, financial_stage)
VALUES ('support_admin@gmail.com', '$2a$10$OmbZsvpencmrTNgDDTycxe5JY4TMWTvlYj4yu.zkZ6mqGwnlUwNWe', 'ADMIN_SUPPORT', 'EMERGENCY_FUND')
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, role, financial_stage)
VALUES ('security_admin@gmail.com', '$2a$10$OmbZsvpencmrTNgDDTycxe5JY4TMWTvlYj4yu.zkZ6mqGwnlUwNWe', 'ADMIN_SECURITY', 'EMERGENCY_FUND')
ON CONFLICT (username) DO NOTHING;
