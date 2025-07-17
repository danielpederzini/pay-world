package org.pdzsoftware.payworld_analytics_api.config;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.pdzsoftware.payworld_analytics_api.broadcaster.PaymentBroadcaster;
import org.pdzsoftware.payworld_analytics_api.entity.MongoPayment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.Message;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.data.mongodb.core.query.Criteria;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Slf4j
@Configuration
public class MongoStreamConfig {
    @Bean
    public MessageListenerContainer changeStreamContainer(MongoTemplate mongoTemplate,
                                                          PaymentBroadcaster paymentBroadcaster) {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer(mongoTemplate);

        ChangeStreamRequest<MongoPayment> request = ChangeStreamRequest
                .builder((Message<ChangeStreamDocument<Document>, MongoPayment> msg) -> {
                    MongoPayment payment = msg.getBody();
                    log.info("[MongoStreamConfig] Heard change in payments collection: {}", payment);
                    paymentBroadcaster.broadcast(payment);
                })
                .collection("payments")
                .fullDocumentLookup(FullDocument.UPDATE_LOOKUP)
                .filter(newAggregation(match(
                        Criteria.where("operationType").in("insert", "update", "replace")
                )))
                .build();

        container.register(request, MongoPayment.class);
        container.start();
        return container;
    }
}
