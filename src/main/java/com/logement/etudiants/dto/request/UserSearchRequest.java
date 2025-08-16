package com.logement.etudiants.dto.request;

import com.logement.etudiants.enumeration.Role;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {

        @Size(max = 100, message = "Le terme de recherche ne peut pas dépasser 100 caractères")
        private String search;

        private Role role;

        private Boolean active;

        private LocalDateTime dateCreation;

        private String pays;
        // Tri
        private String sortDirection = "desc"; // asc, desc
        private String sortBy = "nom"; // nom, prenom,...
}
