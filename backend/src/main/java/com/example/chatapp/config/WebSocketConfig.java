package com.example.chatapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Value("${rabbitmq.host:}")
    private String rabbitmqHost;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        if (rabbitmqHost != null && !rabbitmqHost.isEmpty()) {
            // Use RabbitMQ if configured
            config.enableStompBrokerRelay("/topic").setRelayHost(rabbitmqHost).setRelayPort(61613);
            logger.info("Using RabbitMQ broker relay at {}:61613", rabbitmqHost);
        } else {
            // Fallback to simple broker
            config.enableSimpleBroker("/topic");
            logger.info("Using simple in-memory broker (RabbitMQ not configured)");
        }
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        logger.info("Registering STOMP endpoint /ws with allowed origins http://localhost:3000");
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:3000").withSockJS();
    }
}