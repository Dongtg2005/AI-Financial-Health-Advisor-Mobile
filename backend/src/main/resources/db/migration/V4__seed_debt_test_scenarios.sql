-- 1. Chèn thêm User B phục vụ kịch bản không kích hoạt cảnh báo (Chi tiêu ít)
INSERT INTO users (id, financial_stage, monthly_income, debt_free_since, notification_time, created_at, username, password)
VALUES (
    'c81d4e2e-bcf2-11ed-afa1-0242ac120020',
    'DEBT_REPAYMENT',
    pgp_sym_encrypt('30000000', 'FINANCE_SECRET_KEY'),
    '2026-06-16',
    '21:00',
    CURRENT_TIMESTAMP,
    'user_b',
    '$2a$10$OmbZsvpencmrTNgDDTycxe5JY4TMWTvlYj4yu.zkZ6mqGwnlUwNWe' -- mật khẩu mặc định: 'password'
) ON CONFLICT (id) DO NOTHING;

-- 2. Chèn các giao dịch chi tiêu mẫu cho User Admin (c81d4e2e-bcf2-11ed-afa1-0242ac120002) - Thu nhập 50tr
-- Giao dịch 1: Chi tiêu 12.000.000đ mua sắm
INSERT INTO transactions (id, user_id, type, amount, category, entry_method, transaction_at, is_confirmed, confirmed_at)
VALUES (
    'e932b13c-41fb-45ba-bf5d-35be196a6b01',
    'c81d4e2e-bcf2-11ed-afa1-0242ac120002',
    'EXPENSE',
    12000000.00,
    'shopping',
    'QUICK_ADD',
    '2026-06-20 10:00:00',
    true,
    '2026-06-20 10:00:00'
) ON CONFLICT (id) DO NOTHING;

-- Giao dịch 2: Chi tiêu 8.000.000đ ăn uống
INSERT INTO transactions (id, user_id, type, amount, category, entry_method, transaction_at, is_confirmed, confirmed_at)
VALUES (
    'e932b13c-41fb-45ba-bf5d-35be196a6b02',
    'c81d4e2e-bcf2-11ed-afa1-0242ac120002',
    'EXPENSE',
    8000000.00,
    'food',
    'QUICK_ADD',
    '2026-06-22 12:00:00',
    true,
    '2026-06-22 12:00:00'
) ON CONFLICT (id) DO NOTHING;

-- Tổng chi tiêu Admin = 20tr -> Thu nhập dự kiến còn lại = 30tr


-- 3. Chèn các giao dịch chi tiêu mẫu cho User B (c81d4e2e-bcf2-11ed-afa1-0242ac120020) - Thu nhập 30tr
-- Giao dịch 1: Chi tiêu 2.000.000đ ăn uống
INSERT INTO transactions (id, user_id, type, amount, category, entry_method, transaction_at, is_confirmed, confirmed_at)
VALUES (
    'e932b13c-41fb-45ba-bf5d-35be196a6b03',
    'c81d4e2e-bcf2-11ed-afa1-0242ac120020',
    'EXPENSE',
    2000000.00,
    'food',
    'QUICK_ADD',
    '2026-06-21 18:30:00',
    true,
    '2026-06-21 18:30:00'
) ON CONFLICT (id) DO NOTHING;

-- Tổng chi tiêu User B = 2tr -> Thu nhập dự kiến còn lại = 28tr


-- 4. Chèn các khoản nợ mẫu (Debts)

-- Kịch bản 1: Nợ trễ hạn của Admin (dueDate = 2026-06-10 < hiện tại 2026-06-27)
INSERT INTO debts (id, user_id, type, balance, minimum_payment, due_date, overdue_since, is_active)
VALUES (
    'c81d4e2e-bcf2-11ed-afa1-0242ac120010',
    'c81d4e2e-bcf2-11ed-afa1-0242ac120002',
    'CREDIT_CARD',
    pgp_sym_encrypt('5000000', 'FINANCE_SECRET_KEY'),
    250000.00,
    '2026-06-10',
    '2026-06-11',
    true
) ON CONFLICT (id) DO NOTHING;

-- Kịch bản 2: Nợ sắp đến hạn nhưng THIẾU TIỀN của Admin (dueDate = 2026-07-02 cách hiện tại 5 ngày)
-- Số dư nợ = 40.000.000đ > Thu nhập dự kiến còn lại (30.000.000đ) -> Phải cảnh báo thiếu 10tr.
INSERT INTO debts (id, user_id, type, balance, minimum_payment, due_date, overdue_since, is_active)
VALUES (
    'c81d4e2e-bcf2-11ed-afa1-0242ac120011',
    'c81d4e2e-bcf2-11ed-afa1-0242ac120002',
    'MOMO_PAYLATER',
    pgp_sym_encrypt('40000000', 'FINANCE_SECRET_KEY'),
    40000000.00,
    '2026-07-02',
    null,
    true
) ON CONFLICT (id) DO NOTHING;

-- Kịch bản 3: Nợ sắp đến hạn nhưng ĐỦ TIỀN của User B (dueDate = 2026-07-03 cách hiện tại 6 ngày)
-- Số dư nợ = 5.000.000đ < Thu nhập dự kiến còn lại (28.000.000đ) -> Không cảnh báo thiếu tiền.
INSERT INTO debts (id, user_id, type, balance, minimum_payment, due_date, overdue_since, is_active)
VALUES (
    'c81d4e2e-bcf2-11ed-afa1-0242ac120021',
    'c81d4e2e-bcf2-11ed-afa1-0242ac120020',
    'SPAYLATER',
    pgp_sym_encrypt('5000000', 'FINANCE_SECRET_KEY'),
    5000000.00,
    '2026-07-03',
    null,
    true
) ON CONFLICT (id) DO NOTHING;
