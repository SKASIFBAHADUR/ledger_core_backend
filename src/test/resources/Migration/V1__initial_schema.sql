-- Users table (Admin + Regular Users)
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ROLE_ADMIN','ROLE_USER') NOT NULL DEFAULT 'ROLE_USER',
    status VARCHAR(50) DEFAULT 'ACTIVE'
);

-- Customers table
CREATE TABLE customers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(50),
    created_at DATETIME DEFAULT NOW()
);

-- Accounts table
CREATE TABLE accounts (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    account_type VARCHAR(50),
    balance DOUBLE NOT NULL,
    created_at DATETIME DEFAULT NOW(),
    customer_id BIGINT,
    CONSTRAINT fk_accounts_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Basic transactions table (status: SUCCESS, FAILED, etc.)
CREATE TABLE transactions (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    amount DOUBLE,
    type VARCHAR(50),
    status VARCHAR(50),
    reference VARCHAR(255),
    created_at DATETIME DEFAULT NOW()
);
