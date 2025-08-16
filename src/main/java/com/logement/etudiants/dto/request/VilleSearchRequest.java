package com.logement.etudiants.dto.request;

import com.logement.etudiants.enumeration.TypeLogement;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VilleSearchRequest {
    @Size(max = 100, message = "Le terme de recherche ne peut pas dépasser 100 caractères")
    private String search;
    // Tri
    private String sort = "desc"; // asc, desc
    private String sortBy = "nom"; // nom, ...

}
