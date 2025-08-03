package com.logement.etudiants.dto.request;

import com.logement.etudiants.enumeration.TypeLogement;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; /**
 * DTO pour les filtres de recherche d'annonces
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceSearchRequest {

    @Size(max = 100, message = "Le terme de recherche ne peut pas dépasser 100 caractères")
    private String searchTerm;

    private TypeLogement typeLogement;

    @Positive(message = "L'ID de la ville doit être positif")
    private Long villeId;

    @Positive(message = "L'ID du quartier doit être positif")
    private Long quartierId;

    @DecimalMin(value = "0.0", message = "Le prix minimum doit être positif")
    private BigDecimal prixMin;

    @DecimalMin(value = "0.0", message = "Le prix maximum doit être positif")
    private BigDecimal prixMax;

    @Min(value = 1, message = "La superficie minimum doit être d'au moins 1 m²")
    private Integer superficieMin;

    @Min(value = 1, message = "La superficie maximum doit être d'au moins 1 m²")
    private Integer superficieMax;

    @Min(value = 1, message = "Le nombre de pièces minimum doit être d'au moins 1")
    private Integer nombrePiecesMin;

    @Min(value = 1, message = "Le nombre de pièces maximum doit être d'au moins 1")
    private Integer nombrePiecesMax;

    // Caractéristiques
    private Boolean meuble;
    private Boolean parking;
    private Boolean balcon;
    private Boolean jardin;
    private Boolean climatisation;
    private Boolean chauffage;
    private Boolean internet;
    private Boolean animauxAutorises;

    // Tri
    private String sortBy = "dateCreation"; // dateCreation, prix, superficie, nombreVues
    private String sortDirection = "desc"; // asc, desc

    // Géolocalisation
    @DecimalMin(value = "-90.0", message = "Latitude invalide")
    @DecimalMax(value = "90.0", message = "Latitude invalide")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude invalide")
    @DecimalMax(value = "180.0", message = "Longitude invalide")
    private BigDecimal longitude;

    @Min(value = 1, message = "Le rayon doit être d'au moins 1 km")
    @Max(value = 100, message = "Le rayon ne peut pas dépasser 100 km")
    private Integer rayonKm;
}
