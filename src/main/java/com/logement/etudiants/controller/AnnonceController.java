package com.logement.etudiants.controller;

import com.logement.etudiants.dto.request.AnnonceRequest;
import com.logement.etudiants.dto.request.AnnonceSearchRequest;
import com.logement.etudiants.dto.response.AnnonceResponse;
import com.logement.etudiants.dto.response.MessageResponse;
import com.logement.etudiants.dto.response.PageResponse;
import com.logement.etudiants.entity.Annonce;
import com.logement.etudiants.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


/**
 * Contrôleur pour la gestion des annonces
 */
@RestController
@RequestMapping("/api/v1/annonces")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Annonces", description = "Gestion des annonces de logements")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
class AnnonceController {

    private final com.logement.etudiants.service.AnnonceService annonceService;

    /**
     * Récupère toutes les annonces publiques avec filtres optionnels
     */
    @GetMapping("/public")
    @Operation(summary = "Récupérer les annonces publiques",
            description = "Récupère toutes les annonces approuvées avec filtres optionnels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Annonces récupérées avec succès")
    })
    public ResponseEntity<PageResponse<AnnonceResponse>> getAnnonces(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String typeLogement,
            @RequestParam(required = false) Long villeId,
            @RequestParam(required = false) Long quartierId,
            @RequestParam(required = false) java.math.BigDecimal prixMin,
            @RequestParam(required = false) java.math.BigDecimal prixMax,
            @RequestParam(required = false) Integer superficieMin,
            @RequestParam(required = false) Integer superficieMax,
            @RequestParam(required = false) Integer nombrePiecesMin,
            @RequestParam(required = false) Integer nombrePiecesMax,
            @RequestParam(required = false) Boolean meuble,
            @RequestParam(required = false) Boolean parking,
            @RequestParam(required = false) Boolean balcon,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Construire la requête de recherche
        AnnonceSearchRequest searchRequest = AnnonceSearchRequest.builder()
                .searchTerm(searchTerm)
                .typeLogement(typeLogement != null ?
                        com.logement.etudiants.enumeration.TypeLogement.valueOf(typeLogement.toUpperCase()) : null)
                .villeId(villeId)
                .quartierId(quartierId)
                .prixMin(prixMin)
                .prixMax(prixMax)
                .superficieMin(superficieMin)
                .superficieMax(superficieMax)
                .nombrePiecesMin(nombrePiecesMin)
                .nombrePiecesMax(nombrePiecesMax)
                .meuble(meuble)
                .parking(parking)
                .balcon(balcon)
                .sortBy(sortBy != null ? sortBy : "dateCreation")
                .sortDirection(sortDirection)
                .build();

        // Créer le Pageable
        org.springframework.data.domain.Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                org.springframework.data.domain.Sort.by(searchRequest.getSortBy()).ascending() :
                org.springframework.data.domain.Sort.by(searchRequest.getSortBy()).descending();

        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page, size, sort);

        PageResponse<AnnonceResponse> response = annonceService.getAnnonces(searchRequest, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère une annonce par son ID
     */
    @GetMapping("/public/{id}")
    @Operation(summary = "Récupérer une annonce par ID",
            description = "Récupère une annonce spécifique et incrémente le nombre de vues")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Annonce trouvée"),
            @ApiResponse(responseCode = "404", description = "Annonce non trouvée")
    })
    public ResponseEntity<AnnonceResponse> getAnnonceById(@PathVariable Long id) {
        log.info("Récupération de l'annonce ID: {}", id);

        AnnonceResponse annonce = annonceService.getAnnonceById(id, true);

        return ResponseEntity.ok(annonce);
    }

    /**
     * Crée une nouvelle annonce
     */
    @PostMapping(value = "/jsoncreate", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une nouvelle annonce sans images",
            description = "Crée une nouvell" +
                    "e annonce de logement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Annonce créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    public ResponseEntity<AnnonceResponse> createAnnonce(
            @Valid @RequestBody AnnonceRequest request,
            @AuthenticationPrincipal
            User currentUser) {
        log.info("DONNEES {}",request);

        log.info("Création d'annonce demandée par l'utilisateur: {}", currentUser.getEmail());

        AnnonceResponse response = annonceService.createAnnonce(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/imagecreate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Créer une nouvelle annonce avec images",
            description = "Crée une nouvell" +
                    "e annonce de logement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Annonce créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    public ResponseEntity<AnnonceResponse> createAnnonceWithImages(
            @RequestPart("annonce") AnnonceRequest annonceRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal User currentUser) {


        // ✅ LOG DÉTAILLÉ POUR DEBUG
        log.info("🔍 DEBUGGING PAYLOAD:");
        log.info("  - Titre: {}", annonceRequest.getTitre());
        log.info("  - Prix: {} (type: {})", annonceRequest.getPrix(),
                annonceRequest.getPrix() != null ? annonceRequest.getPrix().getClass().getSimpleName() : "null");
        log.info("  - Superficie: {}", annonceRequest.getSuperficie());
        log.info("  - Latitude: {}", annonceRequest.getLatitude());
        log.info("  - Longitude: {}", annonceRequest.getLongitude());

        // Log du JSON reçu pour debug
        log.info("📋 Données reçues: {}", annonceRequest);

        if (images != null) {
            log.info("🖼️ Images reçues:");
            images.forEach(img -> log.info("  - {} ({} bytes)",
                    img.getOriginalFilename(), img.getSize()));
        }

        log.info("Création d'annonce avec {} images pour l'utilisateur: {}",
                images != null ? images.size() : 0, currentUser.getEmail());

        AnnonceResponse response = annonceService.createAnnonceWithImages(
                annonceRequest, images, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Alternative: endpoint pour création sans images
    @PostMapping(value = "/create-simple", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<AnnonceResponse> createAnnonceSimple(
            @Valid @RequestBody AnnonceRequest annonceRequest,
            @AuthenticationPrincipal User currentUser) {

        AnnonceResponse response = annonceService.createAnnonce(annonceRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Met à jour une annonce existante
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "Modifier une annonce",
            description = "Met à jour une annonce existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Annonce mise à jour avec succès"),
            @ApiResponse(responseCode = "403", description = "Non autorisé à modifier cette annonce"),
            @ApiResponse(responseCode = "404", description = "Annonce non trouvée")
    })
    public ResponseEntity<AnnonceResponse> updateAnnonce(
            @PathVariable Long id,
            @Valid @RequestBody AnnonceRequest request,
            @AuthenticationPrincipal User currentUser
    ) {

        log.info("Modification d'annonce ID: {} demandée par l'utilisateur: {}", id, currentUser.getEmail());

        AnnonceResponse response = annonceService.updateAnnonce(id, request, currentUser);

        return ResponseEntity.ok(response);
    }

    /**
     * Met à jour le nombre favorie d'une annonce existante
     */
    @PutMapping("/{id}/{favori}")

    @Operation(summary = "Modifier une annonce",
            description = "Met à jour le nombre favorie d'une annonce existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Annonce mise à jour avec succès"),
            @ApiResponse(responseCode = "403", description = "Non autorisé à modifier cette annonce"),
            @ApiResponse(responseCode = "404", description = "Annonce non trouvée")
    })
    public ResponseEntity<Annonce> miseEnFavori(
            @PathVariable Long id,
            @PathVariable Boolean favori
            , @AuthenticationPrincipal User currentUser
            ) {

        log.info("Ajout au favori de l'annonce ID: {} demandée par l'utilisateur: {}", id
                ,currentUser.getEmail()
        );

        Annonce response = annonceService.miseEnFavori(id, favori,currentUser);

        return ResponseEntity.ok(response);
    }

    /**
     * Supprime une annonce
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une annonce",
            description = "Supprime une annonce (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Annonce supprimée avec succès"),
            @ApiResponse(responseCode = "403", description = "Non autorisé à supprimer cette annonce"),
            @ApiResponse(responseCode = "404", description = "Annonce non trouvée")
    })
    public ResponseEntity<MessageResponse> deleteAnnonce(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        log.info("Suppression d'annonce ID: {} demandée par l'utilisateur: {}", id, currentUser.getEmail());

        annonceService.deleteAnnonce(id, currentUser);

        return ResponseEntity.ok(MessageResponse.success("Annonce supprimée avec succès"));
    }

    /**
     * Récupère les annonces de l'utilisateur connecté
     */
    @GetMapping("/mes-annonces")
    @Operation(summary = "Mes annonces",
            description = "Récupère toutes les annonces de l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Annonces récupérées avec succès"),
            @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    public ResponseEntity<PageResponse<AnnonceResponse>> getMesAnnonces(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0",required = false) int page,
            @RequestParam(defaultValue = "10",required = false) int size) {

        log.info("Récupération des annonces pour l'utilisateur: {}", currentUser.getEmail());

        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page, size,
                        org.springframework.data.domain.Sort.by("dateCreation").descending());

        PageResponse<AnnonceResponse> response = annonceService.getAnnoncesByUser(currentUser.getId(), pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les annonces similaires
     */
    @GetMapping("/{id}/similar")
    @Operation(summary = "Annonces similaires",
            description = "Récupère les annonces similaires à une annonce donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Annonces similaires récupérées"),
            @ApiResponse(responseCode = "404", description = "Annonce non trouvée")
    })
    public ResponseEntity<java.util.List<AnnonceResponse>> getSimilarAnnonces(
            @PathVariable Long id,
            @RequestParam(defaultValue = "5") int limit) {

        log.info("Récupération d'annonces similaires à l'annonce ID: {}", id);

        java.util.List<AnnonceResponse> similarAnnonces = annonceService.getSimilarAnnonces(id, limit);

        return ResponseEntity.ok(similarAnnonces);
    }
}


