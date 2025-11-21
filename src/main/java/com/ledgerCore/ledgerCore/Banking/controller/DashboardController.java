package com.ledgerCore.ledgerCore.Banking.controller;

import com.ledgerCore.ledgerCore.Banking.dto.DashboardStatsDTO;
import com.ledgerCore.ledgerCore.Banking.entity.Account;
import com.ledgerCore.ledgerCore.Banking.repo.AccountRepo;
import com.ledgerCore.ledgerCore.Banking.repo.Customerrepo;
import com.ledgerCore.ledgerCore.Banking.repo.TransactionRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final Customerrepo customerRepo;
    private final AccountRepo accountRepo;
    private final TransactionRepo transactionRepo;

    public DashboardController(Customerrepo customerRepo, AccountRepo accountRepo, TransactionRepo transactionRepo) {
        this.customerRepo = customerRepo;
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }

    @GetMapping("/stats")
    public DashboardStatsDTO getStats() {
        long totalCustomers = customerRepo.count();
        long totalAccounts = accountRepo.count();
        long totalTransactions = transactionRepo.count();

        List<Account> accounts = accountRepo.findAll();
        double totalBalance = accounts.stream()
                .map(Account::getBalance)
                .filter(balance -> balance != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        return new DashboardStatsDTO(totalCustomers, totalAccounts, totalTransactions, totalBalance);
    }
}

