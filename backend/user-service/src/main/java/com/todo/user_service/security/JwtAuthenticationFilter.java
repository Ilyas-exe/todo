package com.todo.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull; // Make sure to import this
import lombok.RequiredArgsConstructor; // And this
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // 1. Tells Spring to manage this as a Bean
@RequiredArgsConstructor // 2. Lombok: Creates a constructor with all 'final' fields
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 3. Runs ONCE per request

    private final JwtService jwtService; // 4. DI via Lombok
    private final UserDetailsService userDetailsService; // 5. DI via Lombok

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // 6. Get the 'Authorization' header
        final String jwt;
        final String userEmail;

        // 7. If no header, or header doesn't start with "Bearer ", pass to next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // 8. Extract the token (everything after "Bearer ")
        userEmail = jwtService.extractUsername(jwt); // 9. Extract the email from the token

        // 10. If we have an email AND the user is not already authenticated...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 11. Load the user from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 12. Check if the token is valid
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 13. If valid, create an authentication token...
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // 14. ...and set it in the SecurityContext. This "authenticates" the user.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 15. Pass the request to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}