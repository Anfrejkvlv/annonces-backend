package com.logement.etudiants.config;

import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger/OpenAPI
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public io.swagger.v3.oas.models.OpenAPI openAPI() {
        return new io.swagger.v3.oas.models.OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("API Logements Étudiants")
                        .version("1.0.0")
                        .description("API REST pour la gestion d'annonces de logements étudiants")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Équipe de développement")
                                .email("dev@logements-etudiants.com")
                                .url("https://www.logements-etudiants.com"))
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT pour l'authentification")))
                .servers(List.of(
                        new io.swagger.v3.oas.models.servers.Server()
                                .url("http://localhost:8084")
                                .description("Serveur de développement"),
                        new io.swagger.v3.oas.models.servers.Server()
                                .url("https://api.logements-etudiants.com")
                                .description("Serveur de production")
                ));
    }
}
/*
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Logements Étudiants")
                        .version("1.0.0")
                        .description("Documentation Swagger"));
    }
}

 */

