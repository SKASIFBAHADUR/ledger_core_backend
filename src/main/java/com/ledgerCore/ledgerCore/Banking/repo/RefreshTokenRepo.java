package com.ledgerCore.ledgerCore.Banking.repo;

import com.ledgerCore.ledgerCore.Banking.entity.RefreshToken;
import com.ledgerCore.ledgerCore.Banking.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(AppUser user);
}
