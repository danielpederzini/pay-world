package org.pdzsoftware.payworld_ingestor.repository;

import org.pdzsoftware.payworld_ingestor.entity.Payment;
import org.pdzsoftware.payworld_ingestor.entity.PaymentStatus;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<Payment, String> {
    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { " +
            "'status'        : ?1,  " +
            "'failureReason' : ?2,  " +
            "'updatedAt'     : ?3   " +
            "} }")
    Mono<Void> markPaymentAsFailed(String uuid,
                                   PaymentStatus status,
                                   String failureReason,
                                   LocalDateTime now);
}
