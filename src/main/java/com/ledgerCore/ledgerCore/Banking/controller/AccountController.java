package com.ledgerCore.ledgerCore.Banking.controller;

import com.ledgerCore.ledgerCore.Banking.entity.Account;
import com.ledgerCore.ledgerCore.Banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Get all accounts
    @GetMapping("/list")
    public java.util.List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // Create account for customer (customer id is LONG)
    @PostMapping("/create/{customerId}")
    public Account createAccount(@PathVariable("customerId") Long customerId, @RequestBody Account account) {
        return accountService.createAccount(customerId, account);
    }

    // Get account by account id (String)
    @GetMapping("/get/{accountId}")
    public Account getAccount(@PathVariable("accountId") String accountId) {
        return accountService.getAccount(accountId);
    }

    // Delete account by account id (String)
    @DeleteMapping("/delete/{accountId}")
    public String deleteAccount(@PathVariable("accountId") String accountId) {
        return accountService.deleteAccount(accountId);
    }
}
