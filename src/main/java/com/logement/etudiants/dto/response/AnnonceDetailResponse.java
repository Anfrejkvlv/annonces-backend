package com.logement.etudiants.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map; /**
 * DTO de réponse pour les détails d'une annonce (version complète)
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceDetailResponse extends AnnonceResponse {

    private UserResponse utilisateur;

    private List<AnnonceResponse> annoncesAssociees;

    private Map<String, Object> statistiques;

    private Boolean isFavorite;

    private String contactInfo;

}
