package app.bola.smartnotesai.ai.service.llm.provider;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

@Component
public class OpenAIProvider implements LLMProvider {
	
	final ChatClient chatClient;
	
	public OpenAIProvider(OpenAiChatModel chatModel) {
		this.chatClient = ChatClient.create(chatModel);
	}
	
	@Override
	public String getName() {
		return "openai";
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
