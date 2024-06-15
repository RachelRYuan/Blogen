package com.blogen.config;

import com.blogen.services.security.RestApiAccessDeniedHandler;
import com.blogen.services.security.RestApiAuthenticationEntryPoint;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Blogen Security Configuration.
 * <p>
 * Configures security for the application, supporting OAuth2 login, JWT authentication, and ignoring certain static resources.
 * </p>
 * <p>
 * Features:
 * - User login with username and password
 * - JWT authentication for REST API endpoints
 * - OAuth2 login server
 * - OAuth2 resource server for creating, accepting, and validating JWTs
 * - Ignoring static resource paths for the frontend client
 * </p>
 * <p>
 * After a successful login, any JWT tokens from an OAuth2 provider are converted into custom JWTs by copying relevant claims.
 * </p>
 * Author: Cliff
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecConfig extends WebSecurityConfigurerAdapter {

    @Value("${jwt.public.key}")
    private RSAPublicKey key;

    @Value("${jwt.private.key}")
    private RSAPrivateKey priv;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.key).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.key).privateKey(this.priv).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(
                        "/v3/api-docs/**",
                        "/swagger-resources/configuration/ui",
                        "/swagger-resources",
                        "/swagger-resources/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/console/*",
                        "/h2-console/**",
                        "/actuator/**",
                        "/favicon.ico",
                        "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg",
                        "/**/*.html", "/**/*.css", "/**/*.js", "/**/*.map"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf(AbstractHttpConfigurer::disable)
            .logout(configurer -> configurer.logoutSuccessUrl("/"))
            .authorizeHttpRequests(authorize -> authorize
                    .antMatchers("/", "/api/v1/auth/**", "/login/form").permitAll()
                    .antMatchers("/api/**").hasAuthority("SCOPE_ROLE_API")
                    .anyRequest().authenticated()
            )
            .oauth2Login(c -> c.defaultSuccessUrl("/login/oauth2/success", true))
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(new RestApiAuthenticationEntryPoint())
                    .accessDeniedHandler(new RestApiAccessDeniedHandler())
            );
    }
}