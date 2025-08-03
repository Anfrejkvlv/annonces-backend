package com.logement.etudiants.service;

import com.logement.etudiants.dto.request.AnnonceRequest;
import com.logement.etudiants.dto.request.AnnonceSearchRequest;
import com.logement.etudiants.dto.response.AnnonceResponse;
import com.logement.etudiants.dto.response.PageResponse;
import com.logement.etudiants.entity.Annonce;
import com.logement.etudiants.entity.Quartier;
import com.logement.etudiants.entity.User;
import com.logement.etudiants.entity.Ville;
import com.logement.etudiants.enumeration.StatutAnnonce;
import com.logement.etudiants.exception.BusinessException;
import com.logement.etudiants.exception.ResourceNotFoundException;
import com.logement.etudiants.mapper.AnnonceMapper;
import com.logement.etudiants.repository.AnnonceRepository;
import com.logement.etudiants.repository.QuartierRepository;
import com.logement.etudiants.repository.UserRepository;
import com.logement.etudiants.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de gestion des annonces
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final VilleRepository villeRepository;
    private final QuartierRepository quartierRepository;
    private final UserRepository userRepository;
    private final AnnonceMapper annonceMapper;
    private final ModerationService moderationService;
    private final NotificationService notificationService;

    /**
     * Crée une nouvelle annonce
     */
    @Transactional
    public AnnonceResponse createAnnonce(AnnonceRequest request, User currentUser) {
        log.info("Création d'une nouvelle annonce par l'utilisateur: {} (ID: {})",
                currentUser.getEmail(), currentUser.getId());

        // Valider et récupérer la ville
        Ville ville = villeRepository.findById(request.getVilleId())
                .orElseThrow(() -> new ResourceNotFoundException("Ville non trouvée avec l'ID: " + request.getVilleId()));

        // Valider et récupérer le quartier
        Quartier quartier = quartierRepository.findById(request.getQuartierId())
                .orElseThrow(() -> new ResourceNotFoundException("Quartier non trouvé avec l'ID: " + request.getQuartierId()));

        // Vérifier que le quartier appartient à la ville
        if (!quartier.getVille().getId().equals(ville.getId())) {
            throw new BusinessException("Le quartier ne correspond pas à la ville sélectionnée");
        }

        // Construire l'annonce
        Annonce annonce = Annonce.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .prix(request.getPrix())
                .typeLogement(request.getTypeLogement())
                .superficie(request.getSuperficie())
                .nombrePieces(request.getNombrePieces())
                .adresse(request.getAdresse())
                .ville(ville)
                .quartier(quartier)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .images(request.getImages())
                .utilisateur(currentUser)
                .statut(StatutAnnonce.EN_ATTENTE)
                .dateExpiration(LocalDateTime.now().plusDays(30))
                // Caractéristiques
                .meuble(request.getMeuble() != null ? request.getMeuble() : false)
                .parking(request.getParking() != null ? request.getParking() : false)
                .balcon(request.getBalcon() != null ? request.getBalcon() : false)
                .jardin(request.getJardin() != null ? request.getJardin() : false)
                .climatisation(request.getClimatisation() != null ? request.getClimatisation() : false)
                .chauffage(request.getChauffage() != null ? request.getChauffage() : false)
                .internet(request.getInternet() != null ? request.getInternet() : false)
                .animauxAutorises(request.getAnimauxAutorises() != null ? request.getAnimauxAutorises() : false)
                // Métadonnées SEO
                .metaTitle(request.getMetaTitle())
                .metaDescription(request.getMetaDescription())
                .build();

        // Appliquer la modération automatique
        StatutAnnonce statutModeration = moderationService.moderateAnnonce(annonce);
        annonce.setStatut(statutModeration);

        // Sauvegarder l'annonce
        Annonce savedAnnonce = annonceRepository.save(annonce);

        // Mettre à jour les compteurs
        ville.incrementNombreAnnonces();
        quartier.incrementNombreAnnonces();
        villeRepository.save(ville);
        quartierRepository.save(quartier);

        log.info("Annonce créée avec succès - ID: {}, Statut: {}, Utilisateur: {}",
                savedAnnonce.getId(), savedAnnonce.getStatut(), currentUser.getEmail());

        // Envoyer notifications
        try {
            if (statutModeration == StatutAnnonce.APPROUVEE) {
                notificationService.sendAnnonceApprovedNotification(savedAnnonce);
            } else if (statutModeration == StatutAnnonce.REJETEE) {
                notificationService.sendAnnonceRejectedNotification(savedAnnonce);
            }
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi de notification pour l'annonce {}: {}",
                    savedAnnonce.getId(), e.getMessage());
        }

        return annonceMapper.toResponse(savedAnnonce);
    }

    /**
     * Récupère les annonces publiques avec filtres
     */
    public PageResponse<AnnonceResponse> getAnnonces(AnnonceSearchRequest searchRequest, Pageable pageable) {
        log.debug("Recherche d'annonces avec filtres: {}", searchRequest);

        // Construire la requête avec filtres
        Page<Annonce> annoncesPage;

        if (searchRequest.getSearchTerm() != null && !searchRequest.getSearchTerm().trim().isEmpty()) {
            // Recherche textuelle
            annoncesPage = annonceRepository.findBySearchTerm(searchRequest.getSearchTerm().trim(), pageable);
        } else if (searchRequest.getLatitude() != null && searchRequest.getLongitude() != null && searchRequest.getRayonKm() != null) {
            // Recherche géographique
            annoncesPage = annonceRepository.findByLocationWithinRadius(
                    searchRequest.getLatitude(),
                    searchRequest.getLongitude(),
                    searchRequest.getRayonKm(),
                    pageable);
        } else {
            // Recherche avec filtres
            annoncesPage = annonceRepository.findWithFilters(
                    searchRequest.getTypeLogement(),
                    searchRequest.getPrixMin(),
                    searchRequest.getPrixMax(),
                    searchRequest.getSuperficieMin(),
                    searchRequest.getSuperficieMax(),
                    searchRequest.getNombrePiecesMin(),
                    searchRequest.getNombrePiecesMax(),
                    searchRequest.getVilleId(),
                    searchRequest.getQuartierId(),
                    pageable);
        }

        List<AnnonceResponse> annonceResponses = annoncesPage.getContent()
                .stream()
                .map(annonceMapper::toResponse)
                .toList();

        // Construire la réponse paginée
        Map<String, Object> filters = new HashMap<>();
        filters.put("typeLogement", searchRequest.getTypeLogement());
        filters.put("villeId", searchRequest.getVilleId());
        filters.put("quartierId", searchRequest.getQuartierId());
        filters.put("prixMin", searchRequest.getPrixMin());
        filters.put("prixMax", searchRequest.getPrixMax());

        return PageResponse.<AnnonceResponse>builder()
                .content(annonceResponses)
                .page(annoncesPage.getNumber())
                .size(annoncesPage.getSize())
                .totalElements(annoncesPage.getTotalElements())
                .totalPages(annoncesPage.getTotalPages())
                .first(annoncesPage.isFirst())
                .last(annoncesPage.isLast())
                .empty(annoncesPage.isEmpty())
                .filters(filters)
                .sortBy(searchRequest.getSortBy())
                .sortDirection(searchRequest.getSortDirection())
                .build();
    }

    /**
     * Récupère une annonce par son ID avec incrément des vues
     */
    @Transactional
    public AnnonceResponse getAnnonceById(Long id, boolean incrementViews) {
        log.debug("Récupération de l'annonce ID: {}", id);

        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce non trouvée avec l'ID: " + id));

        // Vérifier que l'annonce est visible publiquement
        if (!annonce.isPubliee()) {
            throw new ResourceNotFoundException("Annonce non disponible");
        }

        // Incrémenter le nombre de vues
        if (incrementViews) {
            annonce.incrementVues();
            annonceRepository.save(annonce);
        }

        return annonceMapper.toResponse(annonce);
    }

    /**
     * Met à jour une annonce existante
     */
    @Transactional
    public AnnonceResponse updateAnnonce(Long id, AnnonceRequest request, User currentUser) {
        log.info("Mise à jour de l'annonce ID: {} par l'utilisateur: {}", id, currentUser.getEmail());

        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce non trouvée avec l'ID: " + id));

        // Vérifier les permissions
        if (!annonce.getUtilisateur().getId().equals(currentUser.getId()) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cette annonce");
        }

        // Vérifier que l'annonce est modifiable
        if (!annonce.isEditable() && !currentUser.isAdmin()) {
            throw new BusinessException("Cette annonce ne peut plus être modifiée");
        }

        // Valider les nouvelles données
        Ville ville = villeRepository.findById(request.getVilleId())
                .orElseThrow(() -> new ResourceNotFoundException("Ville non trouvée"));

        Quartier quartier = quartierRepository.findById(request.getQuartierId())
                .orElseThrow(() -> new ResourceNotFoundException("Quartier non trouvé"));

        if (!quartier.getVille().getId().equals(ville.getId())) {
            throw new BusinessException("Le quartier ne correspond pas à la ville sélectionnée");
        }

        // Mettre à jour les champs
        annonce.setTitre(request.getTitre());
        annonce.setDescription(request.getDescription());
        annonce.setPrix(request.getPrix());
        annonce.setTypeLogement(request.getTypeLogement());
        annonce.setSuperficie(request.getSuperficie());
        annonce.setNombrePieces(request.getNombrePieces());
        annonce.setAdresse(request.getAdresse());
        annonce.setVille(ville);
        annonce.setQuartier(quartier);
        annonce.setLatitude(request.getLatitude());
        annonce.setLongitude(request.getLongitude());
        annonce.setImages(request.getImages());

        // Caractéristiques
        annonce.setMeuble(request.getMeuble() != null ? request.getMeuble() : false);
        annonce.setParking(request.getParking() != null ? request.getParking() : false);
        annonce.setBalcon(request.getBalcon() != null ? request.getBalcon() : false);
        annonce.setJardin(request.getJardin() != null ? request.getJardin() : false);
        annonce.setClimatisation(request.getClimatisation() != null ? request.getClimatisation() : false);
        annonce.setChauffage(request.getChauffage() != null ? request.getChauffage() : false);
        annonce.setInternet(request.getInternet() != null ? request.getInternet() : false);
        annonce.setAnimauxAutorises(request.getAnimauxAutorises() != null ? request.getAnimauxAutorises() : false);

        // Remettre en attente de modération si ce n'est pas un admin
        if (!currentUser.isAdmin()) {
            annonce.setStatut(StatutAnnonce.EN_ATTENTE);
            annonce.setCommentaireModeation(null);
        }

        Annonce updatedAnnonce = annonceRepository.save(annonce);

        log.info("Annonce mise à jour avec succès - ID: {}", updatedAnnonce.getId());

        return annonceMapper.toResponse(updatedAnnonce);
    }

    /**
     * Supprime une annonce (soft delete)
     */
    @Transactional
    public void deleteAnnonce(Long id, User currentUser) {
        log.info("Suppression de l'annonce ID: {} par l'utilisateur: {}", id, currentUser.getEmail());

        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce non trouvée avec l'ID: " + id));

        // Vérifier les permissions
        if (!annonce.getUtilisateur().getId().equals(currentUser.getId()) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer cette annonce");
        }

        // Soft delete
        annonce.setActive(false);
        annonce.setStatut(StatutAnnonce.ARCHIVEE);
        annonceRepository.save(annonce);

        // Mettre à jour les compteurs
        annonce.getVille().decrementNombreAnnonces();
        annonce.getQuartier().decrementNombreAnnonces();
        villeRepository.save(annonce.getVille());
        quartierRepository.save(annonce.getQuartier());

        log.info("Annonce supprimée avec succès - ID: {}", id);
    }

    /**
     * Récupère les annonces d'un utilisateur
     */
    public PageResponse<AnnonceResponse> getAnnoncesByUser(Long userId, Pageable pageable) {
        log.debug("Récupération des annonces pour l'utilisateur ID: {}", userId);

        Page<Annonce> annoncesPage = annonceRepository.findByUtilisateurId(userId, pageable);

        List<AnnonceResponse> annonceResponses = annoncesPage.getContent()
                .stream()
                .map(annonceMapper::toResponse)
                .toList();

        return PageResponse.<AnnonceResponse>builder()
                .content(annonceResponses)
                .page(annoncesPage.getNumber())
                .size(annoncesPage.getSize())
                .totalElements(annoncesPage.getTotalElements())
                .totalPages(annoncesPage.getTotalPages())
                .first(annoncesPage.isFirst())
                .last(annoncesPage.isLast())
                .empty(annoncesPage.isEmpty())
                .build();
    }

    /**
     * Récupère les annonces similaires
     */
    public List<AnnonceResponse> getSimilarAnnonces(Long annonceId, int limit) {
        log.debug("Recherche d'annonces similaires à l'annonce ID: {}", annonceId);

        Annonce annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce non trouvée"));

        if (!annonce.isPubliee()) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<Annonce> similarAnnonces = annonceRepository.findSimilarAnnonces(
                annonceId,
                annonce.getVille().getId(),
                annonce.getTypeLogement(),
                annonce.getPrix(),
                pageable);

        return similarAnnonces.stream()
                .map(annonceMapper::toResponse)
                .toList();
    }

    /**
     * Marque une annonce comme expirée
     */
    @Transactional
    public void expireAnnonce(Long id) {
        log.info("Expiration de l'annonce ID: {}", id);

        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce non trouvée"));

        annonce.setStatut(StatutAnnonce.EXPIREE);
        annonce.setActive(false);
        annonceRepository.save(annonce);

        // Notifier l'utilisateur
        try {
            notificationService.sendAnnonceExpiredNotification(annonce);
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi de notification d'expiration pour l'annonce {}: {}",
                    id, e.getMessage());
        }
    }

    /**
     * Traite les annonces expirées automatiquement
     */
    @Transactional
    public void processExpiredAnnonces() {
        log.info("Traitement des annonces expirées");

        List<Annonce> expiredAnnonces = annonceRepository.findExpiredAnnonces();

        for (Annonce annonce : expiredAnnonces) {
            expireAnnonce(annonce.getId());
        }

        log.info("Traitement terminé - {} annonces expirées", expiredAnnonces.size());
    }

    /**
     * Récupère les statistiques des annonces
     */
    public Map<String, Object> getAnnonceStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Compteurs par statut
        List<Object[]> statusCounts = annonceRepository.countAnnoncesByStatut();
        Map<String, Long> statusCountMap = new HashMap<>();
        for (Object[] row : statusCounts) {
            statusCountMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("byStatus", statusCountMap);

        // Compteurs par type
        List<Object[]> typeCounts = annonceRepository.countAnnoncesByType();
        Map<String, Long> typeCountMap = new HashMap<>();
        for (Object[] row : typeCounts) {
            typeCountMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("byType", typeCountMap);

        // Compteurs par ville
        List<Object[]> villeCounts = annonceRepository.countAnnoncesByVille();
        Map<String, Long> villeCountMap = new HashMap<>();
        for (Object[] row : villeCounts) {
            villeCountMap.put((String) row[0], (Long) row[1]);
        }
        stats.put("byVille", villeCountMap);

        // Utilisateur par pays
        List<Object[]> paysCount=userRepository.countUsersByPays();
        Map<String,Long> paysCountMap=new HashMap<>();
        for(Object[] obj: paysCount ){
            paysCountMap.put((String) obj[0], (Long) obj[1]);
        }
        stats.put("paysCount", paysCountMap);


        // Nouvelles annonces aujourd'hui
        List<Annonce> todayAnnonces = annonceRepository.findAnnoncesCreatedToday();
        if (todayAnnonces.isEmpty()) {
            log.info("Aucune annonce n'a ete ajouter aujourd'hui ");
        }
        stats.put("todayCount", (long)todayAnnonces.size());

        // Nouvelles annonces aujourd'hui
        List<Annonce> expiredAnnonces = annonceRepository.findExpiredAnnonces();
        if (expiredAnnonces.isEmpty()) {
            log.info("Aucune annonce n'a ete ajouter aujourd'hui ");
        }
        stats.put("expiredCount", (long)expiredAnnonces.size());

        return stats;
    }

    public Long getCountAnnoncesByStatut(StatutAnnonce statut) {
        return annonceRepository.countAnnoncesByStatus(statut);
    }

    /**
     * Mise en favori d'une annonce
     */
    @Transactional
    public Annonce miseEnFavori(Long id, boolean favori,User currentUser){
        Annonce annonce=annonceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce non trouvée"));

        if (favori) {
            annonce.incrementFavoris();
            log.info("Favori incremented");
        } else  {
            annonce.decrementFavoris();
            log.info("Favori decrement");
        }
        annonce=annonceRepository.save(annonce);
        return annonce;
    }


    /**
     * Approuver une annonce ayant un status REJETEE OU EN ATTENTE
     */
    public void approveAnnonce(Long id, String commentaire){
        List<Annonce> annoncesList= annonceRepository.findAnnoncesRejected();
        if (annoncesList.isEmpty()) {
            throw new ResourceNotFoundException("Aucune annonce rejete trouver");
        }
        Annonce annonce= annoncesList.stream()
                    .filter(annonce1 -> annonce1
                            .getId().equals(id)).findFirst().get();
        annonce.setStatut(StatutAnnonce.APPROUVEE);
        annonce.setCommentaireModeation(commentaire);
        annonce=annonceRepository.save(annonce);
    }

}

