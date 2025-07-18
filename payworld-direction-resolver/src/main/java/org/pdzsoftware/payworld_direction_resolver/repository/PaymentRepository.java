package org.pdzsoftware.payworld_direction_resolver.repository;

import org.pdzsoftware.payworld_direction_resolver.entity.Payment;
import org.pdzsoftware.payworld_direction_resolver.entity.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { " +
            "'status'           : ?1,  " +
            "'senderKey'        : ?2,  " +
            "'receiverKey'      : ?3,  " +
            "'originalCurrency' : ?4,  " +
            "'newCurrency'      : ?5,  " +
            "'originalAmount'   : ?6,  " +
            "'convertedAmount'  : ?7,  " +
            "'convertedAt'      : ?8,  " +
            "'updatedAt'        : ?8   " +
            "} }")
    void markPaymentAsEnriched(String uuid,
                               PaymentStatus status,
                               String senderKey,
                               String receiverKey,
                               Currency originalCurrency,
                               Currency newCurrency,
                               BigDecimal originalAmount,
                               BigDecimal convertedAmount,
                               LocalDateTime convertedAt);

    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { " +
            "'status'        : ?1,  " +
            "'failureReason' : ?2,  " +
            "'updatedAt'     : ?3   " +
            "} }")
    void markPaymentAsFailed(String uuid,
                             PaymentStatus status,
                             String failureReason,
                             LocalDateTime now);
}
