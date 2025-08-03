package com.logement.etudiants.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map; /**
 * Contrôleur pour les fonctionnalités publiques et les utilitaires
 */
@RestController
@RequestMapping("/api/v1/public")
@Slf4j
@Tag(name = "Public", description = "APIs publiques et utilitaires")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class PublicController {

    /**
     * Health check de l'API
     */
    @GetMapping("/health")
    @Operation(summary = "Health check",
            description = "Vérifie que l'API fonctionne correctement")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "timestamp", java.time.LocalDateTime.now(),
                "version", "1.0.0",
                "environment", "development"
        );

        return ResponseEntity.ok(health);
    }

    /**
     * Informations sur l'API
     */
    @GetMapping("/info")
    @Operation(summary = "Informations API",
            description = "Retourne les informations générales sur l'API")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = Map.of(
                "name", "API Logements Étudiants",
                "version", "1.0.0",
                "description", "API pour la gestion d'annonces de logements étudiants",
                "documentation", "/swagger-ui.html",
                "contact", Map.of(
                        "name", "Support",
                        "email", "support@logements-etudiants.com"
                )
        );

        return ResponseEntity.ok(info);
    }

    /**
     * Statistiques publiques
     */
    @GetMapping("/stats")
    @Operation(summary = "Statistiques publiques",
            description = "Retourne les statistiques publiques du site")
    public ResponseEntity<Map<String, Object>> getPublicStats() {
        Map<String, Object> stats = Map.of(
                "totalAnnonces", 500L,
                "villesDisponibles", 20L,
                "nouvellesSemaine", 25L,
                "derniereMiseAJour", java.time.LocalDateTime.now()
        );

        return ResponseEntity.ok(stats);
    }
}
