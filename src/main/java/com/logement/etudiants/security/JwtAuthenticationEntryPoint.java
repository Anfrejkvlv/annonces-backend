package com.logement.etudiants.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Point d'entrée d'authentification personnalisé
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Accès non autorisé à {}: {}", request.getRequestURI(), authException.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Créer une réponse d'erreur JSON structurée
        String jsonResponse = """
            {
                "timestamp": "%s",
                "status": 401,
                "error": "Unauthorized",
                "message": "Accès non autorisé - Token JWT requis",
                "path": "%s"
            }
            """.formatted(
                java.time.LocalDateTime.now().toString(),
                request.getRequestURI()
        );

        response.getWriter().write(jsonResponse);
    }
}

