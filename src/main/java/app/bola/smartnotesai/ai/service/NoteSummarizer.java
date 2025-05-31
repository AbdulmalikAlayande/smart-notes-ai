package app.bola.smartnotesai.ai.service;

import app.bola.smartnotesai.ai.dto.NoteSummarizerResponse;
import app.bola.smartnotesai.ai.service.llm.resilience.ResilientLlmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class NoteSummarizer {

	final PromptTemplate summarizePrompt;
	final ResilientLlmService llmService;
	private static final int MAX_CHARS = 10000;
	
	/**
	 * Constructs a NoteSummarizer with the specified LLM service and prompt template.
	 *
	 * @param llmService the resilient LLM service used for generating summaries
	 * @param summarizePromptResource the class path resource containing the prompt template for summarization
	 */
	public NoteSummarizer(ResilientLlmService llmService,
	                      @Value("prompts/summarize-note.st")
	                      ClassPathResource summarizePromptResource) {
		
		this.llmService = llmService;
		this.summarizePrompt = new PromptTemplate(summarizePromptResource);
	}
	
	/**
	 * Asynchronously generates a summary for the given note content.
	 *
	 * @param noteContent the content of the note to be summarized
	 * @return a CompletableFuture containing the NoteSummarizerResponse with the summary, tags, and key points
	 */
	@Async(value = "taskExecutor")
	public CompletableFuture<NoteSummarizerResponse> generateSummaryAsync(String noteContent){
		return CompletableFuture.supplyAsync(() -> generateSummary(noteContent));
	}
	
	/**
	 * Generates a summary for the given note content.
	 *
	 * @param noteContent the content of the note to be summarized
	 * @return a NoteSummarizerResponse containing the summary, tags, and key points
	 */
	public NoteSummarizerResponse generateSummary(String noteContent){
		String processedNote = truncateIfNeeded(noteContent);
		try {
			Prompt prompt = summarizePrompt.create(Map.of("note", processedNote));
			NoteSummarizerResponse summarizedNote = llmService.generate(prompt.getContents(), NoteSummarizerResponse.class);
			log.info("Summarized note: {}", summarizedNote);
			return summarizedNote;
		} catch (Exception exception) {
			log.error("Failed to summarize note {}", String.valueOf(exception));
			return createFallbackResponse();
		}
		
	}
	
	/**
	 * Creates a fallback response in case of failure to generate a summary.
	 *
	 * @return a NoteSummarizerResponse with a default error message and tags
	 */
	private NoteSummarizerResponse createFallbackResponse() {
		return NoteSummarizerResponse.builder()
				       .summary("Unable to generate summary")
				       .tags(List.of("error"))
				       .keyPoints(List.of("Summary generation failed"))
				       .build();
	}
	
	/**
	 * Truncates the note content if it exceeds the maximum character limit.
	 *
	 * @param content the content to be truncated
	 * @return the truncated content with a note if it was truncated
	 */
	private String truncateIfNeeded(String content) {
		if (content.length() > MAX_CHARS) {
			return content.substring(0, MAX_CHARS) + "... [Note truncated due to length]";
		}
		return content;
	}
	
}
