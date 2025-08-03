package com.logement.etudiants.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logement.etudiants.entity.Annonce;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité Ville représentant une ville
 */
@Entity
@Table(name = "villes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ville_nom_pays", columnNames = {"nom", "pays"})
        },
        indexes = {
                @Index(name = "idx_ville_nom", columnList = "nom"),
                @Index(name = "idx_ville_pays", columnList = "pays"),
                @Index(name = "idx_ville_code_postal", columnList = "code_postal")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"quartiers", "annonces"})
@ToString(exclude = {"quartiers", "annonces"})
public class Ville {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la ville est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom de la ville doit contenir entre 2 et 100 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le nom de la ville ne peut contenir que des lettres, espaces, apostrophes et tirets")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "^[0-9]{5}$", message = "Le code postal doit contenir exactement 5 chiffres")
    @Column(name = "code_postal", nullable = false, length = 5)
    private String codePostal;

    @NotBlank(message = "Le pays est obligatoire")
    @Size(min = 2, max = 100, message = "Le pays doit contenir entre 2 et 100 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le pays ne peut contenir que des lettres, espaces, apostrophes et tirets")
    @Column(nullable = false, length = 100)
    private String pays;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    // Coordonnées géographiques de la ville (centre-ville)
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
    @OneToMany(mappedBy = "ville", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Quartier> quartiers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "ville", fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Annonce> annonces = new HashSet<>();

    // Méthodes utilitaires
    public String getNomComplet() {
        return nom + " (" + codePostal + ")";
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



