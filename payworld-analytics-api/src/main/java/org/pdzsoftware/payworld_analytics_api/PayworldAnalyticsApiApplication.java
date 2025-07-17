package org.pdzsoftware.payworld_analytics_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@EnableWebSocketMessageBroker
@EnableJpaRepositories(basePackages = "org.pdzsoftware.payworld_analytics_api.repository.sql")
@EnableMongoRepositories(basePackages = "org.pdzsoftware.payworld_analytics_api.repository.mongo")
@SpringBootApplication
public class PayworldAnalyticsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayworldAnalyticsApiApplication.class, args);
	}

}
