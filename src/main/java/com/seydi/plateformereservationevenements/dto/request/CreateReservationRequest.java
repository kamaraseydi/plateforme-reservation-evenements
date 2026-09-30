package com.seydi.plateformereservationevenements.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Données nécessaires à la création d'une réservation")
public class CreateReservationRequest {

    @Schema(
            description = "Identifiant de la place à réserver",
            example = "15",
            minimum = "1"
    )
    @NotNull
    @Positive
    private Long placeId;

    public CreateReservationRequest() {}

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }
}