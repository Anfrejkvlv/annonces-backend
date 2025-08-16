package com.logement.etudiants.repository;

import com.logement.etudiants.entity.Ville;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des villes
 */

@Repository
public interface VilleRepository extends JpaRepository<Ville, Long> {

    /**
     * Trouve une ville par son nom
     */
    Optional<Ville> findByNom(String nom);

    /**
     * Trouve une ville par nom et pays
     */
    Optional<Ville> findByNomAndPays(String nom, String pays);

    /**
     * Trouve les villes par pays
     */
    List<Ville> findByPaysOrderByNomAsc(String pays);

    /**
     * Trouve les villes actives
     */
    List<Ville> findByActiveTrueOrderByNomAsc();

    /**
     * Trouve les villes par code postal
     */
    Optional<Ville> findByCodePostal(String codePostal);

    /**
     * Recherche de villes par terme
     */
    @Query("SELECT v FROM Ville v WHERE " +
            "LOWER(v.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(v.pays) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Ville> findBySearchTerm(@Param("searchTerm") String searchTerm,Pageable pageable);

    /**
     * Trouve les villes avec le plus d'annonces
     */
    @Query("SELECT v FROM Ville v WHERE v.active = true " +
            "ORDER BY v.nombreAnnonces DESC")
    List<Ville> findVillesWithMostAnnonces(Pageable pageable);


    @Query("SELECT v FROM Ville v")
    Page<Ville> findWithFilter(Pageable pageable);
}

