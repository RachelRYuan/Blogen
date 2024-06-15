package com.blogen.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI (Swagger) and swagger-ui to document the REST API.
 * <p>
 * This configuration sets up the OpenAPI documentation with support for JWT Bearer tokens in the Authorization header.
 * </p>
 * <p>
 * To disable authentication for a particular endpoint, annotate the controller method with @SecurityRequirements.
 * </p>
 * Author: Cliff
 */
@Configuration
public class SwaggerConfig {

    /**
     * Creates and configures the OpenAPI instance for the Blogen API.
     *
     * @return the configured OpenAPI instance
     */
    @Bean
    public OpenAPI blogenOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Blogen API")
                        .description("Blogen sample application")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://github.com/strohs")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("jwt")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}