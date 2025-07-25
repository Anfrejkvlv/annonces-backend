package com.logement.etudiants.entity;

import com.logement.etudiants.enumeration.StatutAnnonce;
import com.logement.etudiants.enumeration.TypeLogement;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "annonces")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 10, max = 100, message = "Le titre doit contenir entre 10 et 100 caractères")
    @Column(nullable = false)
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 50, max = 2000, message = "La description doit contenir entre 50 et 2000 caractères")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Digits(integer = 8, fraction = 2, message = "Format de prix invalide")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal prix;

    @NotNull(message = "Le type de logement est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeLogement typeLogement;

    @NotNull(message = "La superficie est obligatoire")
    @Min(value = 10, message = "La superficie doit être d'au moins 10 m²")
    @Max(value = 500, message = "La superficie ne peut pas dépasser 500 m²")
    @Column(nullable = false)
    private Integer superficie;

    @Min(value = 1, message = "Au moins une pièce requise")
    @Max(value = 10, message = "Maximum 10 pièces")
    @Column(name = "nombre_pieces")
    private Integer nombrePieces;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    @Column(nullable = false)
    private String adresse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ville_id", nullable = false)
    @NotNull(message = "La ville est obligatoire")
    private Ville ville;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quartier_id", nullable = false)
    @NotNull(message = "Le quartier est obligatoire")
    private Quartier quartier;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @ElementCollection
    @CollectionTable(name = "annonce_images", joinColumns = @JoinColumn(name = "annonce_id"))
    @Column(name = "url_image")
    private List<String> images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutAnnonce statut = StatutAnnonce.EN_ATTENTE;

    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    private String commentaireModeation;
}

