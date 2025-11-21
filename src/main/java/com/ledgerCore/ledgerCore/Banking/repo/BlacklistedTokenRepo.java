package com.ledgerCore.ledgerCore.Banking.repo;

import com.ledgerCore.ledgerCore.Banking.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlacklistedTokenRepo extends JpaRepository<BlacklistedToken, String> {
    Optional<BlacklistedToken> findByToken(String token);
}
