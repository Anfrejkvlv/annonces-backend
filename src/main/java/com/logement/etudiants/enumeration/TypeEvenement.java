package com.logement.etudiants.enumeration;

/**
 * Enum représentant les types d'événements du système pour l'audit
 */
public enum TypeEvenement {
    // Événements utilisateur
    USER_REGISTERED("Inscription utilisateur"),
    USER_LOGIN("Connexion utilisateur"),
    USER_LOGOUT("Déconnexion utilisateur"),
    USER_PASSWORD_CHANGED("Changement mot de passe"),
    USER_EMAIL_VERIFIED("Email vérifié"),
    USER_ACCOUNT_LOCKED("Compte verrouillé"),
    USER_ACCOUNT_UNLOCKED("Compte déverrouillé"),

    // Événements annonces
    ANNONCE_CREATED("Annonce créée"),
    ANNONCE_UPDATED("Annonce modifiée"),
    ANNONCE_DELETED("Annonce supprimée"),
    ANNONCE_PUBLISHED("Annonce publiée"),
    ANNONCE_REJECTED("Annonce rejetée"),
    ANNONCE_EXPIRED("Annonce expirée"),
    ANNONCE_VIEWED("Annonce consultée"),
    ANNONCE_FAVORITED("Annonce ajoutée aux favoris"),

    // Événements modération
    MODERATION_APPROVED("Modération - Approuvée"),
    MODERATION_REJECTED("Modération - Rejetée"),
    MODERATION_FLAGGED("Modération - Signalée"),

    // Événements système
    SYSTEM_BACKUP("Sauvegarde système"),
    SYSTEM_MAINTENANCE("Maintenance système"),
    SYSTEM_ERROR("Erreur système");

    private final String displayName;

    TypeEvenement(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isUserEvent() {
        return name().startsWith("USER_");
    }

    public boolean isAnnonceEvent() {
        return name().startsWith("ANNONCE_");
    }

    public boolean isModerationEvent() {
        return name().startsWith("MODERATION_");
    }

    public boolean isSystemEvent() {
        return name().startsWith("SYSTEM_");
    }
}
