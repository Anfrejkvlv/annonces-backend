package com.logement.etudiants.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map; /**
 * DTO de réponse pour l'historique des actions utilisateur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueResponse {

    private Long id;

    private String action;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateAction;

    private String adresseIp;

    private String userAgent;

    private Map<String, Object> metadata;
}
