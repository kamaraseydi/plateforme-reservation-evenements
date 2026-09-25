package com.seydi.plateformereservationevenements.dto.response;

import com.seydi.plateformereservationevenements.model.StatutEvent;

import java.time.OffsetDateTime;

public class EventResponse {

    private Long id;
    private String titre;
    private String description;
    private OffsetDateTime dateHeure;
    private String imageUrl;
    private StatutEvent statut;
    private String organisateur;
    private String salle;
    private String adresse;
    private OffsetDateTime createdAt;

    public EventResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(OffsetDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public StatutEvent getStatut() {
        return statut;
    }

    public void setStatut(StatutEvent statut) {
        this.statut = statut;
    }

    public String getOrganisateur() {
        return organisateur;
    }

    public void setOrganisateur(String organisateur) {
        this.organisateur = organisateur;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}