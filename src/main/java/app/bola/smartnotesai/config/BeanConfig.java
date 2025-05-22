package app.bola.smartnotesai.config;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class BeanConfig {
	
	@Value("${openai.api.url}")
	private String apiUrl;
	
	@Value("${spring.ai.openai.api-key}")
	private String apiKey;
	
	@Value("${spring.ai.openai.chat.options.model}")
	private String model;
	
	@Value("${spring.ai.openai.chat.options.temperature}")
	private double temperature;
	
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
	public ChatClient chatClient(OpenAiChatModel chatModel) {
		return ChatClient.create(chatModel);
	}
}
