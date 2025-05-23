package app.bola.smartnotesai.ai.service.llm.provider;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Component;

@Component
public class OllamaProvider implements LLMProvider {
	
	private final ChatClient chatClient;
	
	public OllamaProvider(OllamaChatModel chatModel) {
		this.chatClient = ChatClient.create(chatModel);
	}
	
	@Override
	public String getName() {
		return "ollama";
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
