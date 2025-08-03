package com.logement.etudiants.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de réponse pour les villes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VilleResponse {

    private Long id;

    private String nom;

    private String codePostal;

    private String pays;

    private String description;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Boolean active;

    private Long nombreAnnonces;

    private Long nombreQuartiers;

    private String nomComplet;
}





