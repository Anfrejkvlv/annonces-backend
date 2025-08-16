package com.logement.etudiants.service;

import com.logement.etudiants.dto.request.UpdateProfileRequest;
import com.logement.etudiants.dto.request.UserSearchRequest;
import com.logement.etudiants.dto.response.PageResponse;
import com.logement.etudiants.dto.response.UserResponse;
import com.logement.etudiants.entity.User;
import com.logement.etudiants.exception.ResourceNotFoundException;
import com.logement.etudiants.mapper.UserMapper;
import com.logement.etudiants.repository.AnnonceRepository;
import com.logement.etudiants.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de gestion des utilisateurs et profils
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AnnonceRepository annonceRepository;
    private final UserMapper userMapper;

    /**
     * Récupère le profil d'un utilisateur
     */
    public UserResponse getProfile(Long userId) {
        log.debug("Récupération du profil utilisateur ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Compter les annonces de l'utilisateur
        long nombreAnnonces = annonceRepository.countNewAnnoncesFromDate(user.getDateCreation());

        return UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .pays(user.getPays())
                .role(user.getRole())
                .active(user.getActive())
                .emailVerifie(user.getEmailVerifie())
                .dateCreation(user.getDateCreation())
                .derniereConnexion(user.getDerniereConnexion())
                .nombreAnnonces(nombreAnnonces)
                .nomComplet(user.getNomComplet())
                .build();
    }

    /**
     * Met à jour le profil d'un utilisateur
     */
    @Transactional
    public UserResponse updateProfile(Long userId,UpdateProfileRequest request) {
        log.info("Mise à jour du profil utilisateur ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Mettre à jour les champs
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setTelephone(request.getTelephone());
        user.setPays(request.getPays());

        User savedUser = userRepository.save(user);

        log.info("Profil mis à jour avec succès pour l'utilisateur: {}", savedUser.getEmail());

        return getProfile(savedUser.getId());
    }

    public List<UserResponse> getAllUsers() {
        //Pageable pageable = null;
        //Page<User> usersPage= userRepository.findAllUsers(pageable);
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
        /*return PageResponse.builder()
                .content(Collections.singletonList(userResponses))
                .size(usersPage.getSize())
                .page(usersPage.getNumber())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .first(usersPage.isFirst())
                .last(usersPage.isLast())
                .empty(usersPage.isEmpty())
                .build();*/
    }

    /**
     * Retrieved nombre des utilisateurs inscrit aujourd'hui
     */
    public long getAllUserToday(){
        return userRepository.findUsersCreatedToday().size();
    }


    /**
     * Recuperer tous les utilisateurs avec filtres
     */
    public PageResponse<UserResponse> getAllUsersWithFilters(UserSearchRequest request, Pageable pageable) {
        Page<User> userPage;

        if( request.getSearch()!=null && !request.getSearch().isEmpty()){
            userPage=userRepository.findBySearchTerm(request.getSearch().trim(),pageable);
        }
        else{
            userPage=userRepository.findUsersWithFilters(
                    request.getRole(),
                    request.getPays(),
                    request.getActive()
                    ,pageable);
        }

        List<UserResponse> userResponses=userPage.getContent()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        Map<String, Object> map=new HashMap<>();
        map.put("role", request.getRole());
        map.put("pays", request.getPays());
        map.put("active", request.getActive());



        return PageResponse.<UserResponse>builder()
                .content(userResponses)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .filters(map)
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .empty(userPage.isEmpty())
                .sortBy(request.getSortBy())
                .sortDirection(request.getSortDirection())
                .build();

    }
}
