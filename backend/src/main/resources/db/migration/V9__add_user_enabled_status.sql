-- Thêm trường trạng thái hoạt động tài khoản (mặc định hoạt động)
ALTER TABLE users ADD COLUMN is_enabled BOOLEAN DEFAULT TRUE;
