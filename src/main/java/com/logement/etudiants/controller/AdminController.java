package com.logement.etudiants.controller;

import com.logement.etudiants.dto.request.AnnonceSearchRequest;
import com.logement.etudiants.dto.response.*;
import com.logement.etudiants.entity.Annonce;
import com.logement.etudiants.entity.User;
import com.logement.etudiants.enumeration.StatutAnnonce;
import com.logement.etudiants.service.AnnonceService;
import com.logement.etudiants.service.LocationService;
import com.logement.etudiants.service.ModerationService;
import com.logement.etudiants.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour les fonctionnalités d'administration
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administration", description = "APIs d'administration du système")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AdminController {

    private final AnnonceService annonceService;
    private final UserService userService;
    private final LocationService locationService;
    private final ModerationService moderationService;

    /**
     * Tableau de bord administrateur avec statistiques globales
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Tableau de bord admin",
            description = "Récupère les statistiques générales du système")
    public ResponseEntity<StatistiquesResponse> getDashboard() {
        log.info("Récupération du tableau de bord administrateur");

        // Récupérer les statistiques des annonces
        Map<String, Object> annonceStats = annonceService.getAnnonceStatistics();

        // Construire la réponse avec les statistiques
        StatistiquesResponse stats = StatistiquesResponse.builder()
                .totalUtilisateurs((long) userService.getAllUsers().size()) // TODO: Implémenter le vrai comptage
                .totalAnnonces(annonceService.getAnnonces(AnnonceSearchRequest.builder().build(), PageRequest.of(20,20)).getTotalElements())      // TODO: Implémenter le vrai comptage
                .annoncesEnAttente(annonceService.getCountAnnoncesByStatut(StatutAnnonce.EN_ATTENTE))   // TODO: Implémenter le vrai comptage
                .annoncesApprouvees(annonceService.getCountAnnoncesByStatut(StatutAnnonce.APPROUVEE)) // TODO: Implémenter le vrai comptage
                .annoncesRejetees(annonceService.getCountAnnoncesByStatut(StatutAnnonce.REJETEE))    // TODO: Implémenter le vrai comptage
                .nouveauxUtilisateursAujourdhui(userService.getAllUserToday())
                .nouvellesAnnoncesAujourdhui((Long) annonceStats.get("todayCount"))
                .annoncesParType((Map<String, Long>) annonceStats.get("byType"))
                .utilisateursParPays((Map<String, Long>) annonceStats.get("paysCount"))
                .annoncesParVille((Map<String, Long>) annonceStats.get("byVille"))
                .derniereMiseAJour(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(stats);
    }

    /**
     * Liste des annonces en attente de modération
     */
    @GetMapping("/annonces/pending")
    @Operation(summary = "Annonces en attente",
            description = "Récupère toutes les annonces en attente de modération")
    public ResponseEntity<List<AnnonceResponse>> getPendingAnnonces() {
        log.info("Récupération des annonces en attente de modération");

        // TODO: Implémenter la récupération des annonces en attente
        List<AnnonceResponse> pendingAnnonces = List.of();

        return ResponseEntity.ok(pendingAnnonces);
    }

    /**
     * Liste des annonces en attente de modération
     */
    @GetMapping("/stats/pending-count")
    @Operation(summary = "Total annonces en attente",
            description = "Récupère toutes les annonces en attente de modération")
    public Integer getTotalPendingAnnonces() {
        log.info("Récupération des annonces en attente de modération");

        StatistiquesResponse stats= getDashboard().getBody();
        assert stats != null;
        return Math.toIntExact(stats.getAnnoncesEnAttente());
    }


    @GetMapping("/stats/user-registrations")
    @Operation(summary = "Total des utilisateurs inscrits",
            description = "Récupère tout les utilisateurs inscrits")
    public Integer getTotalUserRegistrations() {
        log.info("Récupération des utilisateurs inscrits");

        StatistiquesResponse stats= getDashboard().getBody();
        assert stats != null;
        return Math.toIntExact(stats.getTotalUtilisateurs());
    }

    @GetMapping("/stats/listings-by-city")
    @Operation(summary = "Total annonces par ville",
            description = "Récupère toutes les annonces par ville")
    public Integer getTotalAnnoncesByCity() {
        log.info("Récupération des annonces par ville");
        return 3;
    }

    @GetMapping("/stats/monthly-revenue")
    @Operation(summary = "Total des revenues mensuelles",
            description = "Récupère toutes les revenues mensuelles")
    public Integer getTotalMonthlyRevenue() {
        log.info("Récupération de toutes les revenues mensuelles");
        return 3;
    }

    @GetMapping("/stats/active-users")
    @Operation(summary = "Total utilisateurs actifs",
            description = "Récupère tout utilisateurs actif")
    public Integer getTotalActiveUser() {
        log.info("Récupération utilisateurs actif");
        return 3;
    }

    @GetMapping("/stats/total-listings")
    @Operation(summary = "Total annonces ",
            description = "Récupère toutes les annonces ")
    public Integer getTotalListings() {
        log.info("Récupération des annonces");
        StatistiquesResponse stats= getDashboard().getBody();
        assert stats != null;
        return Math.toIntExact(stats.getTotalAnnonces());
    }

    /**
     * Approuver une annonce
     */
    @PostMapping("/annonces/{id}/approve")
    @Operation(summary = "Approuver une annonce rejetee",
            description = "Approuve une annonce rejetee et la rend visible publiquement")
    public ResponseEntity<MessageResponse> approveAnnonce(
            @PathVariable Long id,
            @RequestBody(required = false) String commentaire,
            @AuthenticationPrincipal User admin) {

        log.info("Approbation de l'annonce ID: {} par l'admin: {}", id, admin.getEmail());

        // TODO: Implémenter l'approbation d'annonce
        annonceService.approveAnnonce(id, commentaire);

        return ResponseEntity.ok(MessageResponse.success("Annonce approuvée avec succès"));
    }

    /**
     * Rejeter une annonce
     */
    @PostMapping("/annonces/{id}/reject")
    @Operation(summary = "Rejeter une annonce",
            description = "Rejette une annonce avec un commentaire explicatif")
    public ResponseEntity<MessageResponse> rejectAnnonce(
            @PathVariable Long id,
            @RequestBody String raisonRejet,
            @AuthenticationPrincipal User admin) {

        log.info("Rejet de l'annonce ID: {} par l'admin: {}", id, admin.getEmail());

        // TODO: Implémenter le rejet d'annonce

        return ResponseEntity.ok(MessageResponse.success("Annonce rejetée"));
    }

    /**
     * Liste de tous les utilisateurs
     */
    @GetMapping("/users")
    @Operation(summary = "Liste des utilisateurs",
            description = "Récupère la liste paginée de tous les utilisateurs")
    public ResponseEntity<List<UserResponse>> getAllUsers(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(required = false) String search
    ) {

        //log.info("Récupération de la liste des utilisateurs (page: {}, taille: {}, recherche: {})", page, size, search);

        // TODO: Implémenter la récupération paginée des utilisateurs
//        PageResponse<UserResponse> users = PageResponse.<UserResponse>builder()
//                .content(userService.getAllUsers())
//                .page(page)
//                .size(size)
//                .totalElements(0L)
//                .totalPages(0)
//                .first(true)
//                .last(true)
//                .empty(true)
//                .build();

        //PageResponse<UserResponse> users=userService.getAllUsers();

        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Désactiver un utilisateur
     */
    @PostMapping("/users/{id}/deactivate")
    @Operation(summary = "Désactiver un utilisateur",
            description = "Désactive un compte utilisateur")
    public ResponseEntity<MessageResponse> deactivateUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin) {

        log.info("Désactivation de l'utilisateur ID: {} par l'admin: {}", id, admin.getEmail());

        // TODO: Implémenter la désactivation d'utilisateur

        return ResponseEntity.ok(MessageResponse.success("Utilisateur désactivé"));
    }

    /**
     * Réactiver un utilisateur
     */
    @PostMapping("/users/{id}/activate")
    @Operation(summary = "Réactiver un utilisateur",
            description = "Réactive un compte utilisateur désactivé")
    public ResponseEntity<MessageResponse> activateUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin) {

        log.info("Réactivation de l'utilisateur ID: {} par l'admin: {}", id, admin.getEmail());

        // TODO: Implémenter la réactivation d'utilisateur

        return ResponseEntity.ok(MessageResponse.success("Utilisateur réactivé"));
    }

    /**
     * Rapport de modération pour une annonce
     */
    @GetMapping("/annonces/{id}/moderation-report")
    @Operation(summary = "Rapport de modération",
            description = "Génère un rapport détaillé de modération pour une annonce")
    public ResponseEntity<Map<String, Object>> getModerationReport(@PathVariable Long id) {

        log.info("Génération du rapport de modération pour l'annonce ID: {}", id);

        // TODO: Implémenter la génération du rapport de modération
        Map<String, Object> report = Map.of(
                "annonceId", id,
                "rapport", "Rapport de modération détaillé...",
                "scoreConfiance", 85,
                "recommandation", "Approuver"
        );

        return ResponseEntity.ok(report);
    }

}
