package com.ledgerCore.ledgerCore.Banking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    private LocalDateTime blacklistedAt = LocalDateTime.now();

    private LocalDateTime expiresAt;

    public BlacklistedToken() {}

    public BlacklistedToken(String token, LocalDateTime expiresAt) {
        this.token = token; this.expiresAt = expiresAt;
    }

    // getters/setters...
    public String getId() { return id; }
    public String getToken() { return token; }
    public LocalDateTime getBlacklistedAt() { return blacklistedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
