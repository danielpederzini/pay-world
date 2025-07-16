package org.pdzsoftware.payworld_payment_processor.repository.mongo;

import org.pdzsoftware.payworld_payment_processor.entity.MongoPayment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoPaymentRepository extends MongoRepository<MongoPayment, String> {
}
