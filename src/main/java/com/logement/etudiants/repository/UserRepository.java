package com.logement.etudiants.repository;

import com.logement.etudiants.entity.Annonce;
import com.logement.etudiants.entity.User;
import com.logement.etudiants.enumeration.Role;
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
import java.util.Optional;

/**
 * Repository pour la gestion des utilisateurs
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son email
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Trouve les utilisateurs actifs par pays
     */
    @Query("SELECT u FROM User u WHERE u.pays = :pays AND u.active = true AND u.emailVerifie = true")
    List<User> findActiveUsersByPays(@Param("pays") String pays);

    /**
     * Trouve les utilisateurs par rôle
     */
    List<User> findByRole(Role role);

    /**
     * Trouve les utilisateurs inactifs depuis une certaine date
     */
    @Query("SELECT u FROM User u WHERE u.derniereConnexion < :dateLimit OR u.derniereConnexion IS NULL")
    List<User> findInactiveUsers(@Param("dateLimit") LocalDateTime dateLimit);

    /**
     * Compte les nouveaux utilisateurs depuis une date
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.dateCreation >= :dateDebut")
    Long countNewUsersFromDate(@Param("dateDebut") LocalDateTime dateDebut);

    /**
     * Trouve les utilisateurs créés aujourd'hui
     */
    @Query("SELECT u FROM User u WHERE FUNCTION( 'DATE', u.dateCreation) = CURRENT_DATE")
    List<User> findUsersCreatedToday();

    /**
     * Trouve les utilisateurs avec des comptes verrouillés
     */
    @Query("SELECT u FROM User u WHERE u.compteVerrouilleJusqu IS NOT NULL AND u.compteVerrouilleJusqu > CURRENT_TIMESTAMP")
    List<User> findLockedUsers();

    /**
     * Trouve les utilisateurs par terme de recherche
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Trouve tous les utilisateurs
     */
    @Query("SELECT u FROM User u")
    Page<User> findAllUsers( Pageable pageable);

    /**
     * Recherche d'annonces avec filtres multiples
     */
    @Query("SELECT a FROM User a WHERE " +
            "(:active IS NULL OR a.active = :active) " +
            "AND (:pays IS NULL OR a.pays = :pays) " +
            "AND (:role IS NULL OR a.role= : role)")
    Page<User> findUsersWithFilters(
            @Param("role") Role role,
            @Param("pays") String pays,
            @Param("active") Boolean active,
            Pageable pageable);

    /**
     * Trouve un utilisateur par token de réinitialisation
     */
    @Query("SELECT u FROM User u WHERE u.tokenResetPassword = :token AND u.tokenResetExpiration > CURRENT_TIMESTAMP")
    Optional<User> findByValidResetToken(@Param("token") String token);

    /**
     * Compte les utilisateurs par pays
     */
    @Query("SELECT u.pays, COUNT(u) FROM User u WHERE u.active = true GROUP BY u.pays ORDER BY COUNT(u) DESC")
    List<Object[]> countUsersByPays();

    /**
     * Trouve les utilisateurs avec email non vérifié depuis plus de X jours
     */
    @Query("SELECT u FROM User u WHERE u.emailVerifie = false AND u.dateCreation < :dateLimit")
    List<User> findUnverifiedUsersOlderThan(@Param("dateLimit") LocalDateTime dateLimit);

    /**
     * Trouve les utilisateurs les plus actifs (avec le plus d'annonces)
     */
    @Query("SELECT u FROM User u LEFT JOIN u.annonces a " +
            "WHERE u.active = true " +
            "GROUP BY u " +
            "ORDER BY COUNT(a) DESC")
    Page<User> findMostActiveUsers(Pageable pageable);
}
