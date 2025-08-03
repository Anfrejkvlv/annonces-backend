package com.logement.etudiants.config;

import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List; /**
 * Configuration des propriétés de l'application
 */
@Configuration
@org.springframework.boot.context.properties.ConfigurationProperties(prefix = "app")
@org.springframework.boot.context.properties.EnableConfigurationProperties
@lombok.Data
public class AppProperties {

    private Security security = new Security();
    private Upload upload = new Upload();
    private Cors cors = new Cors();

    @lombok.Data
    public static class Security {
        private Password password = new Password();

        @lombok.Data
        public static class Password {
            private int minLength = 8;
            private boolean requireUppercase = true;
            private boolean requireLowercase = true;
            private boolean requireDigits = true;
            private boolean requireSpecialChars = false;
        }
    }

    @lombok.Data
    public static class Upload {
        private int maxImagesPerAnnonce = 10;
        private List<String> allowedImageTypes = Arrays.asList("jpg", "jpeg", "png", "webp");
        private String maxImageSize = "5MB";
        private String baseDir = "${user.home}/uploads/annonces";
        private String tempDir = "${java.io.tmpdir}/annonces-temp";
    }

    @lombok.Data
    public static class Cors {
        private List<String> allowedOrigins = Arrays.asList("http://localhost:4200");
        private List<String> allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");
        private List<String> allowedHeaders = Arrays.asList("*");
        private boolean allowCredentials = true;
        private long maxAge = 3600;
    }
}
