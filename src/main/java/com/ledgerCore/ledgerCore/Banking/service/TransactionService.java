package com.ledgerCore.ledgerCore.Banking.service;

import com.ledgerCore.ledgerCore.Banking.entity.*;
import com.ledgerCore.ledgerCore.Banking.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {

    @Autowired private AccountRepo accountRepo;
    @Autowired private TransactionRepo transactionRepo;
    @Autowired private LedgerEntryRepo ledgerEntryRepo;
    @Autowired private IdempotencyKeyRepo idempotencyKeyRepo;

    // ----------------------------------------------------
    //  Helper: Check idempotency
    // ----------------------------------------------------
    private Optional<IdempotencyKey> findIdempotency(String key) {
        if (key == null || key.isBlank()) return Optional.empty();
        return idempotencyKeyRepo.findByKeyValue(key);
    }

    // ----------------------------------------------------
    //  DEPOSIT
    // ----------------------------------------------------
    @Transactional
    public TransactionRecord deposit(String idempotencyKey, String accountNumber, Double amount, String reference) {

        // 1. Idempotency
        Optional<IdempotencyKey> existing = findIdempotency(idempotencyKey);
        if (existing.isPresent()) {
            return transactionRepo.findById(existing.get().getResponseTransactionId()).orElseThrow();
        }

        // 2. Lock account
        Account account = accountRepo.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        // 3. Create TX record
        TransactionRecord tx = new TransactionRecord("DEPOSIT", amount, reference, "SUCCESS");
        tx = transactionRepo.save(tx);

        // 4. Create Ledger Entry: CREDIT
        LedgerEntry credit = new LedgerEntry(
                tx.getId(),
                account.getAccountNumber(),
                "CREDIT",
                amount,
                "Deposit"
        );
        ledgerEntryRepo.save(credit);

        // 5. Update balance
        account.setBalance(account.getBalance() + amount);
        accountRepo.save(account);

        // 6. Store idempotency
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyKeyRepo.save(new IdempotencyKey(idempotencyKey, null, tx.getId()));
        }

        return tx;
    }

    // ----------------------------------------------------
    //  WITHDRAW
    // ----------------------------------------------------
    @Transactional
    public TransactionRecord withdraw(String idempotencyKey, String accountNumber, Double amount, String reference) {

        Optional<IdempotencyKey> existing = findIdempotency(idempotencyKey);
        if (existing.isPresent()) {
            return transactionRepo.findById(existing.get().getResponseTransactionId()).orElseThrow();
        }

        Account account = accountRepo.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        TransactionRecord tx = new TransactionRecord("WITHDRAW", amount, reference, "SUCCESS");
        tx = transactionRepo.save(tx);

        LedgerEntry debit = new LedgerEntry(
                tx.getId(),
                account.getAccountNumber(),
                "DEBIT",
                amount,
                "Withdraw"
        );
        ledgerEntryRepo.save(debit);

        account.setBalance(account.getBalance() - amount);
        accountRepo.save(account);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyKeyRepo.save(new IdempotencyKey(idempotencyKey, null, tx.getId()));
        }

        return tx;
    }

    // ----------------------------------------------------
    //  TRANSFER  (FIXED & STABLE)
    // ----------------------------------------------------
    @Transactional
    public TransactionRecord transfer(String idempotencyKey,
                                      String fromAccountNumber,
                                      String toAccountNumber,
                                      Double amount,
                                      String reference) {

        // 1. Idempotency
        Optional<IdempotencyKey> existing = findIdempotency(idempotencyKey);
        if (existing.isPresent()) {
            return transactionRepo.findById(existing.get().getResponseTransactionId()).orElseThrow();
        }

        // 2. Lock accounts in sorted order to avoid DEADLOCK
        String first = fromAccountNumber.compareTo(toAccountNumber) < 0 ? fromAccountNumber : toAccountNumber;
        String second = first.equals(fromAccountNumber) ? toAccountNumber : fromAccountNumber;

        Account acc1 = accountRepo.findById(first)
                .orElseThrow(() -> new RuntimeException("Account not found: " + first));

        Account acc2 = accountRepo.findById(second)
                .orElseThrow(() -> new RuntimeException("Account not found: " + second));

        Account from = acc1.getAccountNumber().equals(fromAccountNumber) ? acc1 : acc2;
        Account to = from == acc1 ? acc2 : acc1;

        // 3. Validate balance
        if (from.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        // 4. Create TX
        TransactionRecord tx = new TransactionRecord("TRANSFER", amount, reference, "SUCCESS");
        tx = transactionRepo.save(tx);

        // 5. Ledger: debit FROM
        LedgerEntry debit = new LedgerEntry(
                tx.getId(),
                from.getAccountNumber(),
                "DEBIT",
                amount,
                "Transfer to " + to.getAccountNumber()
        );
        ledgerEntryRepo.save(debit);

        // 6. Ledger: credit TO
        LedgerEntry credit = new LedgerEntry(
                tx.getId(),
                to.getAccountNumber(),
                "CREDIT",
                amount,
                "Transfer from " + from.getAccountNumber()
        );
        ledgerEntryRepo.save(credit);

        // 7. Update balances
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        accountRepo.save(from);
        accountRepo.save(to);

        // 8. Save idempotency
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyKeyRepo.save(new IdempotencyKey(idempotencyKey, null, tx.getId()));
        }

        return tx;
    }
}
