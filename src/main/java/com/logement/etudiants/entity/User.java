package com.logement.etudiants.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logement.etudiants.enumeration.Role;
import com.logement.etudiants.enumeration.StatutAnnonce;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Entité User représentant un utilisateur du système
 * Implémente UserDetails pour l'intégration avec Spring Security
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_role", columnList = "role"),
        @Index(name = "idx_user_active", columnList = "active"),
        @Index(name = "idx_user_pays", columnList = "pays"),
        @Index(name = "idx_user_date_creation", columnList = "date_creation")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"annonces"})
@ToString(exclude = {"password", "annonces"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le nom ne peut contenir que des lettres, espaces, apostrophes et tirets")
    @Column(nullable = false, length = 50)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le prénom ne peut contenir que des lettres, espaces, apostrophes et tirets")
    @Column(nullable = false, length = 50)
    private String prenom;

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    @Size(max = 150, message = "L'email ne peut pas dépasser 150 caractères")
    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 60, max = 255, message = "Hash du mot de passe invalide")
    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Format de téléphone invalide")
    @Column(length = 20)
    private String telephone;

    @NotBlank(message = "Le pays est obligatoire")
    @Size(min = 2, max = 100, message = "Le pays doit contenir entre 2 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String pays;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Role role = Role.USER;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Builder.Default
    @Column(name = "email_verifie", nullable = false)
    private Boolean emailVerifie = false;

    // Dates
    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;

    // Sécurité et verrouillage du compte
    @Builder.Default
    @Column(name = "tentatives_connexion_echouees", nullable = false)
    private Integer tentativesConnexionEchouees = 0;

    @Column(name = "compte_verrouille_jusq")
    private LocalDateTime compteVerrouilleJusqu;

    // Réinitialisation de mot de passe
    @Column(name = "token_reset_password")
    private String tokenResetPassword;

    @Column(name = "token_reset_expiration")
    private LocalDateTime tokenResetExpiration;

    // Vérification d'email
    @Column(name = "token_verification_email")
    private String tokenVerificationEmail;

    @Column(name = "token_verification_expiration")
    private LocalDateTime tokenVerificationExpiration;

    // Relations
    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Annonce> annonces = new java.util.HashSet<>();

    // Implémentation de UserDetails pour Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return compteVerrouilleJusqu == null || compteVerrouilleJusqu.isBefore(LocalDateTime.now());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active && emailVerifie;
    }

    // Méthodes utilitaires
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isModerator() {
        return role == Role.MODERATOR || role == Role.ADMIN;
    }

    public void incrementTentativesEchouees() {
        this.tentativesConnexionEchouees++;

        // Verrouiller le compte après 5 tentatives échouées
        if (this.tentativesConnexionEchouees >= 5) {
            this.compteVerrouilleJusqu = LocalDateTime.now().plusMinutes(30);
        }
    }

    public void resetTentativesEchouees() {
        this.tentativesConnexionEchouees = 0;
        this.compteVerrouilleJusqu = null;
    }

    public boolean isCompteVerrouille() {
        return compteVerrouilleJusqu != null && compteVerrouilleJusqu.isAfter(LocalDateTime.now());
    }

    public void genererTokenVerificationEmail() {
        this.tokenVerificationEmail = java.util.UUID.randomUUID().toString();
        this.tokenVerificationExpiration = LocalDateTime.now().plusHours(24);
    }

    public void verifierEmail() {
        this.emailVerifie = true;
        this.tokenVerificationEmail = null;
        this.tokenVerificationExpiration = null;
    }

    public boolean isTokenVerificationValide() {
        return tokenVerificationEmail != null &&
                tokenVerificationExpiration != null &&
                tokenVerificationExpiration.isAfter(LocalDateTime.now());
    }

    public boolean isTokenResetValide() {
        return tokenResetPassword != null &&
                tokenResetExpiration != null &&
                tokenResetExpiration.isAfter(LocalDateTime.now());
    }

    public long getNombreAnnonces() {
        return annonces != null ? annonces.size() : 0;
    }

    public long getNombreAnnoncesActives() {
        return annonces != null ?
                annonces.stream().filter(a -> a.getActive() && a.getStatut() == StatutAnnonce.APPROUVEE).count() : 0;
    }

    // Callback JPA
    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (role == null) {
            role = Role.USER;
        }
        if (active == null) {
            active = true;
        }
        if (emailVerifie == null) {
            emailVerifie = false;
        }
        if (tentativesConnexionEchouees == null) {
            tentativesConnexionEchouees = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}


/**
 * Entité User représentant un utilisateur du système
 * Implémente UserDetails pour l'intégration Spring Security
 */

/*
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_pays", columnList = "pays"),
        @Index(name = "idx_user_active", columnList = "active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"annonces"})
@ToString(exclude = {"password", "annonces"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le nom ne peut contenir que des lettres, espaces, apostrophes et tirets")
    @Column(nullable = false, length = 50)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le prénom ne peut contenir que des lettres, espaces, apostrophes et tirets")
    @Column(nullable = false, length = 50)
    private String prenom;

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    @Size(max = 150, message = "L'email ne peut pas dépasser 150 caractères")
    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 60, max = 100, message = "Hash du mot de passe invalide")
    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Format de téléphone invalide")
    @Size(max = 20)
    @Column(length = 20)
    private String telephone;

    @NotBlank(message = "Le pays est obligatoire")
    @Size(min = 2, max = 100, message = "Le pays doit contenir entre 2 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String pays;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Builder.Default
    @Column(name = "email_verifie", nullable = false)
    private Boolean emailVerifie = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Role role = Role.USER;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;

    @Column(name = "token_reset_password")
    private String tokenResetPassword;

    @Column(name = "token_reset_expiration")
    private LocalDateTime tokenResetExpiration;

    @Column(name = "tentatives_connexion_echouees", nullable = false)
    @Builder.Default
    private Integer tentativesConnexionEchouees = 0;

    @Column(name = "compte_verrouille_jusqu")
    private LocalDateTime compteVerrouilleJusqu;

    // Relations
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Annonce> annonces = new HashSet<>();

    // Méthodes UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return compteVerrouilleJusqu == null || compteVerrouilleJusqu.isBefore(LocalDateTime.now());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active && emailVerifie;
    }

    // Méthodes utilitaires
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public void incrementTentativesEchouees() {
        this.tentativesConnexionEchouees++;
        if (this.tentativesConnexionEchouees >= 5) {
            this.compteVerrouilleJusqu = LocalDateTime.now().plusHours(1);
        }
    }

    public void resetTentativesEchouees() {
        this.tentativesConnexionEchouees = 0;
        this.compteVerrouilleJusqu = null;
    }

    // Callback JPA
    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}

 */




