package app.bola.smartnotesai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Smart Notes AI API")
                .version("1.0.0")
                .description("Comprehensive API documentation for Smart Notes AI. Features: notes, folders, users, authentication, and AI-powered endpoints.")
                .contact(new Contact()
                    .name("Smart Notes AI Team")
                    .email("support@smartnotesai.com")
                    .url("https://smartnotesai.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT"))
            );
    }
}
