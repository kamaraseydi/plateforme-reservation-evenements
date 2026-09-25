package com.seydi.plateformereservationevenements.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public class UpdateEventRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String titre;

    @NotBlank
    @Size(min = 10, max = 1000)
    private String description;

    @NotNull
    @Future
    private OffsetDateTime dateHeure;

    @NotNull
    @Positive
    private Long salleId;

    public UpdateEventRequest() {
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

    public Long getSalleId() {
        return salleId;
    }

    public void setSalleId(Long salleId) {
        this.salleId = salleId;
    }
}