package com.logement.etudiants.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; /**
 * Configuration des validateurs personnalisés
 */
@Configuration
public class ValidationConfig {

    @Bean
    public jakarta.validation.Validator validator() {
        return jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
    }
}
