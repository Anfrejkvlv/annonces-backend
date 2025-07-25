package com.logement.etudiants.entity;

import com.logement.etudiants.entity.Annonce;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "villes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ville {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la ville est obligatoire")
    @Column(nullable = false, unique = true)
    private String nom;

    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "^[0-9]{5}$", message = "Code postal invalide")
    @Column(name = "code_postal", nullable = false)
    private String codePostal;

    @NotBlank(message = "Le pays est obligatoire")
    @Column(nullable = false)
    private String pays;

    @OneToMany(mappedBy = "ville", cascade = CascadeType.ALL)
    private Set<Quartier> quartiers;

    @OneToMany(mappedBy = "ville")
    private Set<Annonce> annonces;
}


