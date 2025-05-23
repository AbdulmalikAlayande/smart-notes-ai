package app.bola.smartnotesai.ai.service.llm.provider;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DeepSeekProvider implements LLMProvider {
	
	private final ChatClient chatClient;
	
	/**
	 * Constructor for DeepSeekProvider.
	 * <p>
	 * {@param chatClient} the chat client to be used for generating responses
	 */
	public DeepSeekProvider(@Qualifier("deepSeekClient") ChatClient chatClient) {
		this.chatClient = chatClient;
	}
	
	@Override
	public String getName() {
		return "deepseek";
	}
	
	@Override
	public String generate(String prompt) {
		return chatClient.prompt(prompt).call().content();
	}
	
	@Override
	public <T> T generate(String prompt, Class<T> responseType) {
		return chatClient.prompt(prompt).call().entity(responseType);
	}
}
