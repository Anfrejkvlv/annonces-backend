package com.logement.etudiants.enumeration;

import lombok.Getter;


/**
 * Enum représentant les types de logement
 */
@Getter
public enum TypeLogement {
    /**
     * Chambre dans un appartement partagé ou chez l'habitant
     */
    CHAMBRE("Chambre", "Une chambre dans un logement partagé"),

    /**
     * Studio - petit appartement avec une seule pièce principale
     */
    STUDIO("Studio", "Petit appartement avec une pièce principale"),

    /**
     * Appartement - logement avec plusieurs pièces séparées
     */
    APPARTEMENT("Appartement", "Logement avec plusieurs pièces séparées"),

    /**
     * Maison individuelle
     */
    MAISON("Maison", "Maison individuelle");

    private final String displayName;
    private final String description;

    TypeLogement(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Retourne true si ce type de logement est typiquement partagé
     */
    public boolean isPartage() {
        return this == CHAMBRE;
    }

    /**
     * Retourne true si ce type de logement est généralement indépendant
     */
    public boolean isIndependant() {
        return this == STUDIO || this == APPARTEMENT || this == MAISON;
    }
}
