package app.bola.smartnotesai.ai.service;

import app.bola.smartnotesai.ai.dto.NoteSummarizerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class NoteSummarizer {

	final ChatClient chatClient;
	final PromptTemplate summarizePrompt;
	private static final int MAX_CHARS = 10000;
	
	public NoteSummarizer(ChatClient chatClient,
	                      @Value("prompts/summarize-note.st")
	                      ClassPathResource summarizePromptResource) {
		
		this.chatClient = chatClient;
		this.summarizePrompt = new PromptTemplate(summarizePromptResource);
	}
	
	@Async
	public CompletableFuture<NoteSummarizerResponse> generateSummaryAsync(String noteContent){
		return CompletableFuture.supplyAsync(() -> generateSummary(noteContent));
	}
	
	public NoteSummarizerResponse generateSummary(String noteContent){
		String processedNote = truncateIfNeeded(noteContent);
		try {
			Prompt prompt = summarizePrompt.create(Map.of("note", processedNote));
			NoteSummarizerResponse summarizedNote = chatClient.prompt(prompt)
					                                .call()
					                                .entity(NoteSummarizerResponse.class);
			log.info("Summarized note: {}", summarizedNote);
			return summarizedNote;
		} catch (Exception exception) {
			log.error("Failed to summarize note {}", String.valueOf(exception));
			exception.printStackTrace();
			return createFallbackResponse();
		}
		
	}
	
	private NoteSummarizerResponse createFallbackResponse() {
		return NoteSummarizerResponse.builder()
				       .summary("Unable to generate summary")
				       .tags(List.of("error"))
				       .keyPoints(List.of("Summary generation failed"))
				       .build();
	}
	
	
	private String truncateIfNeeded(String content) {
		if (content.length() > MAX_CHARS) {
			return content.substring(0, MAX_CHARS) + "... [Note truncated due to length]";
		}
		return content;
	}
	
}
