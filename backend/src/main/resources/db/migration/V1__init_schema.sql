-- KÍCH HOẠT EXTENSION BẢO MẬT
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ĐỊNH NGHĨA CÁC KIỂU DỮ LIỆU ENUM
CREATE TYPE financial_stage_enum AS ENUM ('DEBT_REPAYMENT', 'EMERGENCY_FUND');
CREATE TYPE transaction_type_enum AS ENUM ('INCOME', 'EXPENSE', 'SAVINGS');
CREATE TYPE entry_method_enum AS ENUM ('BATCH', 'QUICK_ADD', 'SUNDAY_ESTIMATE');
CREATE TYPE debt_type_enum AS ENUM ('CREDIT_CARD', 'SPAYLATER', 'MOMO_PAYLATER', 'OTHER');
CREATE TYPE debt_mode_enum AS ENUM ('DTI', 'EMERGENCY_FUND');

-- TẠO CÁC BẢNG DỮ LIỆU (TABLES)

-- 1. Bảng users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    financial_stage financial_stage_enum DEFAULT 'DEBT_REPAYMENT',
    monthly_income BYTEA,
    debt_free_since DATE,
    notification_time VARCHAR(5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng budgets
CREATE TABLE budgets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    month DATE NOT NULL,
    target_amount DECIMAL(15, 2) NOT NULL,
    category_limits JSONB DEFAULT '{}'::jsonb,
    is_suggested BOOLEAN DEFAULT TRUE,
    CONSTRAINT unique_user_month UNIQUE(user_id, month)
);

-- 3. Bảng transactions
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type transaction_type_enum NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    category VARCHAR(50),
    entry_method entry_method_enum DEFAULT 'QUICK_ADD',
    transaction_at TIMESTAMP NOT NULL,
    is_confirmed BOOLEAN DEFAULT FALSE,
    confirmed_at TIMESTAMP
);

-- 4. Bảng debts
CREATE TABLE debts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type debt_type_enum,
    balance BYTEA NOT NULL,
    minimum_payment DECIMAL(15, 2),
    due_date DATE NOT NULL,
    overdue_since DATE,
    is_active BOOLEAN DEFAULT TRUE
);

-- 5. Bảng bank_app_detects
CREATE TABLE bank_app_detects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    app_name VARCHAR(50),
    session_id UUID,
    detected_time TIMESTAMP NOT NULL,
    is_processed BOOLEAN DEFAULT FALSE
);

-- 6. Bảng financial_scores
CREATE TABLE financial_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    week_start_date DATE NOT NULL,
    health_score INTEGER CHECK (health_score >= 0 AND health_score <= 100),
    spending_score INTEGER CHECK (spending_score >= 0 AND spending_score <= 35),
    debt_score INTEGER CHECK (debt_score >= 0 AND debt_score <= 35),
    saving_score INTEGER CHECK (saving_score >= 0 AND saving_score <= 20),
    awareness_score INTEGER CHECK (awareness_score >= 0 AND awareness_score <= 10),
    debt_mode debt_mode_enum,
    progress_score INTEGER,
    insights JSONB DEFAULT '[]'::jsonb,
    CONSTRAINT unique_user_week UNIQUE(user_id, week_start_date)
);

-- 4 TẠO CHỈ MỤC (INDEXES) ĐỂ TỐI ƯU HIỆU NĂNG TÌM KIẾM & CHẠY JOB

-- Index cho các Khóa ngoại (Bắt buộc để join bảng không bị chậm)
CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_debts_user_id ON debts(user_id);
CREATE INDEX idx_bank_app_detects_user_id ON bank_app_detects(user_id);
CREATE INDEX idx_scores_user_id ON financial_scores(user_id);

-- Index cho các cột thường xuyên dùng để Filter (Lọc) và chạy Job ngầm
CREATE INDEX idx_transactions_date ON transactions(transaction_at);
CREATE INDEX idx_transactions_unconfirmed ON transactions(is_confirmed) WHERE is_confirmed = FALSE;

CREATE INDEX idx_debts_due_date ON debts(due_date) WHERE is_active = TRUE;

CREATE INDEX idx_detects_unprocessed ON bank_app_detects(is_processed) WHERE is_processed = FALSE;
CREATE INDEX idx_detects_time ON bank_app_detects(detected_time);