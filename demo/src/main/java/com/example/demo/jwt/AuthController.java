package com.example.demo.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationConfiguration authConfig;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    public record AuthRequest(String username, String password) {
    }

    public record AuthResponse(String token) {
    }

    public record UpdateRequest(String username, String password) {
    }

    public AuthController(AuthenticationConfiguration authConfig,
                          CustomUserDetailsService userDetailsService,
                          JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserRepo userRepo) {
        this.authConfig = authConfig;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest req) {
        if (userRepo.findByUsername(req.username()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username already taken");
        }
        User newUser = new User();
        newUser.setUsername(req.username());
        newUser.setPassword(passwordEncoder.encode(req.password()));
        userRepo.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/user")
    public ResponseEntity<Void> deleteUser(Authentication auth) {
        String current = auth.getName();
        userRepo.findByUsername(current)
                .ifPresent(userRepo::delete);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/user")
    public ResponseEntity<Void> updateUser(@RequestBody UpdateRequest req,
                                           Authentication auth) {
        User u = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User nicht gefunden"));
        if (req.username() != null && !req.username().isBlank()) {
            // optional: check uniquene ss
            if (userRepo.findByUsername(req.username()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            u.setUsername(req.username());
        }
        if (req.password() != null && !req.password().isBlank()) {
            u.setPassword(passwordEncoder.encode(req.password()));
        }
        userRepo.save(u);
        return ResponseEntity.ok().build();
    }
}