package com.logement.etudiants.enumeration;

import lombok.Getter;


/**
 * Enum représentant les rôles des utilisateurs
 */
@Getter
public enum Role {
    /**
     * Utilisateur standard - peut créer et gérer ses propres annonces
     */
    USER("Utilisateur"),

    /**
     * Administrateur - accès complet au système
     */
    ADMIN("Administrateur"),

    /**
     * Modérateur - peut modérer les annonces mais pas accéder à l'admin complet
     */
    MODERATOR("Modérateur");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isModerator() {
        return this == MODERATOR || this == ADMIN;
    }
}







