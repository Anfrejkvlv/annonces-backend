package com.logement.etudiants.service;

import com.logement.etudiants.dto.response.VilleResponse;
import com.logement.etudiants.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/*
@Service
@RequiredArgsConstructor
public class VilleService {
    private final VilleRepository villeRepository;
    public List<VilleResponse> getAll(){
        return villeRepository.findAll()
                .stream()
                .map(v -> new VilleResponse(v.getId(), v.getNom(), v.getCodePostal()))
                .toList();
    }
}

 */



