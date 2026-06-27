-- Thêm các cột phục vụ tính toán Health Score vào bảng users
ALTER TABLE users ADD COLUMN suggested_budget DECIMAL(15, 2);
ALTER TABLE users ADD COLUMN health_score INT NOT NULL DEFAULT 100;
ALTER TABLE users ADD COLUMN score_spending INT NOT NULL DEFAULT 35;
ALTER TABLE users ADD COLUMN score_debt INT NOT NULL DEFAULT 35;
ALTER TABLE users ADD COLUMN score_saving INT NOT NULL DEFAULT 20;
ALTER TABLE users ADD COLUMN score_awareness INT NOT NULL DEFAULT 10;
