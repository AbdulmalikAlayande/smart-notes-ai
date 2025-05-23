package app.bola.smartnotesai.ai.service.llm.provider;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class AnthropicProvider implements LLMProvider {
	
	final ChatClient chatClient;
	
	public AnthropicProvider(AnthropicChatModel chatModel) {
		this.chatClient = ChatClient.create(chatModel);
	}
	
	@Override
	public String getName() {
		return "anthropic";
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
