package com.logement.etudiants.mapper;
/**
 * Configuration des mappers pour les utilisateurs
 */
@org.mapstruct.Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)

public interface UserMapper {

    /**
     * Convertit une entité User en DTO de réponse
     */
    @org.mapstruct.Mapping(expression = "java(user.getId())", target = "id")
    @org.mapstruct.Mapping(expression = "java(user.getNomComplet())", target = "nomComplet")
    @org.mapstruct.Mapping(expression = "java(user.getNom())", target = "nom")
    @org.mapstruct.Mapping(expression = "java(user.getPrenom())", target = "prenom")
    @org.mapstruct.Mapping(expression = "java(user.getEmail())", target = "email")
    @org.mapstruct.Mapping(expression = "java(user.getTelephone())", target = "telephone")
    @org.mapstruct.Mapping(expression = "java(user.getPays())", target = "pays")
    @org.mapstruct.Mapping(expression = "java(user.getRole())", target = "role")
    @org.mapstruct.Mapping(expression = "java(user.getActive())", target = "active")
    @org.mapstruct.Mapping(expression = "java(user.getEmailVerifie())", target = "emailVerifie")
    @org.mapstruct.Mapping(expression = "java(user.getDateCreation())", target = "dateCreation")
    @org.mapstruct.Mapping(expression = "java(user.getDerniereConnexion())", target = "derniereConnexion")
    @org.mapstruct.Mapping(expression = "java(user.getNombreAnnonces())", target = "nombreAnnonces")
    com.logement.etudiants.dto.response.UserResponse toResponse(com.logement.etudiants.entity.User user);

    /**
     * Convertit une liste d'entités User en liste de DTOs de réponse
     */
    java.util.List<com.logement.etudiants.dto.response.UserResponse> toResponseList(
            java.util.List<com.logement.etudiants.entity.User> users);
}
