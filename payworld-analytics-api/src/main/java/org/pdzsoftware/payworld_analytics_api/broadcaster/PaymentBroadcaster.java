package org.pdzsoftware.payworld_analytics_api.broadcaster;

import lombok.RequiredArgsConstructor;
import org.pdzsoftware.payworld_analytics_api.entity.MongoPayment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentBroadcaster {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcast(MongoPayment payload) {
        messagingTemplate.convertAndSend("/topic/payments", payload);
    }
}
