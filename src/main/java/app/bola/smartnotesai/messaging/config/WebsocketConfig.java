package app.bola.smartnotesai.messaging.config;

import app.bola.smartnotesai.messaging.websocket.interceptors.HandshakeLoggingInterceptor;
import app.bola.smartnotesai.messaging.websocket.interceptors.MessageLoggingInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@AllArgsConstructor
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
	
	final MessageLoggingInterceptor messageLoggingInterceptor;
	final HandshakeLoggingInterceptor loggingInterceptor;
	
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(messageLoggingInterceptor);
	}
	
	/**
	 * Configures the message broker for WebSocket communication.
	 * Enables a simple in-memory message broker and sets the application destination prefix.
	 *
	 * @param registry the MessageBrokerRegistry to configure
	 */
	@Override
	public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic", "/queue");
		registry.setApplicationDestinationPrefixes("/app");
	}
	
	/**
	 * Registers STOMP endpoints for WebSocket communication.
	 * Sets allowed origins and enables SockJS fallback options.
	 *
	 * @param registry the StompEndpointRegistry to register endpoints
	 */
	@Override
	public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOrigins("http://localhost:3000", "http://localhost:8081")
				.addInterceptors(loggingInterceptor)
				.withSockJS();
	}
	
}

