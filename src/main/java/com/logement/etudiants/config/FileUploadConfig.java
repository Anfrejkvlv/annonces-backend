package com.logement.etudiants.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "app.upload")
public class FileUploadConfig {

    // Getters et Setters
    private String dir = "./uploads";
    private int maxFiles = 10;
    private String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp"};
    private long maxFileSize = 10 * 1024 * 1024; // 10MB

}
