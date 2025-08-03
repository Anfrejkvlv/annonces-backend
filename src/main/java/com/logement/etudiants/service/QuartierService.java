package com.logement.etudiants.service;

import com.logement.etudiants.dto.response.QuartierResponse;
import com.logement.etudiants.repository.QuartierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/*
@Service
@RequiredArgsConstructor
public class QuartierService {
    private final QuartierRepository quartierRepository;
    public List<QuartierResponse> getByVille(Long villeId){
        return quartierRepository.findByVille_Id(villeId)
                .stream()
                .map(q -> new QuartierResponse(q.getId(), q.getNom()))
                .toList();
    }
}

 */
