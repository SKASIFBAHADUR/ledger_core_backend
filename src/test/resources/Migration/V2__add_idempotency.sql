CREATE TABLE idempotency_keys (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    key_value VARCHAR(255) NOT NULL UNIQUE,
    request_body TEXT,
    response_transaction_id VARCHAR(255),
    created_at DATETIME DEFAULT NOW()
);
