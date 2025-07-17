package org.pdzsoftware.payworld_analytics_api.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Whenever a front-end client connects to the websocket
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("[WebSocketChannelInterceptor] → CONNECTED: session={}", accessor.getSessionId());
        }

        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Whenever a front-end client disconnects from the websocket
        if (accessor != null && StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            log.info("[WebSocketChannelInterceptor] ← DISCONNECTED: session={}", accessor.getSessionId());
        }
    }
}