package com.logement.etudiants.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logement.etudiants.enumeration.StatutAnnonce;
import com.logement.etudiants.enumeration.TypeLogement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * DTO de réponse pour les annonces
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceResponse {

    private Long id;

    private String titre;

    private String description;

    private BigDecimal prix;

    private TypeLogement typeLogement;

    private Integer superficie;

    private Integer nombrePieces;

    private String adresse;

    private String villeNom;

    private String quartierNom;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @JsonIgnore
    private List<String> images;

    private StatutAnnonce statut;

    private Boolean active;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateModification;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateExpiration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datePublication;

    private String utilisateurNom;

    private String utilisateurEmail;

    private Long nombreVues;

    private Long nombreFavoris;

    private String commentaireModeation;

    // Caractéristiques
    private Boolean meuble;
    private Boolean parking;
    private Boolean balcon;
    private Boolean jardin;
    private Boolean climatisation;
    private Boolean chauffage;
    private Boolean internet;
    private Boolean animauxAutorises;

    // Métadonnées
    private String metaTitle;
    private String metaDescription;
    private String slug;

    // Statut calculés
    private Boolean isPubliee;
    private Boolean isExpired;
    private Boolean isEditable;
}
