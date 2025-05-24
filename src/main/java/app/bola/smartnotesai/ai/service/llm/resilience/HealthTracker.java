package app.bola.smartnotesai.ai.service.llm.resilience;

import app.bola.smartnotesai.ai.service.llm.provider.LLMProvider;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HealthTracker {
	
	private final Map<String, ProviderHealth> healthMap = new ConcurrentHashMap<>();
	private final int failureThreshold;
	
	public HealthTracker(int failureThreshold) {
		this.failureThreshold = failureThreshold;
	}
	
	public void recordSuccess(String providerName) {
		healthMap.computeIfAbsent(providerName, k->new ProviderHealth())
				 .recordSuccess();
	}
	
	public void recordFailure(String providerName) {
		healthMap.computeIfAbsent(providerName, k->new ProviderHealth())
				 .recordFailure();
	}
	
	public List<LLMProvider> getHealthyProviders(List<LLMProvider> providers) {
		List<LLMProvider> healthyProviders = new ArrayList<>();
		for (LLMProvider provider : providers) {
			ProviderHealth health = healthMap.get(provider.getName());
			if (health == null) {
				healthyProviders.add(provider);
			}
			else {
				boolean isHealthy = health.getConsecutiveFailures() < failureThreshold;
				log.debug("Provider {} health check: consecutive failures = {}, threshold = {}, healthy = {}",
						provider.getName(), health.getConsecutiveFailures(), failureThreshold, isHealthy);
				if (isHealthy) {
					healthyProviders.add(provider);
				}
			}
		}

		log.info("Found {} healthy providers out of {} total providers", healthyProviders.size(), providers.size());
		return healthyProviders;
	}
	@Getter
	private static class ProviderHealth {
		
		private int consecutiveFailures = 0;
		
		public void recordSuccess() {
			consecutiveFailures = 0;
		}
		
		public void recordFailure() {
			consecutiveFailures++;
		}
		
	}
}
