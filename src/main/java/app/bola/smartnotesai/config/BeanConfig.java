package app.bola.smartnotesai.config;

import app.bola.smartnotesai.ai.service.llm.provider.*;
import app.bola.smartnotesai.ai.service.llm.resilience.HealthTracker;
import app.bola.smartnotesai.ai.service.llm.resilience.ResilientLlmService;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.List;

@Slf4j
@Getter
@EnableAsync
@Configuration
public class BeanConfig {
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration()
				.setFieldMatchingEnabled(true)
				.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
				.setMatchingStrategy(MatchingStrategies.STRICT);
		return mapper;
	}
	
	@Bean
	public RetryRegistry retryRegistry(){
		RetryConfig retryConfig = RetryConfig.custom()
				                          .maxAttempts(2)
				                          .waitDuration(Duration.ofMillis(500))
				                          .retryOnException(ex -> {
					                          log.warn("Retrying due to exception: {}", ex.getMessage());
					                          return true;
				                          })
				                          .build();
		return RetryRegistry.of(retryConfig);
	}
	
	@Bean
	public HealthTracker healthTracker(@Value("${app.llm.failure-threshold:4}") int failureThreshold) {
		return new HealthTracker(failureThreshold);
	}
	
	@Bean
	@Order(1)
	@ConditionalOnProperty(name = "spring.ai.grok.api-key")
	public ChatClient grokClient(@Value("${spring.ai.grok.api-key:#{null}}") String apiKey,
	                             @Value("${spring.ai.grok.base-url}") String baseUrl,
	                             @Value("${spring.ai.grok.chat.options.model}") String model,
	                             @Value("${spring.ai.grok.chat.options.temperature}") double temperature) {
		log.info("Creating Grok client with model: {}", model);
		return getChatClient(apiKey, baseUrl, model, temperature);
	}
	
	private ChatClient getChatClient(String apiKey, String baseUrl, String model, double temperature) {
		try {
			var api = OpenAiApi.builder()
					          .apiKey(new SimpleApiKey(apiKey))
					          .baseUrl(baseUrl)
					          .build();
			var chatOptions = OpenAiChatOptions.builder()
					                  .model(model)
					                  .temperature(temperature)
					                  .build();
			var chatModel = OpenAiChatModel.builder()
					                .openAiApi(api)
					                .defaultOptions(chatOptions)
					                .build();
			return ChatClient.create(chatModel);
		} catch (Exception e) {
			log.error("Failed to create chat client for model: {} at {}", model, baseUrl, e);
			throw e;
		}
	}
	
	@Bean
	public ResilientLlmService resilientLlmService(List<LLMProvider> providers, HealthTracker healthTracker, RetryRegistry retryRegistry) {
		log.info("Creating ResilientLlmService with {} providers: {}",
				providers.size(),
				providers.stream().map(LLMProvider::getName).toList());
		return new ResilientLlmService(providers, healthTracker, retryRegistry);
	}
	
	@Bean(name = "taskExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		executor.setThreadNamePrefix("AI-Async-");
		executor.initialize();
		return executor;
	}
	
}