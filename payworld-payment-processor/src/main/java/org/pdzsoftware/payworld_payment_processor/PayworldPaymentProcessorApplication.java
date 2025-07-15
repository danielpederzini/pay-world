package org.pdzsoftware.payworld_payment_processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class PayworldPaymentProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayworldPaymentProcessorApplication.class, args);
	}

}
