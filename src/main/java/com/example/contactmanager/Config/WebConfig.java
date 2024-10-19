package com.example.contactmanager.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")  // Allow the frontends origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Allow all required methods
                        .allowedHeaders("*")  // Allow all headers
                        .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")  // Expose headers
                        .allowCredentials(true)  // Allow credentials like cookies, authorization headers, etc.
                        .maxAge(3600);  // Cache preflight response for 1 hour
            }
        };
    }
}
