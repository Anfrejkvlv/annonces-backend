package com.logement.etudiants.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logement.etudiants.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * DTO de réponse pour les informations utilisateur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String nom;

    private String prenom;

    private String email;

    private String telephone;

    private String pays;

    private Role role;

    private Boolean active;

    private Boolean emailVerifie;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime derniereConnexion;

    private Long nombreAnnonces;

    private String nomComplet;
}
