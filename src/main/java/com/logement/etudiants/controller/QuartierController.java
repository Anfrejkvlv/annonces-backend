package com.logement.etudiants.controller;

import com.logement.etudiants.dto.response.QuartierResponse;
//import com.logement.etudiants.service.QuartierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/*
@RestController
@RequestMapping("/quartiers")
@RequiredArgsConstructor
@Tag(name="Quartiers")
public class QuartierController {

    private final QuartierService quartierService;

    @GetMapping("/{villeId}")
    public ResponseEntity<List<QuartierResponse>> listByVille(@PathVariable Long villeId){
        return ResponseEntity.ok(quartierService.getByVille(villeId));
    }
}

 */

