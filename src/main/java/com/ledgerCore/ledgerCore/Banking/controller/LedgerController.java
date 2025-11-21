package com.ledgerCore.ledgerCore.Banking.controller;

import com.ledgerCore.ledgerCore.Banking.entity.LedgerEntry;
import com.ledgerCore.ledgerCore.Banking.repo.LedgerEntryRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ledger")
public class LedgerController {

    private final LedgerEntryRepo ledgerEntryRepo;

    public LedgerController(LedgerEntryRepo ledgerEntryRepo) {
        this.ledgerEntryRepo = ledgerEntryRepo;
    }

    @GetMapping("/account/{accNo}")
    public List<LedgerEntry> byAccount(@PathVariable("accNo") String accountNumber) {
        return ledgerEntryRepo.findByAccountNumber(accountNumber);
    }

    @GetMapping("/reference/{ref}")
    public List<LedgerEntry> byReference(@PathVariable("ref") String refId) {
        return ledgerEntryRepo.findByTransactionId(refId);
    }
}
