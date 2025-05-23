package app.bola.smartnotesai.ai.service.llm.provider;

public interface LLMProvider {
	
	String getName();
	
	String generate(String prompt);
	<T> T generate(String prompt, Class<T> responseType);
}
