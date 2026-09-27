package com.seydi.plateformereservationevenements.mapper;

import com.seydi.plateformereservationevenements.dto.response.EventReservationResponse;
import com.seydi.plateformereservationevenements.model.Event;
import com.seydi.plateformereservationevenements.model.Place;
import com.seydi.plateformereservationevenements.model.Reservation;
import com.seydi.plateformereservationevenements.model.User;
import com.seydi.plateformereservationevenements.model.StatutReservation;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationMapperTest {

    private final ReservationMapper reservationMapper = new ReservationMapper();

    @Test
    void toEventReservationResponse_doitMapperCorrectementLaReservation() {

        User participant = new User();
        participant.setId(1L);
        participant.setNom("Seydi Camara");
        participant.setEmail("seydi@example.com");

        Place place = new Place();
        place.setId(10L);
        place.setNumero("A10");

        Event event = new Event();
        event.setId(20L);

        OffsetDateTime createdAt = OffsetDateTime.now();

        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setParticipant(participant);
        reservation.setEvent(event);
        reservation.setPlace(place);
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setCreatedAt(createdAt);

        EventReservationResponse response =
                reservationMapper.toEventReservationResponse(reservation);

        assertEquals(100L, response.getId());
        assertEquals(1L, response.getParticipantId());
        assertEquals("Seydi Camara", response.getParticipantNom());
        assertEquals("seydi@example.com", response.getParticipantEmail());
        assertEquals(10L, response.getPlaceId());
        assertEquals("A10", response.getNumeroPlace());
        assertEquals("CONFIRMEE", response.getStatut());
        assertEquals(createdAt, response.getCreatedAt());
    }
}