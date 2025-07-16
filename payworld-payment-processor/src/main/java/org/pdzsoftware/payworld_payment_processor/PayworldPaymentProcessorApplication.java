package org.pdzsoftware.payworld_payment_processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableJpaRepositories(basePackages = "org.pdzsoftware.payworld_payment_processor.repository.sql")
@EnableMongoRepositories(basePackages = "org.pdzsoftware.payworld_payment_processor.repository.mongo")
@SpringBootApplication
public class PayworldPaymentProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayworldPaymentProcessorApplication.class, args);
	}

}
