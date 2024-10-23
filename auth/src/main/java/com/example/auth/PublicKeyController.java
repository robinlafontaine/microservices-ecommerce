package com.example.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;
import java.util.Base64;

@RestController
public class PublicKeyController {

    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/public-key")
    public String getPublicKey() {
        PublicKey publicKey = jwtUtil.getPublicKey();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}