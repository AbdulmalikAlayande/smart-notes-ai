package app.bola.smartnotesai.ai.service.llm.provider;

import app.bola.smartnotesai.ai.dto.NoteSummarizerResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class FallbackProvider implements LLMProvider {


	@Override
	public String getName() {
		return "fallback";
	}

	@Override
	public String generate(String prompt) {
		log.warn("Using fallback provider for text generation");
		return "I apologize, but I'm currently unable to process your request. Please try again later.";
	}

	public <T> T generate(String prompt, Class<T> responseType) {
		log.warn("Using fallback provider for structured generation of type: {}", responseType.getSimpleName());

		try {
			if (responseType == NoteSummarizerResponse.class) {
				return responseType.cast(NoteSummarizerResponse.builder()
						                         .summary("Unable to generate summary at this time. Please try again later.")
						                         .keyPoints(Arrays.asList(
								                         "AI service temporarily unavailable",
								                         "Summary generation failed",
								                         "Please retry your request"
						                         ))
						                         .tags(Arrays.asList("error", "fallback", "unavailable"))
						                         .sentiment("neutral")
						                         .build());
			}

			if (responseType == String.class) {
				return responseType.cast("Fallback response: Service temporarily unavailable");
			}
			log.error("Cannot create fallback response for type: {}. Returning null.", responseType.getSimpleName());
			return null;

		} catch (Exception e) {
			log.error("Error in fallback provider while generating response of type: {}", responseType.getSimpleName(), e);
			return null;
		}
	}

}
