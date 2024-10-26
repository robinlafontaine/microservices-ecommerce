package com.example.gateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> unprotectedURLs = List.of("/authenticate");

    public Predicate<ServerHttpRequest> isSecured = request ->
            unprotectedURLs.stream().noneMatch(uri -> request.getURI().getPath().equals(uri));
}