package com.ledgerCore.ledgerCore.Banking;


import com.ledgerCore.ledgerCore.Banking.LedgerCoreBankingApplication;
import com.ledgerCore.ledgerCore.Banking.entity.Account;
import com.ledgerCore.ledgerCore.Banking.repo.AccountRepo;
import com.ledgerCore.ledgerCore.Banking.repo.TransactionRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(
        classes = LedgerCoreBankingApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
// gives you MockMvc
@ActiveProfiles("test") // use application-test.properties


public class IntegrationTest {


        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private AccountRepo accountRepository;

        @Autowired
        private TransactionRepo transactionRecordRepository;

        @BeforeEach
        void setup() {
            // Clean DB between tests
            transactionRecordRepository.deleteAll();
            accountRepository.deleteAll();

            // Insert a test account
            Account acc = new Account();
            acc.setId("ACC1");
            acc.setAccountNumber("ACC1");
            acc.setBalance(1000.0);
            accountRepository.save(acc);
        }

        @Test
        void depositShouldIncreaseBalanceAndCreateTransaction() throws Exception {
            // 1. Call your real endpoint (no mocking)
            mockMvc.perform(post("/transactions/deposit/ACC1/500")
                            .header("Idempotency-Key", "KEY123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.amount").value(500.0));

            // 2. Verify DB is updated
            Account updated = accountRepository.findById("ACC1").orElseThrow();
            assertThat(updated.getBalance()).isEqualTo(1500.0);

            // 3. Verify transaction created
            assertThat(transactionRecordRepository.findAll()).hasSize(1);
        }
    }





