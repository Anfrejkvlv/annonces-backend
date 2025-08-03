package com.logement.etudiants.repository;

import com.logement.etudiants.entity.Quartier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des quartiers
 */

@Repository
public interface QuartierRepository extends JpaRepository<Quartier, Long> {

    /**
     * Trouve les quartiers d'une ville
     */
    List<Quartier> findByVille_IdOrderByNomAsc(Long villeId);

    /**
     * Trouve un quartier par nom et ville
     */
    Optional<Quartier> findByNomAndVille_Id(String nom, Long villeId);

    /**
     * Trouve les quartiers actifs d'une ville
     */
    List<Quartier> findByVille_IdAndActiveTrueOrderByNomAsc(Long villeId);

    /**
     * Recherche de quartiers par terme
     */
    @Query("SELECT q FROM Quartier q WHERE " +
            "LOWER(q.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Quartier> findBySearchTerm(@Param("searchTerm") String searchTerm);

    /**
     * Trouve les quartiers avec le plus d'annonces pour une ville
     */
    @Query("SELECT q FROM Quartier q WHERE q.ville.id = :villeId AND q.active = true " +
            "ORDER BY q.nombreAnnonces DESC")
    List<Quartier> findQuartiersWithMostAnnonces(@Param("villeId") Long villeId, Pageable pageable);
}
