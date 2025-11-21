CREATE TABLE blacklisted_tokens (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    token TEXT NOT NULL,
    blacklisted_at DATETIME DEFAULT NOW(),
    expires_at DATETIME
);
