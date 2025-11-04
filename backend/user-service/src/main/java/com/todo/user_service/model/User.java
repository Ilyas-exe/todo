package com.todo.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List; // Import java.util.List

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails { // 1. Implement UserDetails

    @Id
    private String id;

    private String email;
    
    private String password;

    // --- UserDetails Methods (Required by Spring Security) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // We'll return an empty list for now. We can add Roles (e.g., "ROLE_ADMIN") later.
        return List.of(); 
    }

    @Override
    public String getUsername() {
        // Spring Security's "username" will be our user's email
        return this.email; 
    }

    @Override
    public String getPassword() {
        // Return the hashed password
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account is always valid
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Account is never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credentials are always valid
    }

    @Override
    public boolean isEnabled() {
        return true; // Account is always enabled
    }
}