package com.logement.etudiants.controller;

import com.logement.etudiants.dto.response.UserResponse;
import com.logement.etudiants.entity.User;
import com.logement.etudiants.service.UserService;
import io.swagger.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@SecurityRequirement(name="bearerAuth")
@Tag(name="Utilisateur")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(userService.getProfile(currentUser.getId()));
    }
}

