package com.logement.etudiants.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; /**
 * DTO pour la requête de création de ville
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VilleRequest {

    @NotBlank(message = "Le nom de la ville est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom de la ville doit contenir entre 2 et 100 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le nom de la ville ne peut contenir que des lettres, espaces, apostrophes et tirets")
    private String nom;

    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "^[0-9]{5}$", message = "Le code postal doit contenir exactement 5 chiffres")
    private String codePostal;

    @NotBlank(message = "Le pays est obligatoire")
    @Size(min = 2, max = 100, message = "Le pays doit contenir entre 2 et 100 caractères")
    private String pays;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @DecimalMin(value = "-90.0", message = "Latitude invalide")
    @DecimalMax(value = "90.0", message = "Latitude invalide")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude invalide")
    @DecimalMax(value = "180.0", message = "Longitude invalide")
    private BigDecimal longitude;
}
