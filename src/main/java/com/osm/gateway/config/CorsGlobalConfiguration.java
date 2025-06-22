package com.osm.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
@Configuration
public class CorsGlobalConfiguration {

    @Bean
    public WebFilter corsFilter() {
        return (exchange, chain) -> {
            var request = exchange.getRequest();
            var response = exchange.getResponse();

            if (request.getHeaders().getOrigin() != null &&
                    request.getHeaders().getOrigin().equals("https://osm-ms-fe.onrender.com")) {

                var headers = response.getHeaders();
                headers.set("Access-Control-Allow-Origin", "https://osm-ms-fe.onrender.com");
                headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                headers.set("Access-Control-Allow-Headers", "Authorization, Content-Type");
                headers.set("Access-Control-Allow-Credentials", "true");
            }

            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return response.setComplete();
            }

            return chain.filter(exchange);
        };
    }
}
