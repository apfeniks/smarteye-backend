package org.smarteye.backend.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Клиенты подписываются на /topic/**
        config.enableSimpleBroker("/topic");
        // Клиенты отправляют на /app/**
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Точка подключения WS/STOMP
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // CORS для WS
        // Опционально SockJS для совместимости
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
