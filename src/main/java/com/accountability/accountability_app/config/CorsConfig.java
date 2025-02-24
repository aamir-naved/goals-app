package com.accountability.accountability_app.config;

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

                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:5174", // Local frontend
                                "https://goals-frontend-6lanygqz4-aamir-naveds-projects.vercel.app",
                                "https://goals-frontend-tau.vercel.app"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true); // Required for authentication headers

                System.out.println("✅ CORS Configuration Applied Successfully!");
            }
        };
    }
}
