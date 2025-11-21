package com.ledgerCore.ledgerCore.Banking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String token; // random opaque token

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    private LocalDateTime expiresAt;

    private boolean revoked = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public RefreshToken(){}

    public RefreshToken(String token, AppUser user, LocalDateTime expiresAt) {
        this.token = token; this.user = user; this.expiresAt = expiresAt;
    }

    // getters / setters
    public String getId() { return id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                ", user=" + user +
                ", expiresAt=" + expiresAt +
                ", revoked=" + revoked +
                ", createdAt=" + createdAt +
                '}';
    }
}
