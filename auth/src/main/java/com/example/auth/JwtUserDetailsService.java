package com.example.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * @author Robin Lafontaine
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDataRepository userDataRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserData> optionalUserData = userDataRepository.findByEmail(email).stream().findFirst();

        if (optionalUserData.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        UserData userData = optionalUserData.get();

        if (userData.getPasswordHash() == null || userData.getRole() == null) {
            throw new IllegalArgumentException("Invalid User Data");
        }

        return new User(email, userData.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority(userData.getRole().name())));
    }
}