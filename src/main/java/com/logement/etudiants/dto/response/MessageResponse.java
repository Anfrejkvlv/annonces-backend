package com.logement.etudiants.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; /**
 * DTO de réponse pour les messages de succès
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private String message;

    private String type; // success, info, warning, error

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static MessageResponse success(String message) {
        return MessageResponse.builder()
                .message(message)
                .type("success")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static MessageResponse info(String message) {
        return MessageResponse.builder()
                .message(message)
                .type("info")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static MessageResponse warning(String message) {
        return MessageResponse.builder()
                .message(message)
                .type("warning")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static MessageResponse error(String message) {
        return MessageResponse.builder()
                .message(message)
                .type("error")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
