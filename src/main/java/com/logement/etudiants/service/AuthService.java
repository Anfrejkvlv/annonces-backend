package com.logement.etudiants.service;

import com.logement.etudiants.dto.request.*;
import com.logement.etudiants.dto.response.JwtResponse;
import com.logement.etudiants.entity.User;
import com.logement.etudiants.enumeration.Role;
import com.logement.etudiants.exception.InvalidCredentialsException;
import com.logement.etudiants.exception.UserAlreadyExistsException;
import com.logement.etudiants.repository.UserRepository;
import com.logement.etudiants.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service d'authentification et de gestion des utilisateurs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    /**
     * Inscription d'un nouvel utilisateur
     */
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        log.info("Tentative d'inscription pour l'email: {}", request.getEmail());

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Tentative d'inscription avec un email existant: {}", request.getEmail());
            throw new UserAlreadyExistsException("Un utilisateur avec cet email existe déjà");
        }

        // Créer le nouvel utilisateur
        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telephone(request.getTelephone())
                .pays(request.getPays())
                .role(Role.USER)
                .active(true)
                .emailVerifie(true) // Pour simplifier, on considère l'email comme vérifié
                .build();

        User savedUser = userRepository.save(user);

        // Générer le token JWT
        String token = jwtUtil.generateToken(savedUser.getEmail());
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtUtil.getExpirationInSeconds());

        log.info("Utilisateur créé avec succès: {} (ID: {})", savedUser.getEmail(), savedUser.getId());

        // Envoyer email de bienvenue (asynchrone)
        try {
            emailService.sendWelcomeEmail(savedUser);
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi de l'email de bienvenue pour {}: {}", savedUser.getEmail(), e.getMessage());
        }

        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .nom(savedUser.getNom())
                .prenom(savedUser.getPrenom())
                .role(savedUser.getRole())
                .expiresAt(expiresAt)
                .authorities(List.of("ROLE_" + savedUser.getRole().name()))
                .build();
    }

    /**
     * Connexion d'un utilisateur
     */
    @Transactional
    public JwtResponse login(LoginRequest request) {
        log.info("Tentative de connexion pour l'email: {}", request.getEmail());

        try {
            // Authentifier l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();

            // Vérifier si le compte est verrouillé
            if (!user.isAccountNonLocked()) {
                log.warn("Tentative de connexion sur un compte verrouillé: {}", user.getEmail());
                throw new InvalidCredentialsException("Compte temporairement verrouillé");
            }

            // Réinitialiser les tentatives échouées et mettre à jour la dernière connexion
            user.resetTentativesEchouees();
            user.setDerniereConnexion(LocalDateTime.now());
            userRepository.save(user);

            // Générer le token JWT
            String token = jwtUtil.generateToken(user.getEmail());
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtUtil.getExpirationInSeconds());

            log.info("Connexion réussie pour: {} (ID: {})", user.getEmail(), user.getId());

            return JwtResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .nom(user.getNom())
                    .prenom(user.getPrenom())
                    .role(user.getRole())
                    .expiresAt(expiresAt)
                    .authorities(List.of("ROLE_" + user.getRole().name()))
                    .build();

        } catch (AuthenticationException e) {
            log.warn("Échec de connexion pour l'email: {} - {}", request.getEmail(), e.getMessage());

            // Incrémenter les tentatives échouées si l'utilisateur existe
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(user -> {
                        user.incrementTentativesEchouees();
                        userRepository.save(user);
                        log.info("Tentatives échouées incrémentées pour {}: {}",
                                user.getEmail(), user.getTentativesConnexionEchouees());
                    });

            throw new InvalidCredentialsException("Email ou mot de passe incorrect");
        }
    }

    /**
     * Changement de mot de passe
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.info("Changement de mot de passe demandé pour l'utilisateur ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("Utilisateur non trouvé"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Ancien mot de passe incorrect pour l'utilisateur: {}", user.getEmail());
            throw new InvalidCredentialsException("Ancien mot de passe incorrect");
        }

        // Mettre à jour avec le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Mot de passe changé avec succès pour l'utilisateur: {}", user.getEmail());

        // Envoyer email de notification
        try {
            emailService.sendPasswordChangeNotification(user);
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi de la notification de changement de mot de passe pour {}: {}",
                    user.getEmail(), e.getMessage());
        }
    }

    /**
     * Demande de réinitialisation de mot de passe
     */
    @Transactional
    public void requestPasswordReset(ResetPasswordRequest request) {
        log.info("Demande de réinitialisation de mot de passe pour l'email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Aucun utilisateur trouvé avec cet email"));

        // Générer un token de réinitialisation
        String resetToken = UUID.randomUUID().toString();
        user.setTokenResetPassword(resetToken);
        user.setTokenResetExpiration(LocalDateTime.now().plusHours(1)); // Expire dans 1 heure

        userRepository.save(user);

        log.info("Token de réinitialisation généré pour l'utilisateur: {}", user.getEmail());

        // Envoyer email avec le lien de réinitialisation
        try {
            emailService.sendPasswordResetEmail(user, resetToken);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de réinitialisation pour {}: {}",
                    user.getEmail(), e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email");
        }
    }

    /**
     * Confirmation de réinitialisation de mot de passe
     */
    @Transactional
    public void confirmPasswordReset(ConfirmResetPasswordRequest request) {
        log.info("Confirmation de réinitialisation de mot de passe avec le token: {}", request.getToken());

        User user = userRepository.findByValidResetToken(request.getToken())
                .orElseThrow(() -> new InvalidCredentialsException("Token invalide ou expiré"));

        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setTokenResetPassword(null);
        user.setTokenResetExpiration(null);
        user.resetTentativesEchouees(); // Réinitialiser les tentatives échouées

        userRepository.save(user);

        log.info("Mot de passe réinitialisé avec succès pour l'utilisateur: {}", user.getEmail());

        // Envoyer email de confirmation
        try {
            emailService.sendPasswordResetConfirmation(user);
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi de la confirmation de réinitialisation pour {}: {}",
                    user.getEmail(), e.getMessage());
        }
    }

    /**
     * Valide un token JWT
     */
    public boolean validateToken(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            return userRepository.findByEmail(email)
                    .map(user -> jwtUtil.validateToken(token, user.getUsername()))
                    .orElse(false);
        } catch (Exception e) {
            log.debug("Token invalide: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Récupère un utilisateur à partir d'un token
     */
    public User getUserFromToken(String token) {
        String email = jwtUtil.extractUsername(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Utilisateur non trouvé"));
    }

    /**
     * Déconnexion (côté serveur - pour invalider le token si nécessaire)
     */
    public void logout(String token) {
        // Dans une implémentation complète, on pourrait ajouter le token à une blacklist
        log.info("Déconnexion effectuée pour le token: {}", token.substring(0, 20) + "...");
    }

    /**
     * Vérifie si un utilisateur est actif et peut se connecter
     */
    public boolean isUserActiveAndEnabled(String email) {
        return userRepository.findByEmail(email)
                .map(User::isEnabled)
                .orElse(false);
    }

    /**
     * Déverrouille un compte utilisateur (pour les admins)
     */
    @Transactional
    public void unlockUserAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("Utilisateur non trouvé"));

        user.resetTentativesEchouees();
        userRepository.save(user);

        log.info("Compte déverrouillé pour l'utilisateur: {}", user.getEmail());
    }
}

