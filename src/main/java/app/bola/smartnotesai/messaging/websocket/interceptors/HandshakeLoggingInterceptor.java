package app.bola.smartnotesai.messaging.websocket.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
public class HandshakeLoggingInterceptor implements HandshakeInterceptor {
	
	
	@Override
	public boolean beforeHandshake(@NonNull ServerHttpRequest request,
	                               @NonNull ServerHttpResponse response,
	                               @NonNull WebSocketHandler wsHandler,
	                               @NonNull Map<String, Object> attributes) {
		log.info("WebSocket Handshake initiated: URI = {}, Headers = {}", request.getURI(), request.getHeaders());
		return true;
	}
	
	@Override
	public void afterHandshake(@NonNull ServerHttpRequest request,
	                           @NonNull ServerHttpResponse response,
	                           @NonNull WebSocketHandler wsHandler, Exception exception) {
		
		log.info("WebSocket Handshake completed: URI = {}", request.getURI());
	}
}
