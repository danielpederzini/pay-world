package org.pdzsoftware.payworld_analytics_api.repository.mongo;

import org.pdzsoftware.payworld_analytics_api.entity.MongoPayment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoPaymentRepository extends MongoRepository<MongoPayment, String> {
}
