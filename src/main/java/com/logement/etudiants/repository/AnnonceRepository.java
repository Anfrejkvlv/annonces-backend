package com.logement.etudiants.repository;

import com.logement.etudiants.entity.Annonce;
import com.logement.etudiants.enumeration.StatutAnnonce;
import com.logement.etudiants.enumeration.TypeLogement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des annonces
 */

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {

    /**
     * Trouve les annonces par statut avec pagination
     */
    Page<Annonce> findByStatutAndActiveTrue(StatutAnnonce statut, Pageable pageable);

    /**
     * Trouve les annonces approuvées par ville
     */
    @Query("SELECT a FROM Annonce a WHERE a.ville.nom = :ville " +
            "AND a.statut = 'APPROUVEE' AND a.active = true " +
            "ORDER BY a.dateCreation DESC")
    Page<Annonce> findByVilleAndApproved(@Param("ville") String ville, Pageable pageable);

    /**
     * Trouve les annonces par ville et quartier
     */
    @Query("SELECT a FROM Annonce a WHERE a.ville.nom = :ville " +
            "AND a.quartier.nom = :quartier " +
            "AND a.statut = 'APPROUVEE' AND a.active = true " +
            "ORDER BY a.dateCreation DESC")
    Page<Annonce> findByVilleAndQuartierAndApproved(
            @Param("ville") String ville,
            @Param("quartier") String quartier,
            Pageable pageable);

    /**
     * Recherche d'annonces avec filtres multiples
     */
    @Query("SELECT a FROM Annonce a WHERE " +
            "(:typeLogement IS NULL OR a.typeLogement = :typeLogement) " +
            "AND (:prixMin IS NULL OR a.prix >= :prixMin) " +
            "AND (:prixMax IS NULL OR a.prix <= :prixMax) " +
            "AND (:superficieMin IS NULL OR a.superficie >= :superficieMin) " +
            "AND (:superficieMax IS NULL OR a.superficie <= :superficieMax) " +
            "AND (:nombrePiecesMin IS NULL OR a.nombrePieces >= :nombrePiecesMin) " +
            "AND (:nombrePiecesMax IS NULL OR a.nombrePieces <= :nombrePiecesMax) " +
            "AND (:villeId IS NULL OR a.ville.id = :villeId) " +
            "AND (:quartierId IS NULL OR a.quartier.id = :quartierId) " +
            "AND a.statut = 'APPROUVEE' AND a.active = true")
    Page<Annonce> findWithFilters(
            @Param("typeLogement") TypeLogement typeLogement,
            @Param("prixMin") BigDecimal prixMin,
            @Param("prixMax") BigDecimal prixMax,
            @Param("superficieMin") Integer superficieMin,
            @Param("superficieMax") Integer superficieMax,
            @Param("nombrePiecesMin") Integer nombrePiecesMin,
            @Param("nombrePiecesMax") Integer nombrePiecesMax,
            @Param("villeId") Long villeId,
            @Param("quartierId") Long quartierId,
            Pageable pageable);

    /**
     * Recherche textuelle dans les annonces
     */
    @Query("SELECT a FROM Annonce a WHERE " +
            "(LOWER(a.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.adresse) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND a.statut = 'APPROUVEE' AND a.active = true")
    Page<Annonce> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Trouve les annonces d'un utilisateur
     */
    @Query("SELECT a FROM Annonce a WHERE a.utilisateur.id = :userId " +
            "AND a.active = true ORDER BY a.dateCreation DESC")
    Page<Annonce> findByUtilisateurId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Trouve les annonces en attente de modération
     */
    @Query("SELECT a FROM Annonce a WHERE a.statut = 'EN_ATTENTE' OR a.statut='REJETEE' " +
            "ORDER BY a.dateCreation ASC")
    List<Annonce> findPendingAnnonces();

    @Query("SELECT a FROM Annonce a WHERE a.statut = 'EN_ATTENTE' OR a.statut='REJETEE' " +
            "ORDER BY a.dateCreation ASC")
    Page<Annonce> findPendingAnnoncesPages(Pageable pageable);

    /**
     * Trouver des annonces REJETEE
     */
    @Query("SELECT a FROM Annonce a WHERE a.statut = 'REJETEE' ORDER BY a.dateCreation ASC")
    List<Annonce> findAnnoncesRejected();

    /**
     * Trouve les annonces expirées
     */
    @Query("SELECT a FROM Annonce a WHERE a.dateExpiration < CURRENT_TIMESTAMP " +
            "AND a.statut = 'APPROUVEE' AND a.active = true")
    List<Annonce> findExpiredAnnonces();

    /**
     * Compte les annonces par statut
     */
    @Query("SELECT a.statut, COUNT(a) FROM Annonce a WHERE a.active = true GROUP BY a.statut")
    List<Object[]> countAnnoncesByStatut();

    /**
     * Compte les annonces par status
     */
    @Query("SELECT COUNT(a) FROM Annonce a WHERE a.statut=:statut")
    Long countAnnoncesByStatus(@Param("statut") StatutAnnonce statut);

    /**
     * Compte les annonces par type de logement
     */
    @Query("SELECT a.typeLogement, COUNT(a) FROM Annonce a WHERE a.statut = 'APPROUVEE' AND a.active = true GROUP BY a.typeLogement")
    List<Object[]> countAnnoncesByType();

    /**
     * Compte les annonces par ville
     */
    @Query("SELECT a.ville.nom, COUNT(a) FROM Annonce a WHERE a.statut = 'APPROUVEE' AND a.active = true GROUP BY a.ville.nom ORDER BY COUNT(a) DESC")
    List<Object[]> countAnnoncesByVille();

    /**
     * Trouve les annonces les plus vues
     */
    @Query("SELECT a FROM Annonce a WHERE a.statut = 'APPROUVEE' AND a.active = true " +
            "ORDER BY a.nombreVues DESC")
    Page<Annonce> findMostViewedAnnonces(Pageable pageable);

    /**
     * Trouve les annonces similaires (même ville et type)
     */
    @Query("SELECT a FROM Annonce a WHERE a.ville.id = :villeId " +
            "AND a.typeLogement = :typeLogement " +
            "AND a.id <> :annonceId " +
            "AND a.statut = 'APPROUVEE' AND a.active = true " +
            "ORDER BY ABS(a.prix - :prix) ASC")
    List<Annonce> findSimilarAnnonces(
            @Param("annonceId") Long annonceId,
            @Param("villeId") Long villeId,
            @Param("typeLogement") TypeLogement typeLogement,
            @Param("prix") BigDecimal prix,
            Pageable pageable);

    /**
     * Recherche par géolocalisation (rayon)
     */
    @Query("SELECT a FROM Annonce a WHERE " +
            "a.latitude IS NOT NULL AND a.longitude IS NOT NULL " +
            "AND a.statut = 'APPROUVEE' AND a.active = true " +
            "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) * " +
            "cos(radians(a.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(a.latitude)))) <= :radius")
    Page<Annonce> findByLocationWithinRadius(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radius") Integer radius,
            Pageable pageable);

    /**
     * Compte les nouvelles annonces depuis une date
     */
    @Query("SELECT COUNT(a) FROM Annonce a WHERE a.dateCreation >= :dateDebut")
    Long countNewAnnoncesFromDate(@Param("dateDebut") LocalDateTime dateDebut);

    /**
     * Trouve les annonces créées aujourd'hui
     */
    @Query("SELECT a FROM Annonce a WHERE FUNCTION('DATE',a.dateCreation) = CURRENT_DATE")
    List<Annonce> findAnnoncesCreatedToday();
}
