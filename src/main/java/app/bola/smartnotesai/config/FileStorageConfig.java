package app.bola.smartnotesai.config;

import com.cloudinary.Cloudinary;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Configuration
public class FileStorageConfig implements WebMvcConfigurer {
	
	@Value("${app.cloudinary.cloud-name}")
	private String cloudName;
	@Value("${app.cloudinary.api-key}")
	private String apiKey;
	@Value("${app.cloudinary.api-secret}")
	private String apiSecret;
	@Value("${app.cloudinary.cloudinary-url}")
	private String cloudinaryUrl;
	@Value("${app.file.upload-dir}")
	private String uploadDir;
	
	@Bean
	public Cloudinary cloudinary(){
		
		if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
			
			log.error("Cloudinary URL is not configured, Using Manual Config");
			
			Map<String, String> config = new HashMap<>();
			config.put("cloud_name", cloudName);
			config.put("api_key", apiKey);
			config.put("api_secret",apiSecret);
			
			return new Cloudinary(config);
		}
		
		else return new Cloudinary(cloudinaryUrl);
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String uploadPath = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
		registry.addResourceHandler("/files/**")
				.addResourceLocations(uploadPath)
				.setCachePeriod(31556926);
	}
}
