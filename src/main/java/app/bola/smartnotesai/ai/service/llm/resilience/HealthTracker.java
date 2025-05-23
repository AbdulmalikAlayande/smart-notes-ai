package app.bola.smartnotesai.ai.service.llm.resilience;

import app.bola.smartnotesai.ai.service.llm.provider.LLMProvider;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
		return providers.stream()
				        .filter(provider -> {
					        ProviderHealth health = healthMap.get(provider.getName());
							return health != null && health.getConsecutiveFailures() >= failureThreshold;
				        })
				       .toList();
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
