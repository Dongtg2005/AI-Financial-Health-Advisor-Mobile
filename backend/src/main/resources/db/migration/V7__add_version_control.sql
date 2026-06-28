-- Bổ sung cột version kiểm soát phiên bản vào bảng users (Khóa lạc quan)
ALTER TABLE users ADD COLUMN version INT DEFAULT 0 NOT NULL;
