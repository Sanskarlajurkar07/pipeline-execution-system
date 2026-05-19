package com.vectorshift.pipeline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS configuration for the application.
 * Allows all origins, methods, and headers for development.
 * In production, restrict to specific origins.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow all origins (for development)
        // In production, replace with specific origins:
        // config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://yourdomain.com"));
        config.addAllowedOriginPattern("*");
        
        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allow all headers
        config.addAllowedHeader("*");
        
        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
