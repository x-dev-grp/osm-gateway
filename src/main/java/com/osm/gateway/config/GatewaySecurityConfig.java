package com.osm.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class GatewaySecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(GatewaySecurityConfig.class);

    // +++ CORS Configuration Switch +++
    // Set to 'true' to enable CORS, 'false' to disable it.
    private static final boolean CORS_ENABLED = true;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;
    String[] PERMITTED_ENDPOINTS = {
            "/api/security/user/auth/**",
            "/oauth2/**",
            "/jwks",
            "/.well-known/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**"
    };

    /**
     * Configures the primary security settings for the API Gateway.
     *
     * <p><b>CORS Configuration Switch:</b></p>
     * <p>
     * To easily enable or disable CORS, modify the {@code .cors()} configuration below.
     * </p>
     * <ul>
     *   <li><b>To ENABLE CORS:</b> Use {@code .cors(cors -> cors.configurationSource(corsConfigurationSource()))}</li>
     *   <li><b>To DISABLE CORS:</b> Use {@code .cors(ServerHttpSecurity.CorsSpec::disable)}</li>
     * </ul>
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                );

        // Apply CORS configuration based on the flag
        if (CORS_ENABLED) {
            enableCors(http);
        } else {
            disableCors(http);
        }

        return http.build();
    }

    /**
     * Applies the 'enabled' CORS policy to the HttpSecurity chain.
     */
    private void enableCors(ServerHttpSecurity http) {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
    }

    /**
     * Applies the 'disabled' CORS policy to the HttpSecurity chain.
     */
    private void disableCors(ServerHttpSecurity http) {
        http.cors(ServerHttpSecurity.CorsSpec::disable);
    }

    /**
     * Creates the CORS configuration source, defining the allowed origins, methods, and headers.
     * This bean is then used by the security filter chain.
     *
     * @return The configured {@link CorsConfigurationSource}.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(
                "https://osm-ms-fe.onrender.com",   // production frontend
                "http://localhost:4200"              // local Angular dev
        ));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cors.setAllowCredentials(true);
      //      cors.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);   // apply to every path
        return source;
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}
