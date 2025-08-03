package com.logement.etudiants.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; /**
 * DTO pour la requête de modération d'annonce
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationRequest {

    @NotNull(message = "La décision de modération est obligatoire")
    private Boolean approve; // true = approuver, false = rejeter

    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    private String commentaire;

    @Size(max = 1000, message = "Les raisons ne peuvent pas dépasser 1000 caractères")
    private String raisonsRejet;
}
