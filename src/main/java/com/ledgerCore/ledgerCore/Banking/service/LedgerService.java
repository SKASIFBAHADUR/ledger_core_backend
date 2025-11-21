package com.ledgerCore.ledgerCore.Banking.service;

import com.ledgerCore.ledgerCore.Banking.entity.LedgerEntry;
import com.ledgerCore.ledgerCore.Banking.repo.LedgerEntryRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service

public class LedgerService {

    private final LedgerEntryRepo ledgerRepo;

    public LedgerService(LedgerEntryRepo ledgerRepo) {
        this.ledgerRepo = ledgerRepo;
    }

    @Transactional
    public LedgerEntry addEntry(String transactionId, String accountNumber, String entryType, Double amount, String description) {
        LedgerEntry entry = new LedgerEntry(
                transactionId,
                accountNumber,
                entryType,
                amount,
                description
        );

        return ledgerRepo.save(entry);
    }
}

