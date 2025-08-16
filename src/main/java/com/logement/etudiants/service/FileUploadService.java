package com.logement.etudiants.service;

import com.logement.etudiants.config.FileUploadConfig;
import com.logement.etudiants.dto.response.FileUploadResponse;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import com.logement.etudiants.exception.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileUploadService {

    @Autowired
    private FileUploadConfig uploadConfig;

    private Path uploadPath;

    @PostConstruct
    public void init() throws FileUploadException {
        try {
            this.uploadPath = Paths.get(uploadConfig.getDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new FileUploadException("Impossible de créer le répertoire d'upload", e);
        }
    }

    /**
     * Upload d'un fichier unique
     */
    public FileUploadResponse uploadFile(MultipartFile file) throws FileUploadException {
        validateFile(file);

        String fileName = generateUniqueFileName(file.getOriginalFilename());
        Path targetLocation = uploadPath.resolve(fileName);

        try {
            // Vérification de sécurité : pas d'accès parent directory
            if (!targetLocation.getParent().equals(uploadPath)) {
                throw new FileUploadException("Nom de fichier invalide");
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return FileUploadResponse.builder()
                    .fileName(fileName)
                    .originalFileName(file.getOriginalFilename())
                    .size(file.getSize())
                    .mimeType(file.getContentType())
                    .url("/api/v1/files/" + fileName)
                    .uploadTime(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            throw new FileUploadException("Échec de l'upload du fichier", e);
        }
    }

    /**
     * Upload de fichiers multiples
     */
    @SneakyThrows
    public List<FileUploadResponse> uploadMultipleFiles(List<MultipartFile> files) throws FileUploadException {
        if (files.size() > uploadConfig.getMaxFiles()) {
            throw new FileUploadException("Nombre maximum de fichiers dépassé: " + uploadConfig.getMaxFiles());
        }

        return files.stream()
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    /**
     * Téléchargement d'un fichier
     */
    public Resource loadFileAsResource(String fileName) throws FileUploadException {
        try {
            Path filePath = uploadPath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileUploadException("Fichier non trouvé: " + fileName);
            }
        } catch (MalformedURLException | FileUploadException e) {
            throw new FileUploadException("Fichier non trouvé: " + fileName, e);
        }
    }

    /**
     * Suppression d'un fichier
     */
    public boolean deleteFile(String fileName) throws FileUploadException {
        try {
            Path filePath = uploadPath.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileUploadException("Impossible de supprimer le fichier", e);
        }
    }

    /**
     * Validation du fichier
     */
    private void validateFile(MultipartFile file) throws FileUploadException {
        if (file.isEmpty()) {
            throw new FileUploadException("Fichier vide");
        }

        if (file.getSize() > uploadConfig.getMaxFileSize()) {
            throw new FileUploadException("Fichier trop volumineux. Taille max: " +
                    formatFileSize(uploadConfig.getMaxFileSize()));
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (fileName.contains("..")) {
            throw new FileUploadException("Nom de fichier invalide: " + fileName);
        }

        String extension = getFileExtension(fileName).toLowerCase();
        if (!Arrays.asList(uploadConfig.getAllowedExtensions()).contains(extension)) {
            throw new FileUploadException("Extension non autorisée. Extensions valides: " +
                    Arrays.toString(uploadConfig.getAllowedExtensions()));
        }
    }

    /**
     * Génération d'un nom de fichier unique
     */
    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String baseName = getBaseName(originalFileName);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomStr = UUID.randomUUID().toString().substring(0, 8);

        return String.format("%s_%s_%s.%s", baseName, timestamp, randomStr, extension);
    }

    /**
     * Extraction de l'extension du fichier
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * Extraction du nom de base du fichier
     */
    private String getBaseName(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf('.'))
                .replaceAll("[^a-zA-Z0-9]", "_");
    }

    /**
     * Formatage de la taille du fichier
     */
    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = (int) (Math.log(size) / Math.log(1024));
        return String.format("%.2f %s", size / Math.pow(1024, unitIndex), units[unitIndex]);
    }
}
