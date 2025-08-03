package com.logement.etudiants.service;

import com.logement.etudiants.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service; /**
 * Service pour l'envoi d'emails (stub - à implémenter selon vos besoins)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    public void sendWelcomeEmail(User user) {
        log.info("Envoi email de bienvenue à: {}", user.getEmail());
        // TODO: Implémenter l'envoi d'email réel
    }

    public void sendPasswordChangeNotification(User user) {
        log.info("Envoi notification changement mot de passe à: {}", user.getEmail());
        // TODO: Implémenter l'envoi d'email réel
    }

    public void sendPasswordResetEmail(User user, String token) {
        log.info("Envoi email réinitialisation mot de passe à: {}", user.getEmail());
        // TODO: Implémenter l'envoi d'email réel avec lien contenant le token
        // Exemple de lien: https://votre-site.com/reset-password?token=${token}
    }

    public void sendPasswordResetConfirmation(User user) {
        log.info("Envoi confirmation réinitialisation mot de passe à: {}", user.getEmail());
        // TODO: Implémenter l'envoi d'email réel
    }
}
