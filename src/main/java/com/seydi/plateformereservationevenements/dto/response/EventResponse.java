package com.seydi.plateformereservationevenements.dto.response;

import com.seydi.plateformereservationevenements.model.StatutEvent;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Informations détaillées d'un événement")
public class EventResponse {

    @Schema(
            description = "Identifiant unique de l'événement",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Titre de l'événement",
            example = "Festival de cinéma de Dakar"
    )
    private String titre;

    @Schema(
            description = "Description de l'événement",
            example = "Projection de films et rencontres avec les réalisateurs."
    )
    private String description;

    @Schema(
            description = "Date et heure de l'événement",
            example = "2027-06-15T20:00:00+02:00"
    )
    private OffsetDateTime dateHeure;

    @Schema(
            description = "URL de l'image associée à l'événement",
            example = "https://example.com/images/festival.jpg"
    )
    private String imageUrl;

    @Schema(
            description = "Statut actuel de l'événement",
            example = "PUBLIE"
    )
    private StatutEvent statut;

    @Schema(
            description = "Nom de l'organisateur de l'événement",
            example = "Seydi Kamara"
    )
    private String organisateur;

    @Schema(
            description = "Nom de la salle dans laquelle se déroule l'événement",
            example = "Grande Salle Dakar Arena"
    )
    private String salle;

    @Schema(
            description = "Adresse de la salle",
            example = "Diamniadio, Dakar"
    )
    private String adresse;

    @Schema(
            description = "Date et heure de création de l'événement",
            example = "2026-09-30T15:30:00+02:00"
    )
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