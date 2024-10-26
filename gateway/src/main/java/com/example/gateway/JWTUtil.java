package com.example.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTUtil {

    private PublicKey publicKey;
    private final RestTemplate restTemplate;

    public JWTUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches the public key from the authentication service.
     */
    public void fetchPublicKey() {
        try {
            String publicKeyString = restTemplate.getForObject("http://auth-service:8082/public-key", String.class);
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch public key", e);
        }
    }

    /**
     * Parses the JWT token and returns all claims.
     *
     * @param token JWT token string
     * @return Claims object containing all JWT claims
     */
    public Claims getAllClaims(String token) throws JwtException {
        if (publicKey == null) {
            fetchPublicKey();
        }
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            // Token is invalid, consider it expired
            return true;
        }
    }

    public boolean isInvalid(String token) {
        return isTokenExpired(token);
    }
}
