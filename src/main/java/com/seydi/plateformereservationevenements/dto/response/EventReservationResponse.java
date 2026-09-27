package com.seydi.plateformereservationevenements.dto.response;

import java.time.OffsetDateTime;

public class EventReservationResponse {

    private Long id;
    private Long participantId;
    private String participantNom;
    private String participantEmail;
    private Long placeId;
    private String numeroPlace;
    private String statut;
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