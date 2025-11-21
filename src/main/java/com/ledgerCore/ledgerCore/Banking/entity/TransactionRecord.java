package com.ledgerCore.ledgerCore.Banking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionRecord {
    @Id
    private String id = UUID.randomUUID().toString();

    private String type; // DEPOSIT, WITHDRAW, TRANSFER

    private String reference; // optional client reference

    private Double amount;

    private String status; // SUCCESS, FAILED

    private LocalDateTime createdAt = LocalDateTime.now();

    public TransactionRecord() {}
    public TransactionRecord(String type, Double amount, String reference, String status) {
        this.type = type; this.amount = amount; this.reference = reference; this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TransactionRecord{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", reference='" + reference + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
