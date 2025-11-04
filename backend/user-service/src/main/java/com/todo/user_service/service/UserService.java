package com.todo.user_service.service;

import com.todo.user_service.dto.AuthenticationResponse;
import com.todo.user_service.dto.LoginRequest;
import com.todo.user_service.model.User;
import com.todo.user_service.repository.UserRepository;
import com.todo.user_service.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    // --- Dependencies ---
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // 1. ADDED
    private final AuthenticationManager authenticationManager; // 2. ADDED

    // --- Dependency Injection ---
    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService, // 3. ADDED
                       AuthenticationManager authenticationManager) { // 4. ADDED
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // --- Business Logic for User Story 1 (Registration) ---
    public User registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    // --- Business Logic for User Story 2 (Login) ---
    public AuthenticationResponse login(LoginRequest request) {
        // 5. This is the "magic"
        //    The AuthenticationManager will use our UserDetailsService and PasswordEncoder
        //    to check if the email and password are correct.
        //    If they are wrong, it will throw an exception.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 6. If we get here, the user is authenticated.
        //    We find the user (we know they exist) and generate a token.
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));
        
        String jwtToken = jwtService.generateToken(user);

        // 7. Return the response DTO containing the token
        return new AuthenticationResponse(jwtToken);
    }
}