package com.logement.etudiants.service;

import com.logement.etudiants.entity.Annonce;
import com.logement.etudiants.enumeration.StatutAnnonce;
import java.util.regex.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Service de modération automatique des annonces
 */
@Service
@Slf4j
public class ModerationService {

    // Liste des mots interdits (à configurer selon vos besoins)
    private static final List<String> MOTS_INTERDITS = Arrays.asList(
            "arnaque", "fraude", "scam", "fake", "bitcoin", "crypto",
            "pyramide", "mlm", "ponzi", "gratuit", "urgent", "rapide",
            "facile", "sans effort", "miracle", "garanti", "100%",
            "prostitution", "escort", "sex", "drogue", "cannabis"
    );

    // Patterns suspects
    private static final Pattern PATTERN_EMAIL = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern PATTERN_TELEPHONE = Pattern.compile("(?:\\+33|0)[1-9](?:[0-9]{8})");
    private static final Pattern PATTERN_URL = Pattern.compile("https?://[\\w\\.-]+");
    private static final Pattern PATTERN_PRIX_SUSPECT = Pattern.compile("(0+\\s*€|gratuit|free|0\\s*mad|0\\s*dh)");

    /**
     * Modère automatiquement une annonce
     */
    public StatutAnnonce moderateAnnonce(Annonce annonce) {
        log.debug("Modération automatique de l'annonce: {}", annonce.getId());

        try {
            // Vérifications de base
            if (checkMotsInterdits(annonce)) {
                log.warn("Annonce rejetée - Mots interdits détectés: {}", annonce.getId());
                annonce.setCommentaireModeation("Contenu inapproprié détecté");
                return StatutAnnonce.REJETEE;
            }

            if (checkContenuSuspect(annonce)) {
                log.warn("Annonce rejetée - Contenu suspect détecté: {}", annonce.getId());
                annonce.setCommentaireModeation("Contenu suspect détecté (coordonnées, prix anormal, etc.)");
                return StatutAnnonce.REJETEE;
            }

            if (checkQualiteContenu(annonce)) {
                log.warn("Annonce rejetée - Qualité insuffisante: {}", annonce.getId());
                annonce.setCommentaireModeation("Qualité du contenu insuffisante");
                return StatutAnnonce.REJETEE;
            }

            if (checkCoherenceDonnees(annonce)) {
                log.warn("Annonce rejetée - Données incohérentes: {}", annonce.getId());
                annonce.setCommentaireModeation("Données incohérentes détectées");
                return StatutAnnonce.REJETEE;
            }

            // Si toutes les vérifications passent
            log.info("Annonce approuvée automatiquement: {}", annonce.getId());
            annonce.setCommentaireModeation("Approuvée automatiquement");
            return StatutAnnonce.APPROUVEE;

        } catch (Exception e) {
            log.error("Erreur lors de la modération automatique de l'annonce {}: {}",
                    annonce.getId(), e.getMessage());
            // En cas d'erreur, on met en attente pour modération manuelle
            annonce.setCommentaireModeation("Erreur lors de la modération automatique - Révision manuelle requise");
            return StatutAnnonce.EN_ATTENTE;
        }
    }

    /**
     * Vérifie la présence de mots interdits
     */
    private boolean checkMotsInterdits(Annonce annonce) {
        String contenuComplet = (annonce.getTitre() + " " + annonce.getDescription()).toLowerCase();

        return MOTS_INTERDITS.stream()
                .anyMatch(motInterdit -> contenuComplet.contains(motInterdit.toLowerCase()));
    }

    /**
     * Vérifie la présence de contenu suspect
     */
    private boolean checkContenuSuspect(Annonce annonce) {
        String contenuComplet = annonce.getTitre() + " " + annonce.getDescription();

        // Vérifier la présence d'emails dans le contenu
        if (PATTERN_EMAIL.matcher(contenuComplet).find()) {
            log.debug("Email détecté dans le contenu de l'annonce {}", annonce.getId());
            return true;
        }

        // Vérifier la présence de numéros de téléphone dans le contenu
        if (PATTERN_TELEPHONE.matcher(contenuComplet).find()) {
            log.debug("Numéro de téléphone détecté dans le contenu de l'annonce {}", annonce.getId());
            return true;
        }

        // Vérifier la présence d'URLs
        if (PATTERN_URL.matcher(contenuComplet).find()) {
            log.debug("URL détectée dans le contenu de l'annonce {}", annonce.getId());
            return true;
        }

        // Vérifier les prix suspects (0€, gratuit, etc.)
        if (PATTERN_PRIX_SUSPECT.matcher(contenuComplet.toLowerCase()).find()) {
            log.debug("Prix suspect détecté dans le contenu de l'annonce {}", annonce.getId());
            return true;
        }

        return false;
    }

    /**
     * Vérifie la qualité du contenu
     */
    private boolean checkQualiteContenu(Annonce annonce) {
        // Titre trop court ou trop répétitif
        if (annonce.getTitre().length() < 10) {
            log.debug("Titre trop court pour l'annonce {}", annonce.getId());
            return true;
        }

        // Description trop courte
        if (annonce.getDescription().length() < 50) {
            log.debug("Description trop courte pour l'annonce {}", annonce.getId());
            return true;
        }

        // Titre en majuscules (plus de 70% de majuscules)
        long majuscules = annonce.getTitre().chars()
                .filter(Character::isUpperCase)
                .count();
        long lettres = annonce.getTitre().chars()
                .filter(Character::isLetter)
                .count();

        if (lettres > 0 && (double) majuscules / lettres > 0.7) {
            log.debug("Trop de majuscules dans le titre de l'annonce {}", annonce.getId());
            return true;
        }

        // Répétition excessive de caractères
        if (annonce.getTitre().matches(".*([!]{3,}|[?]{3,}|[.]{4,}).*") ||
                annonce.getDescription().matches(".*([!]{3,}|[?]{3,}|[.]{4,}).*")) {
            log.debug("Répétition excessive de caractères dans l'annonce {}", annonce.getId());
            return true;
        }

        return false;
    }

    /**
     * Vérifie la cohérence des données
     */
    private boolean checkCoherenceDonnees(Annonce annonce) {
        // Prix incohérent avec le type de logement
        if (annonce.getPrix().doubleValue() < 50) {
            log.debug("Prix trop bas pour l'annonce {}: {}", annonce.getId(), annonce.getPrix());
            return true;
        }

        if (annonce.getPrix().doubleValue() > 50000) {
            log.debug("Prix trop élevé pour l'annonce {}: {}", annonce.getId(), annonce.getPrix());
            return true;
        }

        // Superficie incohérente
        if (annonce.getSuperficie() < 5 || annonce.getSuperficie() > 1000) {
            log.debug("Superficie incohérente pour l'annonce {}: {}", annonce.getId(), annonce.getSuperficie());
            return true;
        }

        // Nombre de pièces incohérent avec la superficie
        if (annonce.getNombrePieces() != null) {
            double superficieParPiece = (double) annonce.getSuperficie() / annonce.getNombrePieces();
            if (superficieParPiece < 5 || superficieParPiece > 200) {
                log.debug("Ratio superficie/pièces incohérent pour l'annonce {}: {} m²/pièce",
                        annonce.getId(), superficieParPiece);
                return true;
            }
        }

        return false;
    }

    /**
     * Calcule un score de confiance pour une annonce (0-100)
     */
    public int calculateTrustScore(Annonce annonce) {
        int score = 100;

        // Pénalités pour contenu suspect
        if (checkMotsInterdits(annonce)) score -= 50;
        if (checkContenuSuspect(annonce)) score -= 30;
        if (checkQualiteContenu(annonce)) score -= 20;
        if (checkCoherenceDonnees(annonce)) score -= 25;

        // Bonus pour qualité
        if (annonce.getImages() != null && annonce.getImages().size() >= 3) score += 10;
        if (annonce.getDescription().length() > 200) score += 5;
        if (annonce.getLatitude() != null && annonce.getLongitude() != null) score += 5;

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Détermine si une annonce nécessite une modération manuelle
     */
    public boolean needsManualReview(Annonce annonce) {
        int trustScore = calculateTrustScore(annonce);

        // Score faible = modération manuelle requise
        if (trustScore < 60) {
            return true;
        }

        // Certains critères déclenchent automatiquement une révision manuelle
        String contenu = (annonce.getTitre() + " " + annonce.getDescription()).toLowerCase();

        // Mots qui nécessitent une vérification manuelle
        List<String> motsRevision = Arrays.asList(
                "accident", "décès", "urgent", "déménagement", "divorce",
                "expulsion", "saisie", "vente", "succession"
        );

        return motsRevision.stream()
                .anyMatch(mot -> contenu.contains(mot.toLowerCase()));
    }

    /**
     * Génère un rapport de modération pour les administrateurs
     */
    public String generateModerationReport(Annonce annonce) {
        StringBuilder report = new StringBuilder();

        report.append("=== RAPPORT DE MODÉRATION ===\n");
        report.append("Annonce ID: ").append(annonce.getId()).append("\n");
        report.append("Titre: ").append(annonce.getTitre()).append("\n");
        report.append("Score de confiance: ").append(calculateTrustScore(annonce)).append("/100\n\n");

        report.append("=== VÉRIFICATIONS ===\n");
        report.append("Mots interdits: ").append(checkMotsInterdits(annonce) ? "❌ DÉTECTÉ" : "✅ OK").append("\n");
        report.append("Contenu suspect: ").append(checkContenuSuspect(annonce) ? "❌ DÉTECTÉ" : "✅ OK").append("\n");
        report.append("Qualité contenu: ").append(checkQualiteContenu(annonce) ? "❌ INSUFFISANTE" : "✅ OK").append("\n");
        report.append("Cohérence données: ").append(checkCoherenceDonnees(annonce) ? "❌ INCOHÉRENT" : "✅ OK").append("\n");

        report.append("\n=== RECOMMANDATION ===\n");
        if (needsManualReview(annonce)) {
            report.append("⚠️ MODÉRATION MANUELLE RECOMMANDÉE");
        } else {
            report.append("✅ APPROBATION AUTOMATIQUE POSSIBLE");
        }

        return report.toString();
    }
}



