package com.logement.etudiants.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; /**
 * DTO pour la requête de création de quartier
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuartierRequest {

    @NotBlank(message = "Le nom du quartier est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom du quartier doit contenir entre 2 et 100 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ0-9\\s'-]+$", message = "Le nom du quartier ne peut contenir que des lettres, chiffres, espaces, apostrophes et tirets")
    private String nom;

    @NotNull(message = "La ville est obligatoire")
    @Positive(message = "L'ID de la ville doit être positif")
    private Long villeId;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @DecimalMin(value = "-90.0", message = "Latitude invalide")
    @DecimalMax(value = "90.0", message = "Latitude invalide")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude invalide")
    @DecimalMax(value = "180.0", message = "Longitude invalide")
    private BigDecimal longitude;
}
