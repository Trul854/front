package com.example.demo.seguridadjwt.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.seguridadjwt.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lee Authorization: Bearer <token>, valida token y setea Authentication.
 * Añade logs para depuración.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        logger.debug("JwtAuthFilter - Authorization header: {}", header);

        try {
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7).trim();
                if (token.isEmpty()) {
                    logger.debug("JwtAuthFilter - token vacío después de Bearer");
                } else {
                    boolean valid = jwtUtils.validateJwtToken(token);
                    logger.debug("JwtAuthFilter - token valid: {}", valid);
                    if (valid) {
                        String username = jwtUtils.getUsernameFromJwt(token);
                        logger.debug("JwtAuthFilter - username desde token: {}", username);
                        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                            if (userDetails != null) {
                                UsernamePasswordAuthenticationToken auth =
                                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                SecurityContextHolder.getContext().setAuthentication(auth);
                                logger.debug("JwtAuthFilter - Authentication seteada para user: {}", username);
                            } else {
                                logger.warn("JwtAuthFilter - userDetails es null para username: {}", username);
                            }
                        }
                    } else {
                        logger.warn("JwtAuthFilter - token inválido o expirado");
                    }
                }
            } else {
                logger.debug("JwtAuthFilter - header no presenta Bearer, omitiendo");
            }
        } catch (Exception ex) {
            logger.error("JwtAuthFilter - error validando token: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }
}