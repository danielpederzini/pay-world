package org.pdzsoftware.payworld_direction_resolver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableCaching
@EnableFeignClients
@SpringBootApplication
public class PayworldDirectionResolverApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayworldDirectionResolverApplication.class, args);
	}

}
