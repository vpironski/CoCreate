package org.cocreate.CoCreate.utility;

import org.cocreate.CoCreate.service.CustomUserDetailsService;
import org.cocreate.CoCreate.utility.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    private final Map<String, UserDetails> userDetailsCache = new ConcurrentHashMap<String, UserDetails>();

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        System.out.println(authorizationHeader);
        String jwt = null;
        String username = null;

        // Extract JWT token from the header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtils.extractUsername(jwt);
        }

        // If username is found and the user is not authenticated yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Check if UserDetails are already cached
            UserDetails userDetails = userDetailsCache.get(username);

            if (userDetails == null) {
                // If not cached, load from the database
                userDetails = customUserDetailsService.loadUserByUsername(username);
                // Cache the UserDetails for future requests
                userDetailsCache.put(username, userDetails);
            }

            // Validate the token and set authentication if valid
            if (jwtUtils.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        chain.doFilter(request, response);
    }
}