package com.logement.etudiants.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logement.etudiants.entity.Annonce;
import com.logement.etudiants.entity.Ville;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
/**
 * Entité Quartier représentant un quartier d'une ville
 */
@Entity
@Table(name = "quartiers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_quartier_nom_ville", columnNames = {"nom", "ville_id"})
        },
        indexes = {
                @Index(name = "idx_quartier_nom", columnList = "nom"),
                @Index(name = "idx_quartier_ville", columnList = "ville_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"ville", "annonces"})
@ToString(exclude = {"ville", "annonces"})
public class Quartier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du quartier est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom du quartier doit contenir entre 2 et 100 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ0-9\\s'-]+$", message = "Le nom du quartier ne peut contenir que des lettres, chiffres, espaces, apostrophes et tirets")
    @Column(nullable = false, length = 100)
    private String nom;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    // Coordonnées géographiques du quartier (centre)
    @DecimalMin(value = "-90.0", message = "Latitude invalide")
    @DecimalMax(value = "90.0", message = "Latitude invalide")
    @Digits(integer = 2, fraction = 8, message = "Format de latitude invalide")
    @Column(precision = 10, scale = 8)
    private java.math.BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude invalide")
    @DecimalMax(value = "180.0", message = "Longitude invalide")
    @Digits(integer = 3, fraction = 8, message = "Format de longitude invalide")
    @Column(precision = 11, scale = 8)
    private java.math.BigDecimal longitude;

    // Statistiques
    @Builder.Default
    @Column(name = "nombre_annonces", nullable = false)
    private Long nombreAnnonces = 0L;

    // Dates
    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Relations
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ville_id", nullable = false)
    @NotNull(message = "La ville est obligatoire")
    private Ville ville;

    @JsonIgnore
    @OneToMany(mappedBy = "quartier", fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Annonce> annonces = new HashSet<>();

    // Méthodes utilitaires
    public String getNomComplet() {
        return nom + " - " + (ville != null ? ville.getNom() : "");
    }

    public void incrementNombreAnnonces() {
        this.nombreAnnonces++;
    }

    public void decrementNombreAnnonces() {
        if (this.nombreAnnonces > 0) {
            this.nombreAnnonces--;
        }
    }

    // Callback JPA
    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}

