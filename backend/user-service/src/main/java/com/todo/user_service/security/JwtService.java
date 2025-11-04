package com.todo.user_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.jwt.secret-key}") // 1. Injects the secret key from application.properties
    private String secretKey;

    // --- Token Generation ---
    
    // Generates a token with default expiration for a user
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generates a token with extra claims and default expiration
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims) // 2. Set any extra data (claims)
                .setSubject(userDetails.getUsername()) // 3. Set the "subject" (who the token is for, e.g., the email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 4. Set when the token was created
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 5. Set expiration (24 hours)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // 6. Sign it with our secret key
                .compact(); // 7. Build the final token string
    }

    // --- Token Validation/Reading ---
    
    // Checks if a token is valid for a given user
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Checks if a token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extracts the expiration date from a token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    // Extracts the username (subject) from a token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Generic method to extract any single piece of data (a "claim") from a token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // --- Helper Methods ---

    // Parses the token and extracts all data ("claims")
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Decodes our secret key into the format jjwt needs
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}