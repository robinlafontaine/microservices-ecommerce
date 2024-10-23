package com.example.auth;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserExportService userExportService;

    /**
     * Endpoint to authenticate users and provide JWT token.
     *
     * @param authenticationRequest The request body containing username and password.
     * @return A ResponseEntity with the JWT token if authentication is successful.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        try {
            authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
            final String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(token));
        }
        catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials provided");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during authentication");
        }
    }

    /**
     * Method to authenticate the user using the AuthenticationManager.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @throws Exception if authentication fails.
     */
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    /**
     * Test endpoint to verify if JWT is valid and the user is authenticated.
     *
     * @return A simple message indicating the request was successful.
     */
    @GetMapping("/welcome")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome! You are successfully authenticated.");
    }

    /**
     * Endpoint to change the user's password.
     *
     * @param request The request body containing current and new passwords.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        UserData user = userDataRepository.findByEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current password is incorrect");
        }

        if (!isValidPassword(request.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password does not meet security requirements");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userDataRepository.save(user);

        return ResponseEntity.ok("Password changed successfully");
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/export-users")
    @Async
    public CompletableFuture<ResponseEntity<byte[]>> exportUsers() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] csvContent = userExportService.exportUsersToCsv();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "users_export.csv");
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(csvContent);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("Error exporting users: " + e.getMessage()).getBytes());
            }
        });
    }

    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/test-user")
    public ResponseEntity<String> testUser() {
        return ResponseEntity.ok("This is an user endpoint");
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/test-admin")
    public ResponseEntity<String> testAdmin() {
        return ResponseEntity.ok("This is an admin endpoint");
    }

    @GetMapping("/test-both")
    public ResponseEntity<String> testBoth() {
        return ResponseEntity.ok("This is an user & admin endpoint");
    }

    /**
     * Validates the strength of the new password.
     *
     * @param password The new password to validate.
     * @return true if the password meets the criteria, false otherwise.
     */
    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false; // At least one uppercase letter
        return password.matches(".*\\d.*");   // At least one digit
    }
}

