package com.example.letranthainguu.backend.security;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.letranthainguu.backend.config.AppConstants;
import com.example.letranthainguu.backend.service.impl.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request,
            @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        
        System.out.println("JWTFilter processing request: " + requestURI);
        System.out.println("Auth header: " + authHeader);
        
        // Skip token validation for public URLs and error endpoint
        if (isPublicUrl(requestURI)) {
            System.out.println("Public URL detected, skipping token validation");
            filterChain.doFilter(request, response);
            return;
        }
        
        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (jwt == null || jwt.isBlank()) {
                System.out.println("Invalid JWT token: Token is null or blank");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT token in Bearer Header");
                return;
            }
            try {
                String email = jwtUtil.validateTokenAndRetrieveSubject(jwt);
                System.out.println("Validated email from token: " + email);
                
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
                System.out.println("User authorities: " + userDetails.getAuthorities());
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Authentication set in SecurityContext");
                
            } catch (JWTVerificationException e) {
                System.out.println("JWT Verification failed: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token");
                return;
            }
        } else {
            System.out.println("No valid Authorization header found");
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicUrl(String requestURI) {
        for (String publicUrl : AppConstants.PUBLIC_URLS) {
            if (publicUrl.endsWith("/**")) {
                String baseUrl = publicUrl.substring(0, publicUrl.length() - 3);
                if (requestURI.startsWith(baseUrl)) {
                    return true;
                }
            } else if (requestURI.equals(publicUrl)) {
                return true;
            }
        }
        return requestURI.equals("/error");
    }
}
