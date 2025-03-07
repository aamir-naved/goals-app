package com.accountability.accountability_app.config;

import com.accountability.accountability_app.security.JwtFilter;
import com.accountability.accountability_app.service.CustomUserDetailsService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Configuring SecurityFilterChain...");
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/login", "/report-issue/**").permitAll() // Public endpoints
                        .requestMatchers("/api/messages/stream/**").permitAll()  // Allow SSE without auth (if needed)
                        .anyRequest().authenticated() // Protect all other endpoints
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        System.out.println("SecurityFilterChain configured successfully.");
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("Initializing PasswordEncoder...");
        return new BCryptPasswordEncoder(); // Secure password hashing
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        System.out.println("Initializing AuthenticationManager...");
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        System.out.println("Initializing AuthenticationProvider...");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        System.out.println("Configuring CORS...");


        String allowedOrigins = getAllowedOrigins();

        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            allowedOrigins = "*"; // Default to allow all for testing
        }

        // Split allowed origins from .env
        List<String> allowedOriginsList = Arrays.asList(allowedOrigins.split(","));

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOriginsList);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // REQUIRED for auth cookies/tokens
        System.out.println("CORS configured successfully.");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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