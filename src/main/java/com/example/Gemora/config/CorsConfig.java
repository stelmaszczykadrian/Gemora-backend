package com.example.Gemora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        long maxAgeSecs = 3600;
        config.setMaxAge(maxAgeSecs);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }




//    @Bean
//    public CorsFilter corsFilter() {
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//
//        final CorsConfiguration config1 = new CorsConfiguration();
//        config1.setAllowCredentials(true);
//        config1.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
//        config1.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));
//        config1.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
//        long maxAgeSecs = 3600;
//        config1.setMaxAge(maxAgeSecs);
//
//        source.registerCorsConfiguration("http://localhost:3000/**", config1); // Dla pierwszego URL
//
//        final CorsConfiguration config2 = new CorsConfiguration();
//        config2.setAllowCredentials(true);
//        config2.setAllowedOrigins(Collections.singletonList("http://localhost:3006"));
//        config2.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));
//        config2.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
//        config2.setMaxAge(maxAgeSecs);
//
//        source.registerCorsConfiguration("http://localhost:3006/**", config2); // Dla drugiego URL
//
//        return new CorsFilter(source);
//    }
}
