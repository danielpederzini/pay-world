package org.pdzsoftware.payworld_payment_processor.repository.mongo;

import org.pdzsoftware.payworld_payment_processor.entity.MongoPayment;
import org.pdzsoftware.payworld_payment_processor.entity.MongoPaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MongoPaymentRepository extends MongoRepository<MongoPayment, String> {
    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { " +
            "'status'      : ?1,  " +
            "'processedAt' : ?2,  " +
            "'updatedAt'   : ?2   " +
            "} }")
    void markPaymentAsCompleted(String uuid,
                                MongoPaymentStatus status,
                                LocalDateTime now);

    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { " +
            "'status'        : ?1,  " +
            "'failureReason' : ?2,  " +
            "'updatedAt'     : ?3   " +
            "} }")
    void markPaymentAsFailed(String uuid,
                             MongoPaymentStatus status,
                             String failureReason,
                             LocalDateTime now);
}
