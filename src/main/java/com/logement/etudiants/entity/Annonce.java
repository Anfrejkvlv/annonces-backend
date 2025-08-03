package com.logement.etudiants.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logement.etudiants.enumeration.StatutAnnonce;
import com.logement.etudiants.enumeration.TypeLogement;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Entité Annonce représentant une annonce de logement
 */
@Entity
@Table(name = "annonces", indexes = {
        @Index(name = "idx_annonce_statut", columnList = "statut"),
        @Index(name = "idx_annonce_ville", columnList = "ville_id"),
        @Index(name = "idx_annonce_quartier", columnList = "quartier_id"),
        @Index(name = "idx_annonce_type", columnList = "type_logement"),
        @Index(name = "idx_annonce_prix", columnList = "prix"),
        @Index(name = "idx_annonce_active", columnList = "active"),
        @Index(name = "idx_annonce_date_creation", columnList = "date_creation"),
        @Index(name = "idx_annonce_utilisateur", columnList = "utilisateur_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"utilisateur", "ville", "quartier"})
@ToString(exclude = {"utilisateur"})
public class Annonce {

    private static final Logger log = LoggerFactory.getLogger(Annonce.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 10, max = 100, message = "Le titre doit contenir entre 10 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 50, max = 2000, message = "La description doit contenir entre 50 et 2000 caractères")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @DecimalMax(value = "999999.99", message = "Le prix ne peut pas dépasser 999 999,99")
    @Digits(integer = 8, fraction = 2, message = "Format de prix invalide")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal prix;

    @NotNull(message = "Le type de logement est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_logement", nullable = false)
    private TypeLogement typeLogement;

    @NotNull(message = "La superficie est obligatoire")
    @Min(value = 10, message = "La superficie doit être d'au moins 10 m²")
    @Max(value = 500, message = "La superficie ne peut pas dépasser 500 m²")
    @Column(nullable = false)
    private Integer superficie;

    @Min(value = 1, message = "Au moins une pièce requise")
    @Max(value = 15, message = "Maximum 15 pièces")
    @Column(name = "nombre_pieces")
    private Integer nombrePieces;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 10, max = 200, message = "L'adresse doit contenir entre 10 et 200 caractères")
    @Column(nullable = false, length = 200)
    private String adresse;

    // Relations
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ville_id", nullable = false)
    @NotNull(message = "La ville est obligatoire")
    private Ville ville;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quartier_id", nullable = false)
    @NotNull(message = "Le quartier est obligatoire")
    private Quartier quartier;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @NotNull(message = "L'utilisateur est obligatoire")
    private User utilisateur;

    // Géolocalisation
    @DecimalMin(value = "-90.0", message = "Latitude invalide")
    @DecimalMax(value = "90.0", message = "Latitude invalide")
    @Digits(integer = 2, fraction = 8, message = "Format de latitude invalide")
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude invalide")
    @DecimalMax(value = "180.0", message = "Longitude invalide")
    @Digits(integer = 3, fraction = 8, message = "Format de longitude invalide")
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    // Images
    @JsonIgnore
    @ElementCollection
    @CollectionTable(
            name = "annonce_images",
            joinColumns = @JoinColumn(name = "annonce_id")
    )
    @Column(name = "url_image", length = 500)
    @Size(max = 10, message = "Maximum 10 images par annonce")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    // Statut et modération
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private StatutAnnonce statut = StatutAnnonce.EN_ATTENTE;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Size(max = 500, message = "Le commentaire de modération ne peut pas dépasser 500 caractères")
    @Column(name = "commentaire_modeation", length = 500)
    private String commentaireModeation;

    // Dates
    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    @Column(name = "date_publication")
    private LocalDateTime datePublication;

    // Statistiques
    @Builder.Default
    @Column(name = "nombre_vues", nullable = false)
    private Long nombreVues = 0L;

    @Builder.Default
    @Column(name = "nombre_favoris", nullable = false)
    private Long nombreFavoris = 0L;

    // Métadonnées
    @Column(name = "meta_title", length = 150)
    private String metaTitle;

    @Column(name = "meta_description", length = 300)
    private String metaDescription;

    // Caractéristiques spécifiques
    @Builder.Default
    @Column(name = "meuble")
    private Boolean meuble = false;

    @Builder.Default
    @Column(name = "parking")
    private Boolean parking = false;

    @Builder.Default
    @Column(name = "balcon")
    private Boolean balcon = false;

    @Builder.Default
    @Column(name = "jardin")
    private Boolean jardin = false;

    @Builder.Default
    @Column(name = "climatisation")
    private Boolean climatisation = false;

    @Builder.Default
    @Column(name = "chauffage")
    private Boolean chauffage = false;

    @Builder.Default
    @Column(name = "internet")
    private Boolean internet = false;

    @Builder.Default
    @Column(name = "animaux_autorises")
    private Boolean animauxAutorises = false;

    // Méthodes utilitaires
    public boolean isPubliee() {
        return statut == StatutAnnonce.APPROUVEE && active;
    }

    public boolean isExpired() {
        return dateExpiration != null && dateExpiration.isBefore(LocalDateTime.now());
    }

    public boolean isEditable() {
        return active && (statut == StatutAnnonce.EN_ATTENTE || statut == StatutAnnonce.REJETEE);
    }

    public void incrementVues() {
        this.nombreVues++;
    }

    public void incrementFavoris() {
        this.nombreFavoris++;
        log.info("Favori increment");
    }

    public void decrementFavoris() {
        if (this.nombreFavoris > 0) {
            this.nombreFavoris--;
        }
    }

    public String getSlug() {
        if (titre == null) return "";
        return titre.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    // Callback JPA
    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (dateExpiration == null) {
            dateExpiration = LocalDateTime.now().plusDays(30); // Expire après 30 jours par défaut
        }
        if (metaTitle == null) {
            metaTitle = titre;
        }
        if (metaDescription == null && description != null) {
            metaDescription = description.length() > 200
                    ? description.substring(0, 200) + "..."
                    : description;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
        if (statut == StatutAnnonce.APPROUVEE && datePublication == null) {
            datePublication = LocalDateTime.now();
        }
    }
}