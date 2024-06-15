package com.blogen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration to enable cross-origin requests globally.
 * <p>
 * This is typically needed by JavaScript single-page applications that run on their own separate development servers.
 * </p>
 * Author: Cliff
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final long MAX_AGE_SECS = 3600;

    /**
     * Configure CORS mappings.
     *
     * @param registry the {@link CorsRegistry} to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*localhost*", "https://play.google.com/**")
                .allowCredentials(true)
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
                .exposedHeaders("Authorization")
                .maxAge(MAX_AGE_SECS);
    }
}
