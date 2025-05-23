package app.bola.smartnotesai.ai.service.llm.provider;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class GrokProvider implements LLMProvider {
	
	final ChatClient chatClient;
	
	/**
	 * Constructor for GrokProvider.
	 * <p>
	 * {@param chatClient} the chat client to be used for generating responses
	 */
	public GrokProvider(@Qualifier("grokClient") ChatClient chatClient) {
		this.chatClient = chatClient;
	}
	
	@Override
	public String getName() {
		return "grok";
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
