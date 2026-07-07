-- Thêm các trường phục vụ xác thực vào bảng users
ALTER TABLE users ADD COLUMN username VARCHAR(50) UNIQUE;
ALTER TABLE users ADD COLUMN password VARCHAR(100);

-- Cập nhật thông tin tài khoản cho user test ghidong@gmail.com (mật khẩu mặc định: 'password')
UPDATE users 
SET username = 'ghidong@gmail.com', 
    password = '$2a$10$OmbZsvpencmrTNgDDTycxe5JY4TMWTvlYj4yu.zkZ6mqGwnlUwNWe'
WHERE id = 'c81d4e2e-bcf2-11ed-afa1-0242ac120002';
