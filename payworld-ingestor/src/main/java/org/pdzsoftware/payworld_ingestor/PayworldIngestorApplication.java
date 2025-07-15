package org.pdzsoftware.payworld_ingestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class PayworldIngestorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayworldIngestorApplication.class, args);
	}

}
