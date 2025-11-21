CREATE TABLE refresh_tokens (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT NOW(),
    expires_at DATETIME,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id)
);
