package com.logement.etudiants.mapper;

import com.logement.etudiants.dto.response.QuartierResponse;
import com.logement.etudiants.dto.response.VilleResponse;
import com.logement.etudiants.entity.Quartier;
import com.logement.etudiants.entity.Ville;

/**
 * Configuration des mappers pour les villes et quartiers
 */
@org.mapstruct.Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface LocationMapper {

    /**
     * Convertit une entité Ville en DTO de réponse
     */
    @org.mapstruct.Mapping(expression = "java(ville.getId())", target = "id")
    @org.mapstruct.Mapping(expression = "java(ville.getNomComplet())", target = "nomComplet")
    @org.mapstruct.Mapping(expression = "java(ville.getNom())", target = "nom")
    @org.mapstruct.Mapping(expression = "java(ville.getCodePostal())", target = "codePostal")
    @org.mapstruct.Mapping(expression = "java(ville.getPays())", target = "pays")
    @org.mapstruct.Mapping(expression = "java(ville.getLongitude())", target = "longitude")
    @org.mapstruct.Mapping(expression = "java(ville.getLatitude())", target = "latitude")
    @org.mapstruct.Mapping(expression = "java(ville.getActive())", target = "active")
    @org.mapstruct.Mapping(expression = "java(ville.getNombreAnnonces())", target = "nombreAnnonces")
    @org.mapstruct.Mapping(expression = "java(ville.getDescription())", target = "description")
    @org.mapstruct.Mapping(expression = "java((long) ville.getQuartiers().size())", target = "nombreQuartiers")
    VilleResponse toVilleResponse(Ville ville);

    /**
     * Convertit une entité Quartier en DTO de réponse
     */
    @org.mapstruct.Mapping(expression = "java(quartier.getId())", target = "id")
    @org.mapstruct.Mapping(expression = "java(quartier.getNomComplet())", target = "nomComplet")
    @org.mapstruct.Mapping(expression = "java(quartier.getNombreAnnonces())", target = "nombreAnnonces")
    @org.mapstruct.Mapping(expression = "java(quartier.getLongitude())", target = "longitude")
    @org.mapstruct.Mapping(expression = "java(quartier.getLatitude())", target = "latitude")
    @org.mapstruct.Mapping(expression = "java(quartier.getDescription())", target = "description")
    @org.mapstruct.Mapping(source = "ville", target = "ville")
    QuartierResponse toQuartierResponse(Quartier quartier);

    /**
     * Convertit des listes d'entités en listes de DTOs
     */
    java.util.List<com.logement.etudiants.dto.response.VilleResponse> toVilleResponseList(
            java.util.List<com.logement.etudiants.entity.Ville> villes);

    java.util.List<QuartierResponse> toQuartierResponseList(
            java.util.List<Quartier> quartiers);
}
