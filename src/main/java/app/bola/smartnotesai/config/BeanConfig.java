package app.bola.smartnotesai.config;

import app.bola.smartnotesai.ai.service.llm.provider.*;
import app.bola.smartnotesai.ai.service.llm.resilience.HealthTracker;
import app.bola.smartnotesai.ai.service.llm.resilience.ResilientLlmService;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.List;

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
				                          .waitDuration(Duration.ofMillis(200))
				                          .build();
		return RetryRegistry.of(retryConfig);
	}
	
	@Bean
	public HealthTracker healthTracker(@Value("${app.llm.failure-threshold:3}") int failureThreshold) {
		return new HealthTracker(failureThreshold);
	}
	
	@Bean
	@Order(1)
	public ChatClient grokClient(@Value("${spring.ai.grok.api-key:#{null}}") String apiKey,
	                                @Value("${spring.ai.grok.base-url}") String baseUrl,
	                                @Value("${spring.ai.grok.chat.options.model}") String model,
	                                @Value("${spring.ai.grok.chat.options.temperature}") double temperature) {
		return getChatClient(apiKey, baseUrl, model, temperature);
	}
	
	@Bean
	@Order(2)
	public ChatClient deepSeekClient(@Value("${spring.ai.deepseek.api-key:#{null}}") String apiKey,
	                                 @Value("${spring.ai.deepseek.base-url}") String baseUrl,
	                                 @Value("${spring.ai.deepseek.chat.options.model}") String model,
	                                 @Value("${spring.ai.deepseek.chat.options.temperature}") double temperature) {
		return getChatClient(apiKey, baseUrl, model, temperature);
	}
	
	private ChatClient getChatClient(String apiKey, String baseUrl, String model, double temperature) {
		var grokApi = OpenAiApi.builder()
				                .apiKey(new SimpleApiKey(apiKey))
				                .baseUrl(baseUrl)
				                .build();
		var chatOptions = OpenAiChatOptions.builder()
				                                .model(model)
				                                .temperature(temperature)
				                                .build();
		var chatModel = OpenAiChatModel.builder()
				                            .openAiApi(grokApi)
				                            .defaultOptions(chatOptions)
				                            .build();
		return ChatClient.create(chatModel);
	}
	
	@Bean
	public ResilientLlmService resilientLlmService(List<LLMProvider> providers, HealthTracker healthTracker, RetryRegistry retryRegistry) {
		return new ResilientLlmService(providers, healthTracker, retryRegistry);
	}
	
	@Bean(name = "taskExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(50);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("Async-");
		executor.initialize();
		return executor;
	}
	
}
