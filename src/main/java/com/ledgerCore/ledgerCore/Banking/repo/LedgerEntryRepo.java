package com.ledgerCore.ledgerCore.Banking.repo;

import com.ledgerCore.ledgerCore.Banking.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerEntryRepo extends JpaRepository<LedgerEntry, String> {
    List<LedgerEntry> findByAccountNumber(String accountNumber);
    List<LedgerEntry> findByTransactionId(String txId);
}



