package com.logement.etudiants.controller;

import com.logement.etudiants.dto.request.QuartierRequest;
import com.logement.etudiants.dto.request.VilleRequest;
import com.logement.etudiants.dto.response.QuartierResponse;
import com.logement.etudiants.dto.response.VilleResponse;
import com.logement.etudiants.entity.User;
import com.logement.etudiants.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List; /**
 * Contrôleur pour la gestion des villes et quartiers
 */
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Localisation", description = "Gestion des villes et quartiers")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class LocationController {

    private final LocationService locationService;

    /**
     * Récupère toutes les villes
     */
    @GetMapping("/villes")
    @Operation(summary = "Liste des villes",
            description = "Récupère toutes les villes disponibles")
    public ResponseEntity<List<VilleResponse>> getAllVilles() {
        log.debug("Récupération de toutes les villes");

        List<VilleResponse> villes = locationService.getAllVilles();

        return ResponseEntity.ok(villes);
    }

    /**
     * Récupère les quartiers d'une ville
     */
    @GetMapping("/villes/{villeId}/quartiers")
    @Operation(summary = "Quartiers d'une ville",
            description = "Récupère tous les quartiers d'une ville donnée")
    public ResponseEntity<List<QuartierResponse>> getQuartiersByVille(@PathVariable Long villeId) {
        log.debug("Récupération des quartiers pour la ville ID: {}", villeId);

        List<QuartierResponse> quartiers = locationService.getQuartiersByVille(villeId);

        return ResponseEntity.ok(quartiers);
    }

    /**
     * Ajoute une nouvelle ville (Admin seulement)
     */
    @PostMapping("/villes")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ajouter une ville",
            description = "Ajoute une nouvelle ville au système")
    public ResponseEntity<VilleResponse> createVille(
            @jakarta.validation.Valid @RequestBody VilleRequest request
            , @AuthenticationPrincipal User admin

    ) {

        log.info("Création d'une nouvelle ville: {} par l'admin: {}", request.getNom()
                , admin.getEmail()
        );

        // TODO: Implémenter la création de ville
        VilleResponse newVille = locationService.createVille(request);

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(newVille);
    }

    /**
     * Ajoute un nouveau quartier (Admin seulement)
     */
    @PostMapping("/quartiers")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ajouter un quartier",
            description = "Ajoute un nouveau quartier à une ville")
    public ResponseEntity<QuartierResponse> createQuartier(
            @jakarta.validation.Valid @RequestBody QuartierRequest request
            , @AuthenticationPrincipal User admin
    ) {

        log.info("Création d'un nouveau quartier: {} par l'admin: {}", request.getNom()
                , admin.getEmail()
        );

        // TODO: Implémenter la création de quartier
        QuartierResponse newQuartier = locationService.createQuartier(request);

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(newQuartier);
    }
}
