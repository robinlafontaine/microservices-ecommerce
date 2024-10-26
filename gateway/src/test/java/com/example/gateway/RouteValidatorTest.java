package com.example.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.server.reactive.ServerHttpRequest;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * @author Maxime Zimmermann
 */
class RouteValidatorTest {

    private RouteValidator routeValidator;

    @BeforeEach
    public void setup() {
        routeValidator = new RouteValidator();
    }

    // Helper method to mock ServerHttpRequest
    private ServerHttpRequest mockRequest(String uri) {
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        when(request.getURI()).thenReturn(URI.create(uri));
        return request;
    }

    @Test
    public void testUnprotectedURL() {
        // Testing for an unprotected URL "/login"
        ServerHttpRequest request = mockRequest("/authenticate");
        boolean result = routeValidator.isSecured.test(request);
        assertFalse(result, "Request to /login should not be secured.");
    }

    @Test
    public void testSecuredURL() {
        // Testing for a secured URL "/dashboard"
        ServerHttpRequest request = mockRequest("/dashboard");
        boolean result = routeValidator.isSecured.test(request);
        assertTrue(result, "Request to /dashboard should be secured.");
    }

    @Test
    public void testPartiallyMatchingURL() {
        // Testing for a URL that partially matches "/login" like "/login-help"
        ServerHttpRequest request = mockRequest("/authenticate-help");
        boolean result = routeValidator.isSecured.test(request);
        assertTrue(result, "Request to /login-help should be secured.");
    }

    @Test
    public void testURLWithQueryParams() {
        // Testing for a URL with query parameters
        ServerHttpRequest request = mockRequest("/authenticate?redirect=/dashboard");
        boolean result = routeValidator.isSecured.test(request);
        assertFalse(result, "Request to /login?redirect=/dashboard should not be secured.");
    }

    @Test
    public void testUnprotectedURLWithSpecialCharacters() {
        // Testing for a URL with special characters in unprotected URL
        ServerHttpRequest request = mockRequest("/authenticate#section");
        boolean result = routeValidator.isSecured.test(request);
        assertFalse(result, "Request to /login#section should not be secured.");
    }

    @Test
    public void testSecuredURLWithSpecialCharacters() {
        // Testing for a secured URL with special characters
        ServerHttpRequest request = mockRequest("/dashboard#section");
        boolean result = routeValidator.isSecured.test(request);
        assertTrue(result, "Request to /dashboard#section should be secured.");
    }

    @Test
    public void testCaseInsensitiveUnprotectedURL() {
        // Testing for case-insensitive match
        ServerHttpRequest request = mockRequest("/autHENticate");
        boolean result = routeValidator.isSecured.test(request);
        assertTrue(result, "Request to /LOGIN should be considered secured (case-sensitive check).");
    }

    @Test
    public void testEmptyPath() {
        // Testing for empty URL path
        ServerHttpRequest request = mockRequest("");
        boolean result = routeValidator.isSecured.test(request);
        assertTrue(result, "Request with an empty path should be secured.");
    }

    @Test
    public void testRootPath() {
        // Testing for root URL "/"
        ServerHttpRequest request = mockRequest("/");
        boolean result = routeValidator.isSecured.test(request);
        assertTrue(result, "Request to the root path '/' should be secured.");
    }
}

