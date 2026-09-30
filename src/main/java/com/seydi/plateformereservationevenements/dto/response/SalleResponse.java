package com.seydi.plateformereservationevenements.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "Informations détaillées d'une salle")
public class SalleResponse {

    @Schema(
            description = "Identifiant unique de la salle",
            example = "9"
    )
    private Long id;

    @Schema(
            description = "Nom de la salle",
            example = "Grande Salle Dakar Arena"
    )
    private String nom;

    @Schema(
            description = "Adresse de la salle",
            example = "Diamniadio, Dakar"
    )
    private String adresse;

    @Schema(
            description = "Capacité maximale de la salle",
            example = "500"
    )
    private Integer capacite;

    @Schema(
            description = "Liste des places disponibles dans la salle",
            example = "[\"A1\", \"A2\", \"A3\", \"B1\", \"B2\", \"B3\"]"
    )
    private List<String> places;

    @Schema(
            description = "Date et heure de création de la salle",
            example = "2026-09-30T15:30:00+02:00"
    )
    private OffsetDateTime createdAt;

    public SalleResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public List<String> getPlaces() {
        return places;
    }

    public void setPlaces(List<String> places) {
        this.places = places;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}