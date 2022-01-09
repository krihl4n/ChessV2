package com.krihl4n

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
open class WebSocketConfig: WebSocketMessageBrokerConfigurer {

    // https://coderedirect.com/questions/139260/spring-websocket-sendtosession-send-message-to-specific-session
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
        registry.setApplicationDestinationPrefixes("/chessApp")
        registry.enableSimpleBroker("/secured/user/queue/specific-user")
    //    registry.setUserDestinationPrefix("/secured/user")// not needed?
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/game").setAllowedOriginPatterns("*")
        registry.addEndpoint("/game").setAllowedOriginPatterns("*").withSockJS()
        registry.addEndpoint("/secured/room").setAllowedOriginPatterns("*").withSockJS()
    }
}