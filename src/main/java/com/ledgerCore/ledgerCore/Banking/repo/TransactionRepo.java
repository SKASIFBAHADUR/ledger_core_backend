package com.ledgerCore.ledgerCore.Banking.repo;

import com.ledgerCore.ledgerCore.Banking.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TransactionRepo extends JpaRepository<TransactionRecord, String> {}
