package app.bola.smartnotesai;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@EnableJpaAuditing
@SpringBootApplication
public class SmartNotesAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartNotesAiApplication.class, args);
    }

}
