package com.logement.etudiants.entity;

import com.logement.etudiants.entity.Annonce;
import com.logement.etudiants.entity.Ville;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "quartiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quartier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du quartier est obligatoire")
    @Column(nullable = false)
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ville_id", nullable = false)
    private Ville ville;

    @OneToMany(mappedBy = "quartier")
    private Set<Annonce> annonces;
}
