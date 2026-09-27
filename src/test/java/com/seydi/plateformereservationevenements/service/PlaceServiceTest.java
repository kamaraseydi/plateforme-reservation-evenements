package com.seydi.plateformereservationevenements.service;

import com.seydi.plateformereservationevenements.dto.response.PlaceResponse;
import com.seydi.plateformereservationevenements.exception.EventNotFoundException;
import com.seydi.plateformereservationevenements.mapper.PlaceMapper;
import com.seydi.plateformereservationevenements.model.Event;
import com.seydi.plateformereservationevenements.model.Place;
import com.seydi.plateformereservationevenements.model.Reservation;
import com.seydi.plateformereservationevenements.model.Salle;
import com.seydi.plateformereservationevenements.model.StatutReservation;
import com.seydi.plateformereservationevenements.repository.EventRepository;
import com.seydi.plateformereservationevenements.repository.PlaceRepository;
import com.seydi.plateformereservationevenements.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PlaceMapper placeMapper;

    @InjectMocks
    private PlaceService placeService;

    private Event event;
    private Salle salle;
    private Place place1;
    private Place place2;
    private Place place3;

    @BeforeEach
    void setUp() {

        salle = new Salle();
        salle.setId(10L);
        salle.setNom("Salle Test");

        event = new Event();
        event.setId(20L);
        event.setSalle(salle);

        place1 = new Place();
        place1.setId(1L);
        place1.setNumero("A1");
        place1.setSalle(salle);

        place2 = new Place();
        place2.setId(2L);
        place2.setNumero("A2");
        place2.setSalle(salle);

        place3 = new Place();
        place3.setId(3L);
        place3.setNumero("A3");
        place3.setSalle(salle);
    }

    @Test
    void listerPlacesDisponibles_devraitRetournerLesPlacesAvecDisponibilite()
    {
        when(eventRepository.findById(20L))
                .thenReturn(Optional.of(event));

        when(placeRepository.findBySalleId(10L))
                .thenReturn(List.of(place1, place2, place3));

        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setPlace(place2);
        reservation.setStatut(StatutReservation.CONFIRMEE);

        when(reservationRepository.findByEventIdAndStatutIn(
                eq(20L),
                anyList()
        )).thenReturn(List.of(reservation));

        PlaceResponse response1 =
                new PlaceResponse(1L, "A1", true);

        PlaceResponse response2 =
                new PlaceResponse(2L, "A2", false);

        PlaceResponse response3 =
                new PlaceResponse(3L, "A3", true);

        when(placeMapper.toResponse(place1, true))
                .thenReturn(response1);

        when(placeMapper.toResponse(place2, false))
                .thenReturn(response2);

        when(placeMapper.toResponse(place3, true))
                .thenReturn(response3);

        List<PlaceResponse> result =
                placeService.listerPlacesDisponibles(20L);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("A1", result.get(0).getNumero());
        assertTrue(result.get(0).isDisponible());

        assertEquals(2L, result.get(1).getId());
        assertEquals("A2", result.get(1).getNumero());
        assertFalse(result.get(1).isDisponible());

        assertEquals(3L, result.get(2).getId());
        assertEquals("A3", result.get(2).getNumero());
        assertTrue(result.get(2).isDisponible());

        verify(eventRepository)
                .findById(20L);

        verify(placeRepository)
                .findBySalleId(10L);

        verify(reservationRepository)
                .findByEventIdAndStatutIn(
                        eq(20L),
                        anyList()
                );

        verify(placeMapper)
                .toResponse(place1, true);

        verify(placeMapper)
                .toResponse(place2, false);

        verify(placeMapper)
                .toResponse(place3, true);
    }

    @Test
    void listerPlacesDisponibles_sansReservation_devraitRendreToutesLesPlacesDisponibles()
    {
        when(eventRepository.findById(20L))
                .thenReturn(Optional.of(event));

        when(placeRepository.findBySalleId(10L))
                .thenReturn(List.of(place1, place2, place3));

        when(reservationRepository.findByEventIdAndStatutIn(
                eq(20L),
                anyList()
        )).thenReturn(List.of());

        when(placeMapper.toResponse(place1, true))
                .thenReturn(new PlaceResponse(1L, "A1", true));

        when(placeMapper.toResponse(place2, true))
                .thenReturn(new PlaceResponse(2L, "A2", true));

        when(placeMapper.toResponse(place3, true))
                .thenReturn(new PlaceResponse(3L, "A3", true));

        List<PlaceResponse> result =
                placeService.listerPlacesDisponibles(20L);

        assertEquals(3, result.size());

        assertTrue(result.get(0).isDisponible());
        assertTrue(result.get(1).isDisponible());
        assertTrue(result.get(2).isDisponible());
    }

    @Test
    void listerPlacesDisponibles_reservationAnnulee_neDoitPasBloquerLaPlace()
    {
        when(eventRepository.findById(20L))
                .thenReturn(Optional.of(event));

        when(placeRepository.findBySalleId(10L))
                .thenReturn(List.of(place1));

        // Le repository ne retourne que les réservations actives.
        when(reservationRepository.findByEventIdAndStatutIn(
                eq(20L),
                anyList()
        )).thenReturn(List.of());

        PlaceResponse response =
                new PlaceResponse(1L, "A1", true);

        when(placeMapper.toResponse(place1, true))
                .thenReturn(response);

        List<PlaceResponse> result =
                placeService.listerPlacesDisponibles(20L);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isDisponible());

        verify(placeMapper)
                .toResponse(place1, true);
    }

    @Test
    void listerPlacesDisponibles_evenementInexistant_devraitLeverException()
    {
        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                EventNotFoundException.class,
                () -> placeService.listerPlacesDisponibles(99L)
        );

        verify(eventRepository)
                .findById(99L);

        verifyNoInteractions(
                placeRepository,
                reservationRepository,
                placeMapper
        );
    }

    @Test
    void listerPlacesDisponibles_avecReservationEnAttente_placeDoitEtreIndisponible()
    {
        when(eventRepository.findById(20L))
                .thenReturn(Optional.of(event));

        when(placeRepository.findBySalleId(10L))
                .thenReturn(List.of(place1));

        Reservation reservation = new Reservation();
        reservation.setId(101L);
        reservation.setPlace(place1);
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        when(reservationRepository.findByEventIdAndStatutIn(
                eq(20L),
                anyList()
        )).thenReturn(List.of(reservation));

        PlaceResponse response =
                new PlaceResponse(1L, "A1", false);

        when(placeMapper.toResponse(place1, false))
                .thenReturn(response);

        List<PlaceResponse> result =
                placeService.listerPlacesDisponibles(20L);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isDisponible());

        verify(placeMapper)
                .toResponse(place1, false);
    }
}