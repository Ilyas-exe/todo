package com.todo.user_service.controller;

import com.todo.user_service.dto.AuthenticationResponse; // 1. ADDED
import com.todo.user_service.dto.LoginRequest; // 2. ADDED
import com.todo.user_service.model.User;
import com.todo.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- API Endpoint for User Story 1 (Registration) ---
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- API Endpoint for User Story 2 (Login) ---
    @PostMapping("/login") // 3. ADDED
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            AuthenticationResponse response = userService.login(loginRequest);
            // 4. Success: Return 200 OK with the token
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 5. Failure: Return 401 Unauthorized
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }

    // --- API Endpoint for User Story 3 (View Profile) ---
    @GetMapping("/me") // 1. Maps to GET /api/users/me
    public ResponseEntity<?> getMyProfile(Authentication authentication) { // 2.
        // 3. Spring Security will automatically inject the "Authentication" object
        // for the user who is logged in (thanks to our JWT filter!)
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("No user authenticated", HttpStatus.UNAUTHORIZED);
        }

        // 4. The "principal" of the authentication is the UserDetails object we created
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 5. Return the user's details
        return ResponseEntity.ok(userDetails);
    }
}