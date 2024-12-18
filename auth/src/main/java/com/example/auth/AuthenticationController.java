package com.example.auth;

import io.jsonwebtoken.Claims;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

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
            logger.info("Authenticating user: {}", authenticationRequest.getEmail());
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

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterDTO userRegister) {
        if (!userDataRepository.findByEmail(userRegister.getEmail()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }

        if (!isValidPassword(userRegister.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password does not meet security requirements");
        }

        UserData userData = new UserData();
        userData.setFirstName(userRegister.getFirstName());
        userData.setLastName(userRegister.getLastName());
        userData.setEmail(userRegister.getEmail());
        userData.setPasswordHash(passwordEncoder.encode(userRegister.getPassword()));
        userData.setRole(Roles.USER);

        userDataRepository.save(userData);

        return ResponseEntity.ok("User registered successfully");
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
    @GetMapping("/user/test")
    public ResponseEntity<String> testUser() {
        return ResponseEntity.ok("This is an user endpoint");
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/admin/test")
    public ResponseEntity<String> testAdmin() {
        return ResponseEntity.ok("This is an admin endpoint");
    }

    @GetMapping("/test")
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

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Forwarded-Uri") String requestedEndpoint,
            HttpServletRequest request) {

        logger.info("Handling request: {}", request);

        // Handle preflight request
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            String originHeader = request.getHeader("Origin");
            String methodsHeader = request.getHeader("Access-Control-Request-Methods");
            String headersHeader = request.getHeader("Access-Control-Request-Headers");

            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", originHeader != null ? originHeader : "*")
                    .header("Access-Control-Allow-Methods", methodsHeader != null ? methodsHeader : "GET, POST, OPTIONS")
                    .header("Access-Control-Allow-Headers", headersHeader != null ? headersHeader : "Authorization, Content-Type")
                    .build();
        }

        // Validate JWT Token
        logger.info("Validating token for endpoint: {}", requestedEndpoint);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String userRole;

        try {
            userRole = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        if (requestedEndpoint == null || requestedEndpoint.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid endpoint");
        }

        List<String> requiredRoles = RouteRoles.requiredRolesForPath(requestedEndpoint);

        if (RouteRoles.hasRequiredRole(userRole, requiredRoles)) {
            return ResponseEntity.ok("Access granted");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
    }

    @GetMapping("/user/id")
    public ResponseEntity<Integer> getUserId(@RequestHeader String Authorization) {
        String token = Authorization.substring(7);
        String userEmail = jwtUtil.extractEmail(token);
        Integer id = userDataRepository.findByEmail(userEmail).get(0).getId();
        return ResponseEntity.ok(id);
    }

}

