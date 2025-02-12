package es.fempa.acd.redsocialpawzy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Configuration class for Spring MVC.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Adds resource handlers for serving static resources.
     *
     * @param registry the ResourceHandlerRegistry object
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = Paths.get("uploads").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**").addResourceLocations(uploadDir);
    }
}