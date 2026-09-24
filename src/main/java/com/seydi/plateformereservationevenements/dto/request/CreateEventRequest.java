package com.seydi.plateformereservationevenements.dto.request;

import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

public class CreateEventRequest {

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

    public CreateEventRequest() {}

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public OffsetDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(OffsetDateTime dateHeure) { this.dateHeure = dateHeure; }

    public Long getSalleId() { return salleId; }
    public void setSalleId(Long salleId) { this.salleId = salleId; }
}