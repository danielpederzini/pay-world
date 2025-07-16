package org.pdzsoftware.payworld_ingestor.repository;

import org.pdzsoftware.payworld_ingestor.entity.Payment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<Payment, String> {
}
