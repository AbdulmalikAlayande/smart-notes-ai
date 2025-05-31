package app.bola.smartnotesai.ai.service;

import app.bola.smartnotesai.ai.dto.TagGeneratorResponse;
import app.bola.smartnotesai.ai.service.llm.resilience.ResilientLlmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class TagGenerator {
	
	final PromptTemplate promptTemplate;
	final ResilientLlmService llmService;
	private static final int MAX_CHARS = 10000;
	
	/**
	 * Constructs a TagGenerator with the specified LLM service and prompt template.
	 *
	 * @param llmService the resilient LLM service used for generating tags
	 * @param generateTagsPrompt the class path resource containing the prompt template for tag generation
	 */
	public TagGenerator(ResilientLlmService llmService,
	                     @Value("prompts/generate-tags.st") ClassPathResource generateTagsPrompt) {
		this.llmService = llmService;
		this.promptTemplate = new PromptTemplate(generateTagsPrompt);
	}
	
	/**
	 * Generates tags for a given note content.
	 *
	 * @param noteContent the content of the note for which tags are to be generated
	 * @return a set of tags generated from the note content
	 */
	public TagGeneratorResponse generateTags(String noteContent) {
		try {
			String processedNote = truncateIfNeeded(noteContent);
			Prompt prompt = promptTemplate.create(Map.of("note", processedNote));
			TagGeneratorResponse response = llmService.generate(prompt.getContents(), TagGeneratorResponse.class);
			log.info("Generated tags: {}", response.getTags());
			return response;
		}catch (Exception exception) {
			log.error("Failed to generate tags: {}", exception.getMessage(), exception);
			return createFallbackResponse();
		}
	}
	
	/**
	 * Creates a fallback response in case of an error during tag generation.
	 *
	 * @return a TagGeneratorResponse with default tags and sentiment
	 */
	private TagGeneratorResponse createFallbackResponse() {
		return TagGeneratorResponse.builder()
				       .tags(Set.of("error", "tag-generation-failed"))
				       .sentiment("unknown")
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
