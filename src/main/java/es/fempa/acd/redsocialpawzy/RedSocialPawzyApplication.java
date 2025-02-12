package es.fempa.acd.redsocialpawzy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(title = "API de Pawzy", version = "1.0", description = "Documentación de la API de Pawzy")
)
@SpringBootApplication
public class RedSocialPawzyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedSocialPawzyApplication.class, args);
	}

}
