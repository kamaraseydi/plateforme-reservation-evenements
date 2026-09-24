package com.seydi.plateformereservationevenements.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateReservationRequest {

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