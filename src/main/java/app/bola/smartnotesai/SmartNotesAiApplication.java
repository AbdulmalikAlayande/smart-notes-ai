package app.bola.smartnotesai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SmartNotesAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartNotesAiApplication.class, args);
    }

}
