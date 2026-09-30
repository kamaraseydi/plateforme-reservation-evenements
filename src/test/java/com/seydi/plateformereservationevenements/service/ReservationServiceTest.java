package com.seydi.plateformereservationevenements.service;

import com.seydi.plateformereservationevenements.dto.request.CreateReservationRequest;
import com.seydi.plateformereservationevenements.dto.response.EventReservationResponse;
import com.seydi.plateformereservationevenements.dto.response.ReservationResponse;
import com.seydi.plateformereservationevenements.exception.*;
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

import java.util.List;
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
    void listerReservationsEvenement_organisateurProprietaire_devraitRetournerReservations() {

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setSupabaseUserId("organisateur-123");
        organisateur.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(organisateur);

        Reservation reservation = new Reservation();
        reservation.setId(100L);

        when(userRepository.findBySupabaseUserId("organisateur-123"))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(reservationRepository.findByEventId(10L))
                .thenReturn(List.of(reservation));

        EventReservationResponse response =
                new EventReservationResponse();

        when(reservationMapper.toEventReservationResponse(reservation))
                .thenReturn(response);

        List<EventReservationResponse> result =
                reservationService.listerReservationsEvenement(
                        10L,
                        "organisateur-123"
                );

        assertEquals(1, result.size());
        assertSame(response, result.get(0));

        verify(reservationRepository).findByEventId(10L);
        verify(reservationMapper).toEventReservationResponse(reservation);
    }

    @Test
    void listerReservationsEvenement_participant_devraitLeverRoleInvalideException() {

        User participant = new User();
        participant.setId(1L);
        participant.setSupabaseUserId("participant-123");
        participant.setRole(Role.PARTICIPANT);

        when(userRepository.findBySupabaseUserId("participant-123"))
                .thenReturn(Optional.of(participant));

        assertThrows(
                RoleInvalideException.class,
                () -> reservationService.listerReservationsEvenement(
                        10L,
                        "participant-123"
                )
        );

        verifyNoInteractions(eventRepository);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void listerReservationsEvenement_autreOrganisateur_devraitLeverAccessDeniedException() {

        User organisateurConnecte = new User();
        organisateurConnecte.setId(1L);
        organisateurConnecte.setSupabaseUserId("organisateur-123");
        organisateurConnecte.setRole(Role.ORGANISATEUR);

        User proprietaire = new User();
        proprietaire.setId(2L);
        proprietaire.setSupabaseUserId("autre-organisateur");
        proprietaire.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(proprietaire);

        when(userRepository.findBySupabaseUserId("organisateur-123"))
                .thenReturn(Optional.of(organisateurConnecte));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        assertThrows(
                EventAccessDeniedException.class,
                () -> reservationService.listerReservationsEvenement(
                        10L,
                        "organisateur-123"
                )
        );

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void listerReservationsEvenement_evenementInexistant_devraitLeverException() {

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setSupabaseUserId("organisateur-123");
        organisateur.setRole(Role.ORGANISATEUR);

        when(userRepository.findBySupabaseUserId("organisateur-123"))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                EventNotFoundException.class,
                () -> reservationService.listerReservationsEvenement(
                        99L,
                        "organisateur-123"
                )
        );

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void listerReservationsEvenement_sansReservation_devraitRetournerListeVide() {

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setSupabaseUserId("organisateur-123");
        organisateur.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(organisateur);

        when(userRepository.findBySupabaseUserId("organisateur-123"))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(reservationRepository.findByEventId(10L))
                .thenReturn(List.of());

        List<EventReservationResponse> result =
                reservationService.listerReservationsEvenement(
                        10L,
                        "organisateur-123"
                );

        assertTrue(result.isEmpty());

        verify(reservationRepository).findByEventId(10L);
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
    void creerReservation_placeDejaReservee_doitLeverReservationAlreadyExistsException() {
        // Arrange
        Long eventId = 1L;
        Long placeId = 10L;
        String supabaseUserId = "participant-123";

        User participant = new User();
        participant.setId(1L);
        participant.setSupabaseUserId(supabaseUserId);
        participant.setRole(Role.PARTICIPANT);

        Salle salle = new Salle();
        salle.setId(5L);

        Event event = new Event();
        event.setId(eventId);
        event.setSalle(salle);
        event.setStatut(StatutEvent.PUBLIE);

        Place place = new Place();
        place.setId(placeId);
        place.setSalle(salle);

        CreateReservationRequest request = new CreateReservationRequest();
        request.setPlaceId(placeId);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(participant));

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        when(placeRepository.findById(placeId))
                .thenReturn(Optional.of(place));

        when(reservationRepository
                .existsByEventIdAndPlaceIdAndStatutIn(
                        eq(eventId),
                        eq(placeId),
                        anyList()
                ))
                .thenReturn(true);

        // Act & Assert
        assertThrows(
                ReservationAlreadyExistsException.class,
                () -> reservationService.creerReservation(
                        eventId,
                        request,
                        supabaseUserId
                )
        );

        verify(reservationRepository, never()).saveAndFlush(any());
    }
}