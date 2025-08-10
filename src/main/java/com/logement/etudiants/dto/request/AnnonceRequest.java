package com.logement.etudiants.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logement.etudiants.enumeration.TypeLogement;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO pour la requête de création/modification d'annonce
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 10, max = 100, message = "Le titre doit contenir entre 10 et 100 caractères")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 50, max = 2000, message = "La description doit contenir entre 50 et 2000 caractères")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @DecimalMax(value = "999999.99", message = "Le prix ne peut pas dépasser 999 999,99")
    @Digits(integer = 8, fraction = 2, message = "Format de prix invalide")
    private BigDecimal prix;

    @NotNull(message = "Le type de logement est obligatoire")
    private TypeLogement typeLogement;

    @NotNull(message = "La superficie est obligatoire")
    @Min(value = 10, message = "La superficie doit être d'au moins 10 m²")
    @Max(value = 500, message = "La superficie ne peut pas dépasser 500 m²")
    private Integer superficie;

    @Min(value = 1, message = "Au moins une pièce requise")
    @Max(value = 15, message = "Maximum 15 pièces")
    private Integer nombrePieces;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 10, max = 200, message = "L'adresse doit contenir entre 10 et 200 caractères")
    private String adresse;

    @NotNull(message = "La ville est obligatoire")
    @Positive(message = "L'ID de la ville doit être positif")
    private Long villeId;

    @NotNull(message = "Le quartier est obligatoire")
    @Positive(message = "L'ID du quartier doit être positif")
    private Long quartierId;

    @DecimalMin(value = "-90.0", message = "Latitude invalide")
    @DecimalMax(value = "90.0", message = "Latitude invalide")
    @Digits(integer = 2, fraction = 8, message = "Format de latitude invalide")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude invalide")
    @DecimalMax(value = "180.0", message = "Longitude invalide")
    @Digits(integer = 3, fraction = 8, message = "Format de longitude invalide")
    private BigDecimal longitude;

    @Size(max = 10, message = "Maximum 10 images par annonce")
    private List<String> images;

    // Caractéristiques optionnelles
    private Boolean meuble;
    private Boolean parking;
    private Boolean balcon;
    private Boolean jardin;
    private Boolean climatisation;
    private Boolean chauffage;
    private Boolean internet;
    private Boolean animauxAutorises;

    // Métadonnées SEO optionnelles
    @Size(max = 150, message = "Le titre meta ne peut pas dépasser 150 caractères")
    private String metaTitle;

    @Size(max = 300, message = "La description meta ne peut pas dépasser 300 caractères")
    private String metaDescription;
}





