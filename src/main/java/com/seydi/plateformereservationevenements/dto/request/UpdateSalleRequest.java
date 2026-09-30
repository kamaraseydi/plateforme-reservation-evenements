package com.seydi.plateformereservationevenements.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Données nécessaires à la modification d'une salle")
public class UpdateSalleRequest {

    @Schema(
            description = "Nouveau nom de la salle",
            example = "Grande Salle Dakar Arena",
            minLength = 2,
            maxLength = 100
    )
    @NotBlank
    @Size(min = 2, max = 100)
    private String nom;

    @Schema(
            description = "Nouvelle adresse de la salle",
            example = "Diamniadio, Dakar",
            minLength = 2,
            maxLength = 200
    )
    @NotBlank
    @Size(min = 2, max = 200)
    private String adresse;

    public UpdateSalleRequest() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}