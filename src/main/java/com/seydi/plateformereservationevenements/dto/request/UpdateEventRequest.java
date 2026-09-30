package com.seydi.plateformereservationevenements.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

@Schema(description = "Données nécessaires à la modification d'un événement")
public class UpdateEventRequest {

    @Schema(
            description = "Nouveau titre de l'événement",
            example = "Festival de cinéma de Dakar - Édition 2027",
            minLength = 2,
            maxLength = 100
    )
    @NotBlank
    @Size(min = 2, max = 100)
    private String titre;

    @Schema(
            description = "Nouvelle description de l'événement",
            example = "Nouvelle programmation du festival et rencontres avec les réalisateurs.",
            minLength = 10,
            maxLength = 1000
    )
    @NotBlank
    @Size(min = 10, max = 1000)
    private String description;

    @Schema(
            description = "Nouvelle date et heure de l'événement",
            example = "2027-06-15T20:00:00+02:00"
    )
    @NotNull
    @Future
    private OffsetDateTime dateHeure;

    @Schema(
            description = "Identifiant de la nouvelle salle",
            example = "9",
            minimum = "1"
    )
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