package com.ledgerCore.ledgerCore.Banking.service;

import com.ledgerCore.ledgerCore.Banking.entity.Account;
import com.ledgerCore.ledgerCore.Banking.entity.Customer;
import com.ledgerCore.ledgerCore.Banking.repo.AccountRepo;
import com.ledgerCore.ledgerCore.Banking.repo.Customerrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private Customerrepo customerrepo;

    // Create account for given customer id (Long)
    public Account createAccount(Long customerId, Account account) {

        Customer customer = customerrepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        // generate id & account number if not provided
        if (account.getId() == null || account.getId().isBlank()) {
            account.setId(UUID.randomUUID().toString());
        }

        if (account.getAccountNumber() == null || account.getAccountNumber().isBlank()) {
            account.setAccountNumber(generateAccountNumber());
        }

        if (account.getBalance() == null) {
            account.setBalance(0.0); // initial balance
        }

        account.setCustomer(customer);

        return accountRepo.save(account);
    }

    public Account getAccount(String id) {
        return accountRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public String deleteAccount(String id) {
        accountRepo.deleteById(id);
        return "Account deleted successfully";
    }

    public java.util.List<Account> getAllAccounts() {
        return accountRepo.findAll();
    }

    private String generateAccountNumber() {
        return "AC" + System.currentTimeMillis();
    }
}
