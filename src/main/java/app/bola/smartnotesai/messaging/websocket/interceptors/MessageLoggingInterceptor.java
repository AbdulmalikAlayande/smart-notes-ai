package app.bola.smartnotesai.messaging.websocket.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageLoggingInterceptor implements ChannelInterceptor {
	
	@Override
	public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
		log.info("Message preSend: Channel = {}, Message = {}", channel, message);
		StompHeaderAccessor accessor =
				MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		
		if (accessor != null && accessor.getCommand() != null) {
			StompCommand command = accessor.getCommand();
			log.info("STOMP command: {}", command);
			log.info("Headers: {}", accessor.toNativeHeaderMap());
			log.info("Session ID: {}", accessor.getSessionId());
		}
		
		return message;
	}
}
