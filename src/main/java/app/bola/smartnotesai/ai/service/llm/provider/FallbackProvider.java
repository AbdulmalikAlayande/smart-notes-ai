package app.bola.smartnotesai.ai.service.llm.provider;

public class FallbackProvider implements LLMProvider {
	
	
	@Override
	public String getName() {
		return "fallback";
	}
	
	@Override
	public String generate(String prompt) {
		return "";
	}
	
	@Override
	public <T> T generate(String prompt, Class<T> responseType) {
		return null;
	}
}
