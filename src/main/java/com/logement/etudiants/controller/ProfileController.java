package com.logement.etudiants.controller;

import com.logement.etudiants.dto.response.UserResponse;
import com.logement.etudiants.entity.User;
import com.logement.etudiants.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*; /**
 * Contrôleur pour la gestion des profils utilisateurs
 */
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Profil utilisateur", description = "Gestion du profil utilisateur")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class ProfileController {

    private final UserService userService;

    /**
     * Récupère le profil de l'utilisateur connecté
     */
    @GetMapping
    @Operation(summary = "Mon profil",
            description = "Récupère les informations du profil de l'utilisateur connecté")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal User currentUser) {

        log.info("Récupération du profil pour l'utilisateur: {}", currentUser.getEmail());

        UserResponse profile = userService.getProfile(currentUser.getId());

        return ResponseEntity.ok(profile);
    }

    /**
     * Met à jour le profil de l'utilisateur connecté
     */
    @PutMapping
    @Operation(summary = "Modifier mon profil",
            description = "Met à jour les informations du profil utilisateur")
    public ResponseEntity<UserResponse> updateMyProfile(
            @jakarta.validation.Valid @RequestBody com.logement.etudiants.dto.request.UpdateProfileRequest request,
            @AuthenticationPrincipal User currentUser) {

        log.info("Mise à jour du profil pour l'utilisateur: {}", currentUser.getEmail());

        UserResponse updatedProfile = userService.updateProfile(currentUser.getId(), request);

        return ResponseEntity.ok(updatedProfile);
    }
}
