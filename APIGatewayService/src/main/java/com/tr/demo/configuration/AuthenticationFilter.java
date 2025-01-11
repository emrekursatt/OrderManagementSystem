package com.tr.demo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tr.demo.configuration.properties.JwtProperties;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecretKey secretKey;

    private static final List<String> SWAGGER_WHITELIST = List.of(
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/webjars",
            "/swagger-resources",
            "/swagger-resources/configuration/ui",
            "/swagger-resources/configuration/security",
            "/actuator"
    );

    public AuthenticationFilter(RedisTemplate<String, String> redisTemplate, JwtProperties jwtProperties) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getJwtSecret().getBytes());
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();


            // Whitelist edilen rotalar için doğrulama atlanır
            if (path.startsWith("/customer") || isSwaggerPath(path)) {
                return chain.filter(exchange);
            }
            // Authorization header'ını al ve doğrula
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (token == null || token.isEmpty()) {
                return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
            } else {
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                } else {
                    return this.onError(exchange, "Invalid token format", HttpStatus.UNAUTHORIZED);
                }
            }
            // Token doğrulama işlemini Customer Service'e delege et
            return validateTokenWithAuthService(token, exchange)
                    .then(chain.filter(exchange)); // Doğrulama başarılı ise request'i devam ettir
        };
    }

    private Mono<Void> validateTokenWithAuthService(String token, ServerWebExchange exchange) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost/customer/validate") // Doğru endpoint
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("token", token).build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .flatMap(isValid -> {
                    if (!isValid) {
                        log.error("Invalid token detected for token: {}", token);
                        return this.onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                    }
                    log.info("Token is valid.");
                    return Mono.empty();
                })
                .timeout(Duration.ofSeconds(5)) // Timeout ekliyoruz
                .onErrorResume(e -> {
                    log.error("Error occurred while calling Customer Service: {}", e.getMessage());
                    return this.onError(exchange, "Customer service error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    private boolean isSwaggerPath(String path) {
        return SWAGGER_WHITELIST.stream().anyMatch(path::contains);
    }


    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        String errorMessage = String.format("{\"error\": \"%s\"}", err);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }


    public static class Config {}

}
