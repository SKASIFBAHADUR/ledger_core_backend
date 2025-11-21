CREATE TABLE ledger_entries (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    transaction_id VARCHAR(255),
    account_id VARCHAR(255),
    entry_type VARCHAR(50),  -- CREDIT / DEBIT
    amount DOUBLE,
    description VARCHAR(255),
    created_at DATETIME DEFAULT NOW(),
    CONSTRAINT fk_ledger_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    CONSTRAINT fk_ledger_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);
