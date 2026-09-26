package com.seydi.plateformereservationevenements.dto.response;

import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;

    private Long eventId;

    private String eventTitre;

    private Long placeId;

    private String numeroPlace;

    private String statut;

    private LocalDateTime createdAt;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}