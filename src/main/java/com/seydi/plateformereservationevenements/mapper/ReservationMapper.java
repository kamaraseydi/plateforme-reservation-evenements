package com.seydi.plateformereservationevenements.mapper;

import com.seydi.plateformereservationevenements.dto.response.ReservationResponse;
import com.seydi.plateformereservationevenements.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {

        ReservationResponse response = new ReservationResponse();

        response.setId(reservation.getId());

        if (reservation.getEvent() != null) {
            response.setEventId(reservation.getEvent().getId());
            response.setEventTitre(reservation.getEvent().getTitre());
        }

        if (reservation.getPlace() != null) {
            response.setPlaceId(reservation.getPlace().getId());
            response.setNumeroPlace(reservation.getPlace().getNumero());
        }

        if (reservation.getStatut() != null) {
            response.setStatut(reservation.getStatut().name());
        }

        if (reservation.getCreatedAt() != null) {
            response.setCreatedAt(
                    reservation.getCreatedAt().toLocalDateTime()
            );
        }

        return response;
    }
}