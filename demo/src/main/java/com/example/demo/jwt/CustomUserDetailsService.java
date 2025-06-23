package com.example.demo.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.demo.jwt.User userEntity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User nicht gefunden: " + username));

        return User.withUsername(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities("ROLE_USER")
                .build();
    }
}