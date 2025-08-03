package com.logement.etudiants.mapper;

import com.logement.etudiants.dto.response.AnnonceResponse;
import com.logement.etudiants.entity.Annonce;

/**
 * Configuration des mappers pour la conversion entre entités et DTOs
 */
@org.mapstruct.Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE
)
public interface AnnonceMapper {

    /**
     * Convertit une entité Annonce en DTO de réponse
     */
    @org.mapstruct.Mapping(source = "ville.nom", target = "villeNom")
    @org.mapstruct.Mapping(source = "quartier.nom", target = "quartierNom")
    @org.mapstruct.Mapping(source = "utilisateur.nom", target = "utilisateurNom")
    @org.mapstruct.Mapping(source = "utilisateur.email", target = "utilisateurEmail")
    @org.mapstruct.Mapping(expression = "java(annonce.getSlug())", target = "slug")
    @org.mapstruct.Mapping(expression = "java(annonce.getImages())", target = "images")
    @org.mapstruct.Mapping(expression = "java(annonce.isPubliee())", target = "isPubliee")
    @org.mapstruct.Mapping(expression = "java(annonce.isExpired())", target = "isExpired")
    @org.mapstruct.Mapping(expression = "java(annonce.isEditable())", target = "isEditable")
    AnnonceResponse toResponse(Annonce annonce);

    /**
     * Convertit une liste d'entités Annonce en liste de DTOs de réponse
     */
    java.util.List<com.logement.etudiants.dto.response.AnnonceResponse> toResponseList(
            java.util.List<com.logement.etudiants.entity.Annonce> annonces);
}

