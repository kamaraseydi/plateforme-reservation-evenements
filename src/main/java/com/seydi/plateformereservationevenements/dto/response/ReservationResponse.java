package com.seydi.plateformereservationevenements.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Informations d'une réservation")
public class ReservationResponse {

    @Schema(
            description = "Identifiant unique de la réservation",
            example = "42"
    )
    private Long id;

    @Schema(
            description = "Identifiant de l'événement réservé",
            example = "1"
    )
    private Long eventId;

    @Schema(
            description = "Titre de l'événement réservé",
            example = "Festival de cinéma de Dakar"
    )
    private String eventTitre;

    @Schema(
            description = "Identifiant de la place réservée",
            example = "15"
    )
    private Long placeId;

    @Schema(
            description = "Numéro de la place réservée",
            example = "A15"
    )
    private String numeroPlace;

    @Schema(
            description = "Statut de la réservation",
            example = "CONFIRMEE"
    )
    private String statut;

    @Schema(
            description = "Date et heure de création de la réservation",
            example = "2026-09-30T15:45:00"
    )
    private OffsetDateTime createdAt;

    public ReservationResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitre() {
        return eventTitre;
    }

    public void setEventTitre(String eventTitre) {
        this.eventTitre = eventTitre;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getNumeroPlace() {
        return numeroPlace;
    }

    public void setNumeroPlace(String numeroPlace) {
        this.numeroPlace = numeroPlace;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}