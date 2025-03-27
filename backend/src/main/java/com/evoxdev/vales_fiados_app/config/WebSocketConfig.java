package com.evoxdev.vales_fiados_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefixo para endpoints que retornam mensagens para o cliente (retornos de métodos anotados com @SendTo)
        config.enableSimpleBroker("/topic", "/queue");

        // Prefixo para endpoints que recebem mensagens do cliente
        config.setApplicationDestinationPrefixes("/app");

        // Canal para mensagens privadas a usuários específicos
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para conectar ao websocket, com fallback para SockJS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Ajuste isso em produção para origens específicas
                .withSockJS();
    }
}