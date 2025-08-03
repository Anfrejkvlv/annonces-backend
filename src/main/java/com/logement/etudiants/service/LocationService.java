package com.logement.etudiants.service;

import com.logement.etudiants.dto.request.QuartierRequest;
import com.logement.etudiants.dto.request.VilleRequest;
import com.logement.etudiants.dto.response.QuartierResponse;
import com.logement.etudiants.dto.response.VilleResponse;
import com.logement.etudiants.entity.Quartier;
import com.logement.etudiants.entity.Ville;
import com.logement.etudiants.mapper.LocationMapper;
import com.logement.etudiants.repository.QuartierRepository;
import com.logement.etudiants.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service de gestion des villes et quartiers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final VilleRepository villeRepository;
    private final QuartierRepository quartierRepository;
    private final LocationMapper locationMapper;


    /**
     *
     * Ajout d'une nouvelle ville
     */
    public VilleResponse createVille(VilleRequest villeRequest) {

        Ville ville = Ville.builder()
                .nom(villeRequest.getNom())
                .description(villeRequest.getDescription())
                .pays(villeRequest.getPays())
                .codePostal(villeRequest.getCodePostal())
                .latitude(villeRequest.getLatitude())
                .longitude(villeRequest.getLongitude()).build();

        Ville savedVille = villeRepository.save(ville);

        return locationMapper.toVilleResponse(savedVille);
    }


    /**
     *
     * Ajout d'une nouvelle ville
     */
    public QuartierResponse createQuartier(QuartierRequest request) {

        Ville ville = villeRepository.findById(request.getVilleId()).orElse(null);

        Quartier quartier = Quartier.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .ville(ville)
                .longitude(request.getLongitude()).build();

            Quartier savedQuartier = quartierRepository.save(quartier);
            return locationMapper.toQuartierResponse(savedQuartier);
    }


    /**
     * Récupère toutes les villes
     */
    public List<VilleResponse> getAllVilles() {
        log.debug("Récupération de toutes les villes");

        return villeRepository.findByActiveTrueOrderByNomAsc()
                .stream()
                .map(ville -> com.logement.etudiants.dto.response.VilleResponse.builder()
                        .id(ville.getId())
                        .nom(ville.getNom())
                        .codePostal(ville.getCodePostal())
                        .pays(ville.getPays())
                        .description(ville.getDescription())
                        .latitude(ville.getLatitude())
                        .longitude(ville.getLongitude())
                        .active(ville.getActive())
                        .nombreAnnonces(ville.getNombreAnnonces())
                        .nombreQuartiers((long) ville.getQuartiers().size())
                        .nomComplet(ville.getNomComplet())
                        .build())
                .toList();
    }

    /**
     * Récupère les quartiers d'une ville
     */
    public List<com.logement.etudiants.dto.response.QuartierResponse> getQuartiersByVille(Long villeId) {
        log.debug("Récupération des quartiers pour la ville ID: {}", villeId);

        return quartierRepository.findByVille_IdAndActiveTrueOrderByNomAsc(villeId)
                .stream()
                .map(quartier -> com.logement.etudiants.dto.response.QuartierResponse.builder()
                        .id(quartier.getId())
                        .nom(quartier.getNom())
                        .description(quartier.getDescription())
                        .latitude(quartier.getLatitude())
                        .longitude(quartier.getLongitude())
                        .active(quartier.getActive())
                        .nombreAnnonces(quartier.getNombreAnnonces())
                        .nomComplet(quartier.getNomComplet())
                        .build())
                .toList();
    }
}
