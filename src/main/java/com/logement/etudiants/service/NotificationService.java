package com.logement.etudiants.service;

import com.logement.etudiants.entity.Annonce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service; /**
 * Service stub pour les notifications
 */
@Service
@Slf4j
public class NotificationService {

    public void sendAnnonceApprovedNotification(Annonce annonce) {
        log.info("Notification d'approbation envoyée pour l'annonce ID: {}", annonce.getId());
        // TODO: Implémenter l'envoi de notification réel
    }

    public void sendAnnonceRejectedNotification(Annonce annonce) {
        log.info("Notification de rejet envoyée pour l'annonce ID: {}", annonce.getId());
        // TODO: Implémenter l'envoi de notification réel
    }

    public void sendAnnonceExpiredNotification(Annonce annonce) {
        log.info("Notification d'expiration envoyée pour l'annonce ID: {}", annonce.getId());
        // TODO: Implémenter l'envoi de notification réel
    }
}
