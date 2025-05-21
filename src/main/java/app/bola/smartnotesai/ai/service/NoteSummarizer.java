package app.bola.smartnotesai.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NoteSummarizer {

	final ChatClient chatClient;
	final PromptTemplate summarizePrompt;
	
	public NoteSummarizer(ChatClient chatClient,
	                      @Value("classpath:/prompts/summarize-note.st")
	                      Resource summarizePromptResource) {
		
		this.chatClient = chatClient;
		this.summarizePrompt = new PromptTemplate(summarizePromptResource);
	}
	
	public String generateSummary(String noteContent){
		return "";
	}
}
