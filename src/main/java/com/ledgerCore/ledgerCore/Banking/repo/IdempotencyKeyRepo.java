package com.ledgerCore.ledgerCore.Banking.repo;

import com.ledgerCore.ledgerCore.Banking.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IdempotencyKeyRepo extends JpaRepository<IdempotencyKey, Long> {

    // IMPORTANT — TransactionService depends on this
    Optional<IdempotencyKey> findByKeyValue(String keyValue);

    // Optional cleanup support
    List<IdempotencyKey> findByCreatedAtBefore(LocalDateTime cutoff);
}
