package com.krihl4n.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration
import org.springframework.web.socket.handler.WebSocketHandlerDecorator

@Configuration
@EnableWebSocketMessageBroker
open class WebSocketConfig() : WebSocketMessageBrokerConfigurer {

    private var connectionListener: ConnectionListener? = null

    @Autowired
    open fun setConnectionListener(@Lazy connectionListener: ConnectionListener) {
        this.connectionListener = connectionListener;
    }

    // https://coderedirect.com/questions/139260/spring-websocket-sendtosession-send-message-to-specific-session
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/user/queue")
        registry.setApplicationDestinationPrefixes("/chess-app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/game").setAllowedOriginPatterns("*")
        registry.addEndpoint("/game").setAllowedOriginPatterns("*").withSockJS()
    }

    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        registry.addDecoratorFactory { handler ->
            object : WebSocketHandlerDecorator(handler) {
                @Throws(Exception::class)
                override fun afterConnectionEstablished(session: WebSocketSession) {
                    println("connection established: $session")
                    connectionListener?.connectionEstablished(session.id)
                    super.afterConnectionEstablished(session)
                }

                override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
                    println("connection closed: $session")
                    connectionListener?.connectionClosed(session.id)
                    super.afterConnectionClosed(session, closeStatus)
                }
            }
        }

        super.configureWebSocketTransport(registry)
    }
}