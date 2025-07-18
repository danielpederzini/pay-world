package org.pdzsoftware.payworld_analytics_api.repository.mongo;

import org.pdzsoftware.payworld_analytics_api.dto.PaymentCountDto;
import org.pdzsoftware.payworld_analytics_api.entity.MongoPayment;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoPaymentRepository extends MongoRepository<MongoPayment, String> {
    @Aggregation(pipeline = {
            "{ $group: { _id: '$status', count: { $sum: 1 } } }",
            "{ $project: { _id: 0, status: '$_id', count: 1 } }"
    })
    List<PaymentCountDto> getPaymentCountByStatus();
}
