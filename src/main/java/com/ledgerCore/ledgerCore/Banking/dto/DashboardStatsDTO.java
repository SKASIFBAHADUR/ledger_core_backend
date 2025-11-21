package com.ledgerCore.ledgerCore.Banking.dto;

public class DashboardStatsDTO {
    private long totalCustomers;
    private long totalAccounts;
    private long totalTransactions;
    private double totalBalance;

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(long totalCustomers, long totalAccounts, long totalTransactions, double totalBalance) {
        this.totalCustomers = totalCustomers;
        this.totalAccounts = totalAccounts;
        this.totalTransactions = totalTransactions;
        this.totalBalance = totalBalance;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalAccounts() {
        return totalAccounts;
    }

    public void setTotalAccounts(long totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }
}

