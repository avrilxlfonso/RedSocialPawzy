package es.fempa.acd.redsocialpawzy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the RedSocialPawzy application.
 * Configures and starts the Spring Boot application.
 */
@OpenAPIDefinition(
        info = @Info(title = "API de Pawzy", version = "1.0", description = "Documentación de la API de Pawzy")
)
@SpringBootApplication
public class RedSocialPawzyApplication {

    /**
     * Main method to run the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RedSocialPawzyApplication.class, args);
    }

}