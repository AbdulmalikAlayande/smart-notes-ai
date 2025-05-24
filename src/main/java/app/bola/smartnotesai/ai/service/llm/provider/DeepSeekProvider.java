//package app.bola.smartnotesai.ai.service.llm.provider;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.deepseek.DeepSeekChatModel;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DeepSeekProvider implements LLMProvider {
//
//	private final ChatClient chatClient;
//
//	/**
//	 * Constructor for DeepSeekProvider.
//	 * <p>
//	 * {@param chatClient} the chat client to be used for generating responses
//	 * </p>
//	 */
//	public DeepSeekProvider(DeepSeekChatModel chatModel) {
//		this.chatClient = ChatClient.create(chatModel);
//	}
//
//	@Override
//	public String getName() {
//		return "deepseek";
//	}
//
//	@Override
//	public String generate(String prompt) {
//		String content = chatClient.prompt(prompt).call().content();
//		if (content != null) {
//			logMessage(getName(), content.substring(0, 10)+"...");
//		}
//		return content;
//	}
//
//	@Override
//	public <T> T generate(String prompt, Class<T> responseType) {
//		T content = chatClient.prompt(prompt).call().entity(responseType);
//		if (content != null) {
//			logMessage(getName(), content.toString());
//		}
//		return content;
//	}
//}
