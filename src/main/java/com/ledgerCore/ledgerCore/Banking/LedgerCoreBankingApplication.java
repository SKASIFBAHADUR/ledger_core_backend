package com.ledgerCore.ledgerCore.Banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LedgerCoreBankingApplication {

	public static void main(String[] args) {

		SpringApplication.run(LedgerCoreBankingApplication.class, args);
		
	}

}
