package com.seydi.plateformereservationevenements.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Informations d'une réservation consultée par l'organisateur d'un événement")
public class EventReservationResponse {

    @Schema(
            description = "Identifiant unique de la réservation",
            example = "42"
    )
    private Long id;

    @Schema(
            description = "Identifiant du participant",
            example = "12"
    )
    private Long participantId;

    @Schema(
            description = "Nom du participant",
            example = "Moussa Diop"
    )
    private String participantNom;

    @Schema(
            description = "Adresse e-mail du participant",
            example = "moussa.diop@example.com"
    )
    private String participantEmail;

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
            example = "2026-09-30T15:45:00+02:00"
    )
    private OffsetDateTime createdAt;

    public EventReservationResponse() {
    }

    public EventReservationResponse(
            Long id,
            Long participantId,
            String participantNom,
            String participantEmail,
            Long placeId,
            String numeroPlace,
            String statut,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.participantId = participantId;
        this.participantNom = participantNom;
        this.participantEmail = participantEmail;
        this.placeId = placeId;
        this.numeroPlace = numeroPlace;
        this.statut = statut;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public String getParticipantNom() {
        return participantNom;
    }

    public void setParticipantNom(String participantNom) {
        this.participantNom = participantNom;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public void setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
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