CREATE TABLE time_deposits
(
    id        INTEGER PRIMARY KEY,
    plan_type VARCHAR(20)    NOT NULL,
    days      INTEGER        NOT NULL,
    balance   NUMERIC(19, 2) NOT NULL
);

CREATE TABLE withdrawals
(
    id              INTEGER PRIMARY KEY,
    time_deposit_id INTEGER        NOT NULL REFERENCES time_deposits (id),
    amount          NUMERIC(19, 2) NOT NULL,
    date            DATE           NOT NULL
);

CREATE INDEX idx_withdrawals_time_deposit_id ON withdrawals (time_deposit_id);
