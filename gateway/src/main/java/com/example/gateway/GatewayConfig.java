package com.example.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.csrf.CsrfToken;

import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Robin Lafontaine
 */
@Configuration
public class GatewayConfig {

    private final AuthFilter authFilter;

    @Autowired
    private CsrfTokenRepository csrfTokenRepository;

    public GatewayConfig(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/authenticate")
                        .uri("http://auth-service:8082"))
                .route("auth-swagger", r -> r.path("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**")
                        .uri("http://auth-service:8082"))
                .route("change-password", r -> r.path("/change-password")
                        .filters(f -> f.filter(authFilter))
                        .uri("http://auth-service:8082"))
                .route("export-users", r -> r.path("/export-users")
                        .filters(f -> f.filter(authFilter))
                        .uri("http://auth-service:8082"))
                .route("protected-routes", r -> r.path("/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("http://auth-service:8082"))
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:5173")); // Allow only this origin
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    @Bean
    public WebFilter csrfWebFilter() {
        return (exchange, chain) -> {
            Mono<CsrfToken> csrfToken = csrfTokenRepository.loadToken(exchange);
            return csrfToken.flatMap(token -> {
                ServerHttpRequest request = exchange.getRequest().mutate()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken())
                        .build();
                return chain.filter(exchange.mutate().request(request).build());
            });
        };
    }
}