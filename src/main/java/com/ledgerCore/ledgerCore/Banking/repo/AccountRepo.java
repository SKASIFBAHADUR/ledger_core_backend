package com.ledgerCore.ledgerCore.Banking.repo;

import com.ledgerCore.ledgerCore.Banking.entity.Account;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;

public interface AccountRepo extends JpaRepository<Account, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    java.util.Optional<Account> findById(String accountNumber);
}
