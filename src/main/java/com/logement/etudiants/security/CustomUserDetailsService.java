package com.logement.etudiants.security;

import com.logement.etudiants.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service UserDetails personnalisé pour Spring Security
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final com.logement.etudiants.repository.UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Chargement des détails utilisateur pour l'email: {}", email);

        com.logement.etudiants.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé avec l'email: {}", email);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
                });

        log.debug("Utilisateur trouvé: {} (ID: {}, Role: {}, Actif: {}, Email vérifié: {})",
                user.getEmail(), user.getId(), user.getRole(), user.getActive(), user.getEmailVerifie());

        return user; // L'entité User implémente déjà UserDetails
    }
}


