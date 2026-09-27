package com.seydi.plateformereservationevenements.mapper;

import com.seydi.plateformereservationevenements.dto.response.PlaceResponse;
import com.seydi.plateformereservationevenements.model.Place;
import org.springframework.stereotype.Component;

@Component
public class PlaceMapper {

    public PlaceResponse toResponse(Place place, boolean disponible) {
        return new PlaceResponse(
                place.getId(),
                place.getNumero(),
                disponible
        );
    }
}