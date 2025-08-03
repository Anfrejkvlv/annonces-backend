package com.logement.etudiants.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map; /**
 * DTO de réponse pour les statistiques administrateur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesResponse {

    private Long totalUtilisateurs;

    private Long totalAnnonces;

    private Long annoncesEnAttente;

    private Long annoncesApprouvees;

    private Long annoncesRejetees;

    private Long nouveauxUtilisateursAujourdhui;

    private Long nouvellesAnnoncesAujourdhui;

    private Map<String, Long> annoncesParType;

    private Map<String, Long> utilisateursParPays;

    private Map<String, Long> annoncesParVille;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime derniereMiseAJour;
}
