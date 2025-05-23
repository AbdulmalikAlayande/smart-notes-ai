package app.bola.smartnotesai.ai.service.llm.resilience;

import app.bola.smartnotesai.ai.service.llm.provider.LLMProvider;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@Service
public class ResilientLlmService {
	
	private final List<LLMProvider> providers;
	private final HealthTracker healthTracker;
	private final RetryRegistry retryRegistry;
	
	public ResilientLlmService(List<LLMProvider> providers, HealthTracker healthTracker, RetryRegistry retryRegistry) {
		this.providers = providers;
		this.healthTracker = healthTracker;
		this.retryRegistry = retryRegistry;
		
	}
	
	public String generate(String prompt) {
		return generate(prompt, String.class);
	}
	
	public <T> T generate(String contents, Class<T> responseType) {
		List<LLMProvider> healthyProviders = healthTracker.getHealthyProviders(providers);
		
		if (healthyProviders.isEmpty()) {
			log.warn("No healthy providers available, using all providers");
			healthyProviders = providers;
		}
		
		for (LLMProvider provider : healthyProviders) {
			Retry retry = retryRegistry.retry(provider.getName());
			try {
				T response = executeWithRetry(retry, () -> provider.generate(contents, responseType));
				healthTracker.recordSuccess(provider.getName());
				log.info("Successfully generated result using provider: {}", provider.getName());
				return response;
			} catch (Exception e) {
				log.warn("Failed to generate completion using provider: {}", provider.getName(), e);
				healthTracker.recordFailure(provider.getName());
			}
		}
		log.error("All providers failed to generate completion");
		throw new RuntimeException("All providers failed to generate a response");
	}
	
	private <T> T executeWithRetry(Retry retry, Callable<T> supplier) throws Exception {
		return retry.executeCallable(supplier);
	}
}

