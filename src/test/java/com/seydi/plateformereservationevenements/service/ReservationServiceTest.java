package com.seydi.plateformereservationevenements.service;

import com.seydi.plateformereservationevenements.dto.request.CreateReservationRequest;
import com.seydi.plateformereservationevenements.dto.response.ReservationResponse;
import com.seydi.plateformereservationevenements.exception.EventNotFoundException;
import com.seydi.plateformereservationevenements.exception.PlaceNotFoundException;
import com.seydi.plateformereservationevenements.exception.ReservationException;
import com.seydi.plateformereservationevenements.exception.RoleInvalideException;
import com.seydi.plateformereservationevenements.exception.UserNotFoundException;
import com.seydi.plateformereservationevenements.mapper.ReservationMapper;
import com.seydi.plateformereservationevenements.model.Event;
import com.seydi.plateformereservationevenements.model.Place;
import com.seydi.plateformereservationevenements.model.Reservation;
import com.seydi.plateformereservationevenements.model.Role;
import com.seydi.plateformereservationevenements.model.Salle;
import com.seydi.plateformereservationevenements.model.StatutEvent;
import com.seydi.plateformereservationevenements.model.StatutReservation;
import com.seydi.plateformereservationevenements.model.User;
import com.seydi.plateformereservationevenements.repository.EventRepository;
import com.seydi.plateformereservationevenements.repository.PlaceRepository;
import com.seydi.plateformereservationevenements.repository.ReservationRepository;
import com.seydi.plateformereservationevenements.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationService reservationService;

    private User participant;
    private Salle salle;
    private Event event;
    private Place place;
    private CreateReservationRequest request;

    @BeforeEach
    void setUp() {

        participant = new User();
        participant.setId(1L);
        participant.setSupabaseUserId("user-123");
        participant.setNom("Seydi");
        participant.setEmail("seydi@test.com");
        participant.setRole(Role.PARTICIPANT);

        salle = new Salle();
        salle.setId(10L);
        salle.setNom("Salle Dakar");
        salle.setAdresse("Dakar");

        event = new Event();
        event.setId(20L);
        event.setTitre("Concert Dakar");
        event.setStatut(StatutEvent.PUBLIE);
        event.setSalle(salle);

        place = new Place();
        place.setId(30L);
        place.setNumero("A1");
        place.setSalle(salle);

        request = new CreateReservationRequest();
        request.setPlaceId(30L);
    }

    @Test
    void creerReservation_devraitCreerReservation() {

        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setParticipant(participant);
        reservation.setEvent(event);
        reservation.setPlace(place);
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        ReservationResponse response = new ReservationResponse();
        response.setId(100L);
        response.setEventId(20L);
        response.setPlaceId(30L);
        response.setNumeroPlace("A1");
        response.setStatut("EN_ATTENTE");

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(eventRepository.findById(20L))
                .thenReturn(Optional.of(event));

        when(placeRepository.findById(30L))
                .thenReturn(Optional.of(place));

        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenReturn(reservation);

        when(reservationMapper.toResponse(reservation))
                .thenReturn(response);

        ReservationResponse resultat =
                reservationService.creerReservation(
                        20L,
                        request,
                        "user-123"
                );

        assertNotNull(resultat);
        assertEquals(100L, resultat.getId());
        assertEquals(20L, resultat.getEventId());
        assertEquals(30L, resultat.getPlaceId());
        assertEquals("A1", resultat.getNumeroPlace());
        assertEquals("EN_ATTENTE", resultat.getStatut());

        verify(userRepository)
                .findBySupabaseUserId("user-123");

        verify(eventRepository)
                .findById(20L);

        verify(placeRepository)
                .findById(30L);

        verify(reservationRepository)
                .saveAndFlush(any(Reservation.class));

        verify(reservationMapper)
                .toResponse(reservation);
    }

    @Test
    void creerReservation_utilisateurInexistant_devraitLeverException() {

        when(userRepository.findBySupabaseUserId("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> reservationService.creerReservation(
                        20L,
                        request,
                        "unknown"
                )
        );

        verify(userRepository)
                .findBySupabaseUserId("unknown");

        verifyNoInteractions(
                eventRepository,
                placeRepository,
                reservationRepository,
                reservationMapper
        );
    }

    @Test
    void creerReservation_utilisateurNonParticipant_devraitLeverException() {

        participant.setRole(Role.ORGANISATEUR);

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        assertThrows(
                RoleInvalideException.class,
                () -> reservationService.creerReservation(
                        20L,
                        request,
                        "user-123"
                )
        );

        verify(userRepository)
                .findBySupabaseUserId("user-123");

        verifyNoInteractions(
                eventRepository,
                placeRepository,
                reservationRepository,
                reservationMapper
        );
    }

    @Test
    void creerReservation_evenementInexistant_devraitLeverException() {

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(eventRepository.findById(20L))
                .thenReturn(Optional.empty());

        assertThrows(
                EventNotFoundException.class,
                () -> reservationService.creerReservation(
                        20L,
                        request,
                        "user-123"
                )
        );

        verify(eventRepository)
                .findById(20L);

        verifyNoInteractions(
                placeRepository,
                reservationRepository,
                reservationMapper
        );
    }

    @Test
    void creerReservation_evenementNonPublie_devraitLeverException() {

        event.setStatut(StatutEvent.BROUILLON);

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(eventRepository.findById(20L))
                .thenReturn(Optional.of(event));

        assertThrows(
                ReservationException.class,
                () -> reservationService.creerReservation(
                        20L,
                        request,
                        "user-123"
                )
        );

        verify(eventRepository)
                .findById(20L);

        verifyNoInteractions(
                placeRepository,
                reservationRepository,
                reservationMapper
        );
    }

    @Test
    void creerReservation_placeInexistante_devraitLeverException() {

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(eventRepository.findById(20L))
                .thenReturn(Optional.of(event));

        when(placeRepository.findById(30L))
                .thenReturn(Optional.empty());

        assertThrows(
                PlaceNotFoundException.class,
                () -> reservationService.creerReservation(
                        20L,
                        request,
                        "user-123"
                )
        );

        verify(placeRepository)
                .findById(30L);

        verifyNoInteractions(
                reservationRepository,
                reservationMapper
        );
    }

    @Test
    void creerReservation_placeDuneAutreSalle_devraitLeverException() {

        Salle autreSalle = new Salle();
        autreSalle.setId(99L);
        autreSalle.setNom("Autre Salle");
        autreSalle.setAdresse("Dakar");

        place.setSalle(autreSalle);

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(eventRepository.findById(20L))
                .thenReturn(Optional.of(event));

        when(placeRepository.findById(30L))
                .thenReturn(Optional.of(place));

        assertThrows(
                ReservationException.class,
                () -> reservationService.creerReservation(
                        20L,
                        request,
                        "user-123"
                )
        );

        verify(placeRepository)
                .findById(30L);

        verifyNoInteractions(
                reservationRepository,
                reservationMapper
        );
    }

    @Test
    void listerMesReservations_devraitRetournerLesReservationsDuParticipant() {

        Reservation reservation1 = new Reservation();
        reservation1.setId(100L);
        reservation1.setParticipant(participant);
        reservation1.setEvent(event);
        reservation1.setPlace(place);
        reservation1.setStatut(StatutReservation.EN_ATTENTE);

        Reservation reservation2 = new Reservation();
        reservation2.setId(101L);
        reservation2.setParticipant(participant);
        reservation2.setEvent(event);
        reservation2.setPlace(place);
        reservation2.setStatut(StatutReservation.CONFIRMEE);

        ReservationResponse response1 = new ReservationResponse();
        response1.setId(100L);
        response1.setEventId(20L);
        response1.setPlaceId(30L);
        response1.setNumeroPlace("A1");
        response1.setStatut("EN_ATTENTE");

        ReservationResponse response2 = new ReservationResponse();
        response2.setId(101L);
        response2.setEventId(20L);
        response2.setPlaceId(30L);
        response2.setNumeroPlace("A1");
        response2.setStatut("CONFIRMEE");

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(reservationRepository.findByParticipantId(1L))
                .thenReturn(java.util.List.of(reservation1, reservation2));

        when(reservationMapper.toResponse(reservation1))
                .thenReturn(response1);

        when(reservationMapper.toResponse(reservation2))
                .thenReturn(response2);

        var resultat = reservationService
                .listerMesReservations("user-123");

        assertNotNull(resultat);
        assertEquals(2, resultat.size());

        assertEquals(100L, resultat.get(0).getId());
        assertEquals("EN_ATTENTE", resultat.get(0).getStatut());

        assertEquals(101L, resultat.get(1).getId());
        assertEquals("CONFIRMEE", resultat.get(1).getStatut());

        verify(userRepository)
                .findBySupabaseUserId("user-123");

        verify(reservationRepository)
                .findByParticipantId(1L);

        verify(reservationMapper)
                .toResponse(reservation1);

        verify(reservationMapper)
                .toResponse(reservation2);
    }

    @Test
    void listerMesReservations_sansReservation_devraitRetournerListeVide() {

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(reservationRepository.findByParticipantId(1L))
                .thenReturn(java.util.List.of());

        var resultat = reservationService
                .listerMesReservations("user-123");

        assertNotNull(resultat);
        assertTrue(resultat.isEmpty());

        verify(reservationRepository)
                .findByParticipantId(1L);

        verifyNoInteractions(reservationMapper);
    }

    @Test
    void listerMesReservations_utilisateurInexistant_devraitLeverException() {

        when(userRepository.findBySupabaseUserId("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> reservationService
                        .listerMesReservations("unknown")
        );

        verify(userRepository)
                .findBySupabaseUserId("unknown");

        verifyNoInteractions(
                reservationRepository,
                reservationMapper
        );
    }

    @Test
    void listerMesReservations_utilisateurNonParticipant_devraitLeverException() {

        participant.setRole(Role.ORGANISATEUR);

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        assertThrows(
                RoleInvalideException.class,
                () -> reservationService
                        .listerMesReservations("user-123")
        );

        verify(userRepository)
                .findBySupabaseUserId("user-123");

        verifyNoInteractions(
                reservationRepository,
                reservationMapper
        );
    }

    @Test
    void annulerReservation_devraitAnnulerLaReservation() {

        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setParticipant(participant);
        reservation.setEvent(event);
        reservation.setPlace(place);
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(reservationRepository.findById(100L))
                .thenReturn(Optional.of(reservation));

        reservationService.annulerReservation(
                100L,
                "user-123"
        );

        assertEquals(
                StatutReservation.ANNULEE,
                reservation.getStatut()
        );

        verify(reservationRepository)
                .findById(100L);

        verify(reservationRepository)
                .save(reservation);
    }

    @Test
    void annulerReservation_reservationInexistante_devraitLeverException() {

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(reservationRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ReservationException.class,
                () -> reservationService.annulerReservation(
                        999L,
                        "user-123"
                )
        );

        verify(reservationRepository)
                .findById(999L);

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void annulerReservation_reservationAutreParticipant_devraitLeverException() {

        User autreParticipant = new User();
        autreParticipant.setId(2L);
        autreParticipant.setRole(Role.PARTICIPANT);

        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setParticipant(autreParticipant);
        reservation.setEvent(event);
        reservation.setPlace(place);
        reservation.setStatut(StatutReservation.CONFIRMEE);

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(reservationRepository.findById(100L))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                ReservationException.class,
                () -> reservationService.annulerReservation(
                        100L,
                        "user-123"
                )
        );

        assertEquals(
                StatutReservation.CONFIRMEE,
                reservation.getStatut()
        );

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void annulerReservation_dejaAnnulee_devraitLeverException() {

        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setParticipant(participant);
        reservation.setEvent(event);
        reservation.setPlace(place);
        reservation.setStatut(StatutReservation.ANNULEE);

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(reservationRepository.findById(100L))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                ReservationException.class,
                () -> reservationService.annulerReservation(
                        100L,
                        "user-123"
                )
        );

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void annulerReservation_utilisateurInexistant_devraitLeverException() {

        when(userRepository.findBySupabaseUserId("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> reservationService.annulerReservation(
                        100L,
                        "unknown"
                )
        );

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void annulerReservation_utilisateurNonParticipant_devraitLeverException() {

        participant.setRole(Role.ORGANISATEUR);

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        assertThrows(
                RoleInvalideException.class,
                () -> reservationService.annulerReservation(
                        100L,
                        "user-123"
                )
        );

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void creerReservation_placeDejaReservee_doitLeverReservationException() {

        when(userRepository.findBySupabaseUserId("user-123"))
                .thenReturn(Optional.of(participant));

        when(eventRepository.findById(20L))
                .thenReturn(Optional.of(event));

        when(placeRepository.findById(30L))
                .thenReturn(Optional.of(place));

        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenThrow(
                        new DataIntegrityViolationException("duplicate key")
                );

        ReservationException exception = assertThrows(
                ReservationException.class,
                () -> reservationService.creerReservation(
                        20L,
                        request,
                        "user-123"
                )
        );

        assertEquals(
                "Cette place est déjà réservée pour cet événement",
                exception.getMessage()
        );

        verify(reservationRepository)
                .saveAndFlush(any(Reservation.class));

        verifyNoInteractions(reservationMapper);
    }
}