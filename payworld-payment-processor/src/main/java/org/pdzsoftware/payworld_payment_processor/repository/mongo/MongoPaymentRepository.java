package org.pdzsoftware.payworld_payment_processor.repository.mongo;

import org.pdzsoftware.payworld_payment_processor.entity.MongoPayment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MongoPaymentRepository extends MongoRepository<MongoPayment, String> {
    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { " +
            "'status'      : 'COMPLETED',  " +
            "'processedAt' : ?1,  " +
            "'updatedAt'   : ?1   " +
            "} }")
    long markPaymentAsCompleted(String uuid,
                                LocalDateTime now);

    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { " +
            "'status'        : 'FAILED_AT_PROCESSING',  " +
            "'failureReason' : ?1,  " +
            "'updatedAt'     : ?2   " +
            "} }")
    long markPaymentAsFailedProcessing(String uuid,
                                       String failureReason,
                                       LocalDateTime now);
}
