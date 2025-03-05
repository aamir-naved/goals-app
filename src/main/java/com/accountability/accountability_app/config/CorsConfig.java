package com.accountability.accountability_app.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                System.out.println("🔧 Configuring CORS..."); // Logging

                System.out.println("Triggering Dynamic Allowed Origins for CORS from .env file");

                // Read ALLOWED_ORIGINS variable
                String allowedOrigins = getAllowedOrigins();

                if (allowedOrigins == null || allowedOrigins.isEmpty()) {
                    allowedOrigins = "*"; // Default to allow all (only for testing)
                }

                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins.split(",")) // Supports multiple origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true); // Required for authentication headers

                System.out.println("✅ CORS Configuration Applied Successfully!");
            }
        };
    }

    public static String getAllowedOrigins() {
        // First, try to get from system environment (works on Railway)
        String allowedOrigins = System.getenv("ALLOWED_ORIGINS");

        // If running locally, load from .env file
        if (allowedOrigins == null) {
            System.out.println("Env Variables not provided ALLOWED_ORIGINS");
            System.out.println("Trying to load from .env file");
            Dotenv dotenv = Dotenv.load();
            allowedOrigins = dotenv.get("ALLOWED_ORIGINS");
            System.out.println(".env file loaded");
        }

        System.out.println("Loaded ALLOWED_ORIGINS: " + allowedOrigins);
        return allowedOrigins;
    }
}
