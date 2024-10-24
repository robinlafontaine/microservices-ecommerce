package com.example.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class GatewayConfig {
    private final AuthFilter authFilter;

    public GatewayConfig(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/authenticate")
                        .uri("lb://auth-service"))
                .route("auth-swagger", r -> r.path("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**")
                        .uri("lb://auth-service"))
                .route("change-password", r -> r.path("/change-password")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://auth-service"))
                .route("export-users", r -> r.path("/export-users")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://auth-service"))
                .route("protected-routes", r -> r.path("/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://auth-service"))
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://inventory-service"))
                .route("payment-service", r -> r.path("/api/payments/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://payment-service"))
                .route("order-service", r -> r.path("/api/orders/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://order-service"))
                .route("minio", r -> r.path("/product-images/**")
                        .uri("http://minio:9000"))
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}