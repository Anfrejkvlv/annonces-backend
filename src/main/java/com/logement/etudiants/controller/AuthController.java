package com.logement.etudiants.controller;

import com.logement.etudiants.dto.request.LoginRequest;
import com.logement.etudiants.dto.request.RegisterRequest;
import com.logement.etudiants.dto.response.JwtResponse;
import com.logement.etudiants.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.logement.etudiants.dto.request.*;
import com.logement.etudiants.dto.response.*;
import com.logement.etudiants.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour l'authentification et la gestion des comptes utilisateurs
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentification", description = "APIs d'authentification et de gestion des comptes")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AuthController {

    private final AuthService authService;

    /**
     * Inscription d'un nouvel utilisateur
     */
    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur",
            description = "Crée un nouveau compte utilisateur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<JwtResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        log.info("Demande d'inscription reçue pour l'email: {} depuis IP: {}",
                request.getEmail(), getClientIP(httpRequest));

        JwtResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Connexion d'un utilisateur
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur",
            description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "423", description = "Compte temporairement verrouillé")
    })
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        log.info("Demande de connexion reçue pour l'email: {} depuis IP: {}",
                request.getEmail(), getClientIP(httpRequest));

        JwtResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Changement de mot de passe
     */
    @PostMapping("/change-password/{userId}")
    @Operation(summary = "Changer le mot de passe",
            description = "Permet à un utilisateur de changer son mot de passe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe changé avec succès"),
            @ApiResponse(responseCode = "400", description = "Ancien mot de passe incorrect"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<MessageResponse> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {

        log.info("Demande de changement de mot de passe pour l'utilisateur ID: {}", userId);

        authService.changePassword(userId, request);

        return ResponseEntity.ok(MessageResponse.success("Mot de passe changé avec succès"));
    }

    /**
     * Demande de réinitialisation de mot de passe
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Demander la réinitialisation du mot de passe",
            description = "Envoie un email avec un lien de réinitialisation du mot de passe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email de réinitialisation envoyé"),
            @ApiResponse(responseCode = "404", description = "Email non trouvé")
    })
    public ResponseEntity<MessageResponse> requestPasswordReset(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {

        log.info("Demande de réinitialisation de mot de passe pour l'email: {} depuis IP: {}",
                request.getEmail(), getClientIP(httpRequest));

        authService.requestPasswordReset(request);

        return ResponseEntity.ok(MessageResponse.success(
                "Si cet email existe dans notre système, vous recevrez un lien de réinitialisation"));
    }

    /**
     * Confirmation de réinitialisation de mot de passe
     */
    @PostMapping("/reset-password/confirm")
    @Operation(summary = "Confirmer la réinitialisation du mot de passe",
            description = "Finalise la réinitialisation du mot de passe avec le token reçu par email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès"),
            @ApiResponse(responseCode = "400", description = "Token invalide ou expiré")
    })
    public ResponseEntity<MessageResponse> confirmPasswordReset(
            @Valid @RequestBody ConfirmResetPasswordRequest request) {

        log.info("Confirmation de réinitialisation de mot de passe avec token: {}", request.getToken());

        authService.confirmPasswordReset(request);

        return ResponseEntity.ok(MessageResponse.success("Mot de passe réinitialisé avec succès"));
    }

    /**
     * Validation d'un token JWT
     */
    @PostMapping("/validate-token")
    @Operation(summary = "Valider un token JWT",
            description = "Vérifie si un token JWT est valide")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token valide"),
            @ApiResponse(responseCode = "401", description = "Token invalide")
    })
    public ResponseEntity<MessageResponse> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MessageResponse.error("Token manquant ou format invalide"));
        }

        String token = authHeader.substring(7);
        boolean isValid = authService.validateToken(token);

        if (isValid) {
            return ResponseEntity.ok(MessageResponse.success("Token valide"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MessageResponse.error("Token invalide"));
        }
    }

    /**
     * Déconnexion
     */
    @PostMapping("/logout")
    @Operation(summary = "Déconnexion",
            description = "Déconnecte l'utilisateur (invalide le token côté serveur si implémenté)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie")
    })
    public ResponseEntity<MessageResponse> logout(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }

        return ResponseEntity.ok(MessageResponse.success("Déconnexion réussie"));
    }

    /**
     * Récupère l'adresse IP du client
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor == null || xForwardedFor.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xForwardedFor.split(",")[0].trim();
    }
}

