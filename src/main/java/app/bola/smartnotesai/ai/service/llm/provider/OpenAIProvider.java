package app.bola.smartnotesai.ai.service.llm.provider;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

@Component
public class OpenAIProvider implements LLMProvider {

	final ChatClient chatClient;

	/**
	 * Constructor for OpenAIProvider.
	 * <p>
	 * {@param chatClient} the chat client to be used for generating responses
	 */
	public OpenAIProvider(OpenAiChatModel chatModel) {
		this.chatClient = ChatClient.create(chatModel);
	}

	@Override
	public String getName() {
		return "openai";
	}

	@Override
	public String generate(String prompt) {
		String content = chatClient.prompt(prompt).call().content();
		if (content != null) {
			logMessage(getName(), content.substring(0, 10)+"...");
		}
		return content;
	}

	@Override
	public <T> T generate(String prompt, Class<T> responseType) {
		T content = chatClient.prompt(prompt).call().entity(responseType);
		if (content != null) {
			logMessage(getName(), content.toString());
		}
		return content;
	}
}
