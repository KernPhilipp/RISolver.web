package com.example.demo.jwt;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationConfiguration authConfig;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public record AuthRequest(String username, String password) {
    }

    public record AuthResponse(String token) {
    }

    public AuthController(AuthenticationConfiguration authConfig,
                          CustomUserDetailsService userDetailsService,
                          JwtUtil jwtUtil) {
        this.authConfig = authConfig;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) throws Exception {
        AuthenticationManager authManager = authConfig.getAuthenticationManager();
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.username());
        String token = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(token));
    }
}