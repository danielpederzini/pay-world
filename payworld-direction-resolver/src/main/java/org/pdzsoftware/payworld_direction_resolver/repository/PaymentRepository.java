package org.pdzsoftware.payworld_direction_resolver.repository;

import org.pdzsoftware.payworld_direction_resolver.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
}
