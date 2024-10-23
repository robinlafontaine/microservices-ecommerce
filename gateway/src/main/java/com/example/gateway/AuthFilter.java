package com.example.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RefreshScope
public class AuthFilter implements GatewayFilter {

    @Autowired
    RouteValidator routeValidator;

    @Autowired
    private JWTUtil jwtUtil;

    @Value("${authentication.enabled}")
    private boolean authEnabled;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getURI().getPath().equals("/authenticate")) {
            return chain.filter(exchange);
        }

        if (!authEnabled) {
            System.out.println("Authentication is disabled. To enable it, set \"authentication.enabled\" property to true");
            return chain.filter(exchange);
        }

        if (routeValidator.isSecured.test(request)) {
            if (this.isCredsMissing(request)) {
                return this.onError(exchange, "Credentials missing", HttpStatus.UNAUTHORIZED);
            }

            String token = this.getAuthHeader(request);

            if (token == null) {
                return this.onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            try {
                jwtUtil.fetchPublicKey();
                Claims claims = jwtUtil.getAllClaims(token);
                String role = claims.get("role", String.class);

                // Get required roles for the path
                List<String> requiredRoles = RouteRoles.requiredRolesForPath(request.getURI().getPath());

                // Check if user has required role
                if (!RouteRoles.hasRequiredRole(role, requiredRoles)) {
                    return this.onError(exchange, 
                        "Forbidden: You don't have the required role (" + role + ")", 
                        HttpStatus.FORBIDDEN);
                }

                if (jwtUtil.isInvalid(token)) {
                    return this.onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
                }

                populateRequestWithHeaders(exchange, token);
            } catch (JwtException | IllegalArgumentException e) {
                return this.onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
            }
        }

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private String getAuthHeader(ServerHttpRequest request) {
        List<String> authHeaders = request.getHeaders().getOrEmpty("Authorization");
        if (authHeaders.isEmpty()) {
            return null; // Or throw an exception if appropriate
        }

        String authHeader = authHeaders.get(0);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Extracts the token after "Bearer "
        } else {
            return null; // Or handle the case where the prefix is missing
        }
    }



    private boolean isCredsMissing(ServerHttpRequest request) {
        return !(request.getHeaders().containsKey("userName") && request.getHeaders().containsKey("role")) && !request.getHeaders().containsKey("Authorization");
    }

    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        Claims claims = jwtUtil.getAllClaims(token);
        exchange.getRequest()
                .mutate()
                .header("id", String.valueOf(claims.get("id")))
                .header("role", String.valueOf(claims.get("role")))
                .build();
    }
}
