package com.example.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtUserDetailsService userDetailsService;

	@Mock
	private JWTUtil jwtUtil;

	@Mock
	private UserDataRepository userDataRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthenticationController authenticationController;

	private AutoCloseable closeable;

	@BeforeEach
	public void setupMocks() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testChangePasswordSuccess() {
		// Setup mocks
		Authentication auth = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(auth.getName()).thenReturn("admin@example.com");
		when(securityContext.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(securityContext);

		UserData user = new UserData();
		user.setEmail("admin@example.com");
		user.setPasswordHash(passwordEncoder.encode("oldPassword"));
		user.setRole(Roles.ADMIN);

		when(userDataRepository.findByEmail("admin@example.com")).thenReturn(List.of(user));
		when(passwordEncoder.matches("oldPassword", user.getPasswordHash())).thenReturn(true);
		when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");

		ChangePasswordRequest request = new ChangePasswordRequest();
		request.setCurrentPassword("oldPassword");
		request.setNewPassword("newPassword!");

		ResponseEntity<?> response = authenticationController.changePassword(request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("Password changed successfully");
		verify(userDataRepository, times(1)).save(user);
	}
}
