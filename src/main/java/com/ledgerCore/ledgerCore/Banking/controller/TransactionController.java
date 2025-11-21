package com.ledgerCore.ledgerCore.Banking.controller;

import com.ledgerCore.ledgerCore.Banking.entity.TransactionRecord;
import com.ledgerCore.ledgerCore.Banking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired private TransactionService transactionService;

    @PostMapping("/deposit/{accountNumber}/{amount}")
    public TransactionRecord deposit(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @PathVariable String accountNumber,
            @PathVariable Double amount,
            @RequestParam(value = "reference", required = false) String reference
    ) {
        return transactionService.deposit(idempotencyKey, accountNumber, amount, reference);
    }

    @PostMapping("/withdraw/{accountNumber}/{amount}")
    public TransactionRecord withdraw(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @PathVariable String accountNumber,
            @PathVariable Double amount,
            @RequestParam(value = "reference", required = false) String reference
    ) {
        return transactionService.withdraw(idempotencyKey, accountNumber, amount, reference);
    }

    @PostMapping("/transfer/{fromAccountNumber}/{toAccountNumber}/{amount}")
    public TransactionRecord transfer(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @PathVariable("fromAccountNumber") String fromAccountNumber,
            @PathVariable("toAccountNumber") String toAccountNumber,
            @PathVariable Double amount,
            @RequestParam(value = "reference", required = false) String reference
    ) {
        return transactionService.transfer(idempotencyKey, fromAccountNumber, toAccountNumber, amount, reference);
    }

}
