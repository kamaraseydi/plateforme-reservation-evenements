package com.seydi.plateformereservationevenements.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Données nécessaires à la création d'une salle")
public class CreateSalleRequest {

    @Schema(
            description = "Nom de la salle",
            example = "Grande Salle Dakar Arena",
            minLength = 2,
            maxLength = 100
    )
    @NotBlank
    @Size(min = 2, max = 100)
    private String nom;

    @Schema(
            description = "Adresse de la salle",
            example = "Diamniadio, Dakar",
            minLength = 2,
            maxLength = 200
    )
    @NotBlank
    @Size(min = 2, max = 200)
    private String adresse;

    @Schema(
            description = "Liste des numéros ou identifiants des places de la salle",
            example = "[\"A1\", \"A2\", \"A3\", \"B1\", \"B2\", \"B3\"]"
    )
    @NotEmpty
    private List<@NotBlank String> places;

    public CreateSalleRequest() {}

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

    public List<String> getPlaces() {
        return places;
    }

    public void setPlaces(List<String> places) {
        this.places = places;
    }
}