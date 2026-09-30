package com.seydi.plateformereservationevenements.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

@Schema(description = "Données nécessaires à la création d'un événement")
public class CreateEventRequest {

    @Schema(
            description = "Titre de l'événement",
            example = "Festival de cinéma de Dakar",
            minLength = 2,
            maxLength = 100
    )
    @NotBlank
    @Size(min = 2, max = 100)
    private String titre;

    @Schema(
            description = "Description de l'événement",
            example = "Projection de plusieurs films suivie d'une rencontre avec les réalisateurs.",
            minLength = 10,
            maxLength = 1000
    )
    @NotBlank
    @Size(min = 10, max = 1000)
    private String description;

    @Schema(
            description = "Date et heure prévues pour l'événement",
            example = "2027-06-15T20:00:00+02:00"
    )
    @NotNull
    @Future
    private OffsetDateTime dateHeure;

    @Schema(
            description = "Identifiant de la salle dans laquelle l'événement aura lieu",
            example = "9",
            minimum = "1"
    )
    @NotNull
    @Positive
    private Long salleId;

    public CreateEventRequest() {}

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