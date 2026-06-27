-- Chèn User test phục vụ cho việc gọi API
INSERT INTO users (id, financial_stage, monthly_income, debt_free_since, notification_time, created_at)
VALUES (
    'c81d4e2e-bcf2-11ed-afa1-0242ac120002',
    'DEBT_REPAYMENT',
    pgp_sym_encrypt('50000000', 'FINANCE_SECRET_KEY'),
    '2026-06-16',
    '09:00',
    CURRENT_TIMESTAMP
) ON CONFLICT (id) DO NOTHING;
