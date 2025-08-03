package com.logement.etudiants.filter;

import com.logement.etudiants.security.CustomUserDetailsService;
import com.logement.etudiants.security.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * Filtre JWT pour l'authentification des requêtes
 */
/*
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        String username = null;
        String jwtToken = null;

        // Le token JWT est dans l'en-tête Authorization sous la forme "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
                log.debug("Token JWT reçu pour l'utilisateur: {} sur {}", username, requestURI);
            } catch (IllegalArgumentException e) {
                log.warn("Impossible d'obtenir le token JWT pour {}: {}", requestURI, e.getMessage());
            } catch (ExpiredJwtException e) {
                log.warn("Token JWT expiré pour {}: {}", requestURI, e.getMessage());
                // On pourrait ajouter un header personnalisé pour indiquer que le token est expiré
                response.setHeader("X-Token-Expired", "true");
            } catch (Exception e) {
                log.warn("Erreur lors de l'analyse du token JWT pour {}: {}", requestURI, e.getMessage());
            }
        } else {
            log.debug("Aucun token JWT trouvé pour {}", requestURI);
        }

        // Une fois qu'on a le token, on valide
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Si le token est valide, on configure Spring Security manuellement
            if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Utilisateur {} authentifié avec succès pour {}", username, requestURI);
            } else {
                log.warn("Token JWT invalide pour l'utilisateur {} sur {}", username, requestURI);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Ne pas filtrer les endpoints publics
        return path.startsWith("/api/v1/auth/") ||
                path.startsWith("/api/v1/public/") ||
                path.startsWith("/api/v1/annonces/public") ||
                path.startsWith("/api/v1/locations/villes") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/actuator/health") ||
                path.equals("/");
    }
}*/


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        String username = null;
        String jwtToken = null;

        // Le token JWT est dans l'en-tête Authorization sous la forme "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
                log.debug("Token JWT reçu pour l'utilisateur: {} sur {}", username, requestURI);
            } catch (IllegalArgumentException e) {
                log.warn("Impossible d'obtenir le token JWT pour {}: {}", requestURI, e.getMessage());
            } catch (ExpiredJwtException e) {
                log.warn("Token JWT expiré pour {}: {}", requestURI, e.getMessage());
                // On pourrait ajouter un header personnalisé pour indiquer que le token est expiré
                response.setHeader("X-Token-Expired", "true");
            } catch (Exception e) {
                log.warn("Erreur lors de l'analyse du token JWT pour {}: {}", requestURI, e.getMessage());
            }
        } else {
            log.debug("Aucun token JWT trouvé pour {}", requestURI);
        }

        // Une fois qu'on a le token, on valide
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Si le token est valide, on configure Spring Security manuellement
            if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Utilisateur {} authentifié avec succès pour {}", username, requestURI);
            } else {
                log.warn("Token JWT invalide pour l'utilisateur {} sur {}", username, requestURI);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Ne pas filtrer les endpoints publics
        return path.startsWith("/api/v1/auth/") ||
                path.startsWith("/api/v1/public/") ||
                path.startsWith("/api/v1/annonces/public") ||
                path.startsWith("/api/v1/locations/villes") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/actuator/health") ||
                path.equals("/");
    }
}


/*
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String email = null, jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            email = jwtUtil.extractUsername(jwt);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        chain.doFilter(request, response);
    }
}

 */

