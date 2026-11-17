-- Dev-only sample data, loaded by the dev profile (see application-dev.yml).
INSERT INTO time_deposits (id, plan_type, days, balance)
VALUES (1, 'basic', 30, 10000.00),   -- first 30 days: no interest
       (2, 'basic', 45, 1234567.00), -- 1%
       (3, 'student', 200, 5000.00), -- 3%
       (4, 'student', 366, 5000.00), -- over 1 year: no interest
       (5, 'premium', 45, 20000.00), -- premium starts after 45 days: no interest
       (6, 'premium', 46, 20000.00)  -- 5%
    ON CONFLICT (id) DO NOTHING;

INSERT INTO withdrawals (id, time_deposit_id, amount, date)
VALUES (1, 2, 500.00, '2026-01-15'),
       (2, 2, 250.50, '2026-02-10'),
       (3, 6, 1000.00, '2026-03-01') ON CONFLICT (id) DO NOTHING;
