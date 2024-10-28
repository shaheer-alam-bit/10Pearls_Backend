package com.example.contactmanager.Config;

import com.example.contactmanager.CustomExceptions.InvalidJWTAuthenticationException;
import com.example.contactmanager.Services.CustomUserDetailsService;
import com.example.contactmanager.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter
{

    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;

    public JwtFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService)
    {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Get the current request path
        String requestPath = request.getServletPath();

        // Skip token validation for login and signup paths
        if (requestPath.equals("/login") || requestPath.equals("/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userEmail = null;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidJWTAuthenticationException("JWT Token is missing or invalid");
        }
        try {
            token = authHeader.substring(7);
            userEmail = jwtService.extractEmail(token);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                               null,null);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
