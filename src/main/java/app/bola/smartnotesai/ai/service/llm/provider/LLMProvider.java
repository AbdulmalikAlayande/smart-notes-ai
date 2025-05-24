package app.bola.smartnotesai.ai.service.llm.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface LLMProvider {
	Logger logger = LoggerFactory.getLogger(LLMProvider.class);
	
	String getName();
	
	String generate(String prompt);
	<T> T generate(String prompt, Class<T> responseType);
	
	default void logMessage(String llmName, Object message) {
		logger.info("Generated Content With LLM Provider: {} {}", llmName, message);
	}
}
