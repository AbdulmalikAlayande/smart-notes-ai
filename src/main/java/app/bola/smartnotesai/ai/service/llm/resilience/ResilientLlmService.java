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
		
		Exception lastException = null;
		
		for (LLMProvider provider : healthyProviders) {
			log.info("Attempting to use provider: {}", provider.getName());
			
			try {
				Retry retry = retryRegistry.retry(provider.getName());
				T response = executeWithRetry(retry, () -> provider.generate(contents, responseType));
				
				log.info("Successfully generated result using provider: {}", provider.getName());
				healthTracker.recordSuccess(provider.getName());
				return response;
				
			} catch (Exception e) {
				lastException = e;
				log.warn("Failed to generate completion using provider: {} - {}",
						provider.getName(), e.getMessage());
				healthTracker.recordFailure(provider.getName());
				log.info("Trying next provider...");
			}
		}
		
		log.error("All {} providers failed to generate completion", healthyProviders.size());
		
		if (lastException != null) {
			throw new RuntimeException("All providers failed to generate a response. Last error: " + lastException.getMessage(), lastException);
		}
		else {
			throw new RuntimeException("All providers failed to generate a response");
		}
	}
	
	private <T> T executeWithRetry(Retry retry, Callable<T> supplier) throws Exception {
		return retry.executeCallable(supplier);
	}
}