package com.ledgerCore.ledgerCore.Banking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyValue;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    private String responseTransactionId;

    private LocalDateTime createdAt = LocalDateTime.now();

    public IdempotencyKey() {}

    public IdempotencyKey(String keyValue, String requestBody, String responseTransactionId) {
        this.keyValue = keyValue;
        this.requestBody = requestBody;
        this.responseTransactionId = responseTransactionId;
    }

    public Long getId() { return id; }
    public String getKeyValue() { return keyValue; }
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }

    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }

    public String getResponseTransactionId() { return responseTransactionId; }
    public void setResponseTransactionId(String responseTransactionId) { this.responseTransactionId = responseTransactionId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
