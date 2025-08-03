package com.logement.etudiants;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.actuate.info.InfoContributor;

/**
 * Application principale Spring Boot pour le système de logements étudiants
 *
 * @author Loubahasra C.Emmanuel, Hydra
 * @version 1.0.0
 * @since 2025
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
@EnableConfigurationProperties
@EnableCaching
@EnableAsync
@EnableScheduling
public class AnnoncesBackendApplication {

    public static void main(String[] args) {
        // Configuration des propriétés système
        System.setProperty("spring.profiles.default", "dev");

        // Démarrage de l'application
        SpringApplication app = new SpringApplication(AnnoncesBackendApplication.class);

        // Configuration des propriétés par défaut
        app.setDefaultProperties(java.util.Map.of(
                "server.port", "8084",
                "management.endpoints.web.exposure.include", "health,info,metrics",
                "logging.level.com.logement.etudiants", "INFO"
        ));

        app.run(args);
    }
}






/**
 * Configuration des tâches planifiées
 */
@org.springframework.stereotype.Component
@lombok.RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
class ScheduledTasks {

    private final com.logement.etudiants.service.AnnonceService annonceService;

    /**
     * Traite les annonces expirées tous les jours à 2h du matin
     */
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 2 * * ?")
    public void processExpiredAnnonces() {
        log.info("Démarrage du traitement des annonces expirées");
        try {
            annonceService.processExpiredAnnonces();
            log.info("Traitement des annonces expirées terminé avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement des annonces expirées: {}", e.getMessage(), e);
        }
    }

    /**
     * Nettoyage des logs et données temporaires tous les dimanches à 3h
     */
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 3 * * SUN")
    public void cleanupTempData() {
        log.info("Démarrage du nettoyage des données temporaires");
        // TODO: Implémenter le nettoyage des données temporaires
        log.info("Nettoyage des données temporaires terminé");
    }

    /**
     * Génération des rapports de statistiques tous les lundis à 1h
     */
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 1 * * MON")
    public void generateWeeklyStats() {
        log.info("Génération des statistiques hebdomadaires");
        // TODO: Implémenter la génération de statistiques
        log.info("Génération des statistiques terminée");
    }
}

/**
 * Listener d'événements de l'application
 */
@org.springframework.stereotype.Component
@lombok.extern.slf4j.Slf4j
class ApplicationEventListener {

    /**
     * Événement de démarrage de l'application
     */
    @org.springframework.context.event.EventListener
    public void handleApplicationReady(org.springframework.boot.context.event.ApplicationReadyEvent event) {
        log.info("=== APPLICATION LOGEMENTS ÉTUDIANTS DÉMARRÉE ===");
        log.info("Version: 1.0.0");
        log.info("Profil actif: {}", java.util.Arrays.toString(event.getApplicationContext().getEnvironment().getActiveProfiles()));
        log.info("Port: {}", event.getApplicationContext().getEnvironment().getProperty("server.port", "8080"));
        log.info("Documentation API: http://localhost:{}/swagger-ui.html",
                event.getApplicationContext().getEnvironment().getProperty("server.port", "8080"));
        log.info("============================================");
    }

    /**
     * Événement d'arrêt de l'application
     */
    @org.springframework.context.event.EventListener
    public void handleApplicationShutdown(org.springframework.context.event.ContextClosedEvent event) {
        log.info("=== ARRÊT DE L'APPLICATION ===");
        log.info("Nettoyage des ressources en cours...");
        // TODO: Ajouter le nettoyage des ressources si nécessaire
        log.info("Application arrêtée proprement");
    }
}

/**
 * Configuration des contrôleurs de santé personnalisés
 */
@org.springframework.stereotype.Component
class CustomHealthIndicator implements org.springframework.boot.actuate.health.HealthIndicator {

    private final javax.sql.DataSource dataSource;

    public CustomHealthIndicator(javax.sql.DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public org.springframework.boot.actuate.health.Health health() {
        try {
            // Vérifier la connexion à la base de données
            try (java.sql.Connection connection = dataSource.getConnection()) {
                if (connection.isValid(1)) {
                    return org.springframework.boot.actuate.health.Health.up()
                            .withDetail("database", "Connection successful")
                            .withDetail("timestamp", java.time.LocalDateTime.now())
                            .build();
                }
            }
        } catch (Exception e) {
            return org.springframework.boot.actuate.health.Health.down()
                    .withDetail("database", "Connection failed")
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", java.time.LocalDateTime.now())
                    .build();
        }

        return org.springframework.boot.actuate.health.Health.down()
                .withDetail("database", "Unknown status")
                .build();
    }
}

/**
 * Configuration des propriétés d'information de l'application
 */
@Component
class CustomInfoContributor implements InfoContributor {

    @Override
    public void contribute(org.springframework.boot.actuate.info.Info.Builder builder) {
        builder.withDetail("application", java.util.Map.of(
                "name", "API Logements Étudiants",
                "version", "1.0.0",
                "description", "API pour la gestion d'annonces de logements étudiants",
                "build-time", java.time.LocalDateTime.now().toString(),
                "java-version", System.getProperty("java.version"),
                "spring-boot-version", org.springframework.boot.SpringBootVersion.getVersion()
        ));

        builder.withDetail("features", java.util.Map.of(
                "authentication", "JWT",
                "database", "PostgreSQL",
                "cache", "Redis (optionnel)",
                "documentation", "Swagger/OpenAPI",
                "monitoring", "Spring Boot Actuator"
        ));
    }
}

/**
 * Configuration des intercepteurs HTTP pour les logs et métriques
 */
@org.springframework.stereotype.Component
@lombok.extern.slf4j.Slf4j
class RequestLoggingInterceptor implements org.springframework.web.servlet.HandlerInterceptor {

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request,
                             jakarta.servlet.http.HttpServletResponse response,
                             Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String clientIP = getClientIP(request);

        // Log des requêtes importantes
        if (!requestURI.startsWith("/actuator") && !requestURI.startsWith("/swagger")) {
            log.info("Requête {} {} depuis {} - User-Agent: {}",
                    method, requestURI, clientIP, request.getHeader("User-Agent"));
        }

        // Ajouter timestamp pour mesurer le temps de traitement
        request.setAttribute("startTime", System.currentTimeMillis());

        return true;
    }

    @Override
    public void afterCompletion(jakarta.servlet.http.HttpServletRequest request,
                                jakarta.servlet.http.HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            String requestURI = request.getRequestURI();

            if (duration > 1000) { // Log les requêtes lentes (> 1 seconde)
                log.warn("Requête lente: {} {} - {}ms - Status: {}",
                        request.getMethod(), requestURI, duration, response.getStatus());
            }
        }

        if (ex != null) {
            log.error("Erreur lors du traitement de la requête {} {}: {}",
                    request.getMethod(), request.getRequestURI(), ex.getMessage());
        }
    }

    private String getClientIP(jakarta.servlet.http.HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor == null || xForwardedFor.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xForwardedFor.split(",")[0].trim();
    }
}

/**
 * Configuration des intercepteurs dans le registre Web MVC
 */
@org.springframework.context.annotation.Configuration
class WebMvcConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    private final RequestLoggingInterceptor requestLoggingInterceptor;

    public WebMvcConfig(RequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }

    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**");
    }

    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:*", "https://*.votre-domaine.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}