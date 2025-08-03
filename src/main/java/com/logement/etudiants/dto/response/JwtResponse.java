package com.logement.etudiants.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logement.etudiants.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de réponse pour l'authentification JWT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private Long userId;

    private String email;

    private String nom;

    private String prenom;

    private Role role;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    private List<String> authorities;
}
