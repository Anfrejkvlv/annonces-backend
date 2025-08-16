package com.logement.etudiants.controller;

import com.logement.etudiants.dto.response.FileUploadResponse;
import com.logement.etudiants.exception.FileUploadException;
import com.logement.etudiants.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/upload")
public class FileUploadController {
    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * Upload d'une image unique
     */
    @PostMapping("/image")
    public ResponseEntity<FileUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file) throws FileUploadException, org.apache.tomcat.util.http.fileupload.FileUploadException {

        FileUploadResponse response = fileUploadService.uploadFile(file);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload de plusieurs images
     */
    @PostMapping("/images")
    public ResponseEntity<List<FileUploadResponse>> uploadImages(
            @RequestParam("files") List<MultipartFile> files) throws FileUploadException, org.apache.tomcat.util.http.fileupload.FileUploadException {

        List<FileUploadResponse> responses = fileUploadService.uploadMultipleFiles(files);
        return ResponseEntity.ok(responses);
    }

    /**
     * Téléchargement/Affichage d'un fichier
     */
    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName,
            HttpServletRequest request) throws IOException {

        Resource resource = fileUploadService.loadFileAsResource(fileName);

        String contentType = request.getServletContext()
                .getMimeType(resource.getFile().getAbsolutePath());

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Suppression d'un fichier
     */
    @DeleteMapping("/files/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) throws FileUploadException, org.apache.tomcat.util.http.fileupload.FileUploadException {
        boolean deleted = fileUploadService.deleteFile(fileName);

        if (deleted) {
            return ResponseEntity.ok("Fichier supprimé avec succès");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
