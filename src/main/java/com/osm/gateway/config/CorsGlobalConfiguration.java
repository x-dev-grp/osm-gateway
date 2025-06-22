package com.osm.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class CorsGlobalConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CorsGlobalConfiguration.class);

    private static final List<String> ALLOWED_ORIGINS = List.of(
            "https://osm-ms-fe.onrender.com",
            "http://localhost:4200"
    );

    /**
     * GlobalFilter bean; @Order tells the gateway to run it late so
     * we can overwrite any header the downstream service might have set.
     */
    @Bean
    @Order(-100)   // LOWEST_PRECEDENCE (-1) – 99  → runs after the response is back
    public GlobalFilter corsGlobalFilter() {

        return (ServerWebExchange exchange, GatewayFilterChain chain) -> {
            String origin = exchange.getRequest().getHeaders().getOrigin();

            /* ---------- 1️⃣  Pre-flight requests handled before routing ---------- */
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS &&
                    origin != null && ALLOWED_ORIGINS.contains(origin)) {

                log.info("[CORS] Pre-flight accepted for {}", origin);
                setCorsHeaders(exchange.getResponse().getHeaders(), origin);
                exchange.getResponse().setStatusCode(HttpStatus.OK);
                return exchange.getResponse().setComplete();
            }

            /* ---------- 2️⃣  Normal request; post-process response headers ---------- */
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
                    HttpHeaders headers = exchange.getResponse().getHeaders();

                    // remove any duplicate value the downstream service added
                    headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                    setCorsHeaders(headers, origin);

                    log.info("[CORS] Response headers fixed for {}", origin);
                }
            }));
        };
    }

    /* Utility that writes **exactly one** set of CORS headers */
    private void setCorsHeaders(HttpHeaders headers, String origin) {
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Authorization,Content-Type");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }
}
