package com.logement.etudiants.enumeration;


import lombok.Getter;


/**
 * Enum représentant le statut d'une annonce dans le processus de modération
 */
@Getter
public enum StatutAnnonce {
    /**
     * Annonce soumise et en attente de modération
     */
    EN_ATTENTE("En attente", "L'annonce est en attente de validation par un modérateur", "#FFA500"),

    /**
     * Annonce approuvée et visible publiquement
     */
    APPROUVEE("Approuvée", "L'annonce a été approuvée et est visible publiquement", "#28A745"),

    /**
     * Annonce rejetée par la modération
     */
    REJETEE("Rejetée", "L'annonce a été rejetée par la modération", "#DC3545"),

    /**
     * Annonce expirée automatiquement
     */
    EXPIREE("Expirée", "L'annonce a expiré automatiquement", "#6C757D"),

    /**
     * Annonce suspendue temporairement
     */
    SUSPENDUE("Suspendue", "L'annonce a été suspendue temporairement", "#FF6B35"),

    /**
     * Annonce archivée (non visible mais conservée)
     */
    ARCHIVEE("Archivée", "L'annonce a été archivée", "#17A2B8");

    private final String displayName;
    private final String description;
    private final String colorCode;

    StatutAnnonce(String displayName, String description, String colorCode) {
        this.displayName = displayName;
        this.description = description;
        this.colorCode = colorCode;
    }

    /**
     * Retourne true si l'annonce est visible publiquement
     */
    public boolean isPublic() {
        return this == APPROUVEE;
    }

    /**
     * Retourne true si l'annonce peut être modifiée par l'utilisateur
     */
    public boolean isEditable() {
        return this == EN_ATTENTE || this == REJETEE;
    }

    /**
     * Retourne true si l'annonce nécessite une action de modération
     */
    public boolean needsModeration() {
        return this == EN_ATTENTE;
    }

    /**
     * Retourne true si le statut est définitif (ne peut plus changer automatiquement)
     */
    public boolean isFinal() {
        return this == REJETEE || this == EXPIREE || this == ARCHIVEE;
    }
}

