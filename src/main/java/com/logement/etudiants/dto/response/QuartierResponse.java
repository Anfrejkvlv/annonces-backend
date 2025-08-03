package com.logement.etudiants.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de réponse pour les quartiers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuartierResponse {

    private Long id;

    private String nom;

    private String description;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Boolean active;

    private Long nombreAnnonces;

    private VilleResponse ville;

    private String nomComplet;
}
