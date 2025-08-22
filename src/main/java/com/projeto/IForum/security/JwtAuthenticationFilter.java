package com.projeto.IForum.security;

import com.projeto.IForum.model.User;
import com.projeto.IForum.service.JwtService;
import com.projeto.IForum.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtUtilsService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtUtilsService, UserService userService) {
        this.jwtUtilsService = jwtUtilsService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        if (!jwtUtilsService.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtUtilsService.getUserIdFromToken(token);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userService.buscarPorId(userId).orElse(null);

            if (user != null) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getSenha())
                        .authorities(Collections.emptyList()) 
                        .build();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
