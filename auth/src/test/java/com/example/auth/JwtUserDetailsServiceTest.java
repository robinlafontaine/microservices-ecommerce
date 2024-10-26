package com.example.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class JwtUserDetailsServiceTest {

    @Mock
    private UserDataRepository userDataRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private JwtUserDetailsService jwtUserDetailsService;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }


    @Test
    public void testLoadUserByUsername() {
        String email = "test@example.com";
        String passwordHash = "$2a$10$sfhSifi7YtDTMBBBz5Rb/0k9Vrm2bhrwj82FsMv79HL9B3mHnV9gi";  // Hashed password
        Roles role = Roles.USER;

        UserData mockUserData = new UserData();
        mockUserData.setEmail(email);
        mockUserData.setPasswordHash(passwordHash);
        mockUserData.setRole(role);

        when(userDataRepository.findByEmail(email)).thenReturn(Collections.singletonList(mockUserData));

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo(passwordHash);
    }

    @Test
    public void testLoadUserByUsernameWithUserNotFoundException() {
        String email = "no-user@example.com";

        when(userDataRepository.findByEmail(email)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> jwtUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: " + email);
    }

    @Test
    public void testLoadUserByUsernameWithIllegalArgumentException() {
        String email = "invalid-user@example.com";

        UserData mockUserData = new UserData();
        mockUserData.setEmail(email);
        mockUserData.setPasswordHash(null);
        mockUserData.setRole(null);

        when(userDataRepository.findByEmail(email)).thenReturn(Collections.singletonList(mockUserData));

        assertThatThrownBy(() -> jwtUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid User Data");
    }
}