package com.seydi.plateformereservationevenements.service;

import com.seydi.plateformereservationevenements.dto.request.CreateEventRequest;
import com.seydi.plateformereservationevenements.dto.request.UpdateEventRequest;
import com.seydi.plateformereservationevenements.dto.response.EventResponse;
import com.seydi.plateformereservationevenements.exception.*;
import com.seydi.plateformereservationevenements.mapper.EventMapper;
import com.seydi.plateformereservationevenements.model.*;
import com.seydi.plateformereservationevenements.repository.EventRepository;
import com.seydi.plateformereservationevenements.repository.ReservationRepository;
import com.seydi.plateformereservationevenements.repository.SalleRepository;
import com.seydi.plateformereservationevenements.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SalleRepository salleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventMapper eventMapper;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(
                eventRepository,
                salleRepository,
                userRepository,
                reservationRepository,
                eventMapper
        );
    }

    @Test
    void creerEvent_organisateurValide_doitCreerEvent() {

        CreateEventRequest request = new CreateEventRequest();

        request.setTitre("Concert Dakar Music Festival");
        request.setDescription(
                "Un grand concert de musique sénégalaise"
        );
        request.setDateHeure(
                OffsetDateTime.now().plusDays(30)
        );
        request.setSalleId(1L);

        User organisateur = new User();
        organisateur.setId(10L);
        organisateur.setSupabaseUserId("supabase-123");
        organisateur.setNom("Seydi Productions");
        organisateur.setEmail("contact@seydi.com");
        organisateur.setRole(Role.ORGANISATEUR);

        Salle salle = new Salle();
        salle.setId(1L);
        salle.setNom("Salle Teranga");
        salle.setAdresse("Dakar, Sénégal");
        salle.setCapacite(500);

        Event event = new Event();

        Event eventSauvegarde = new Event();
        eventSauvegarde.setId(100L);

        EventResponse response = new EventResponse();
        response.setId(100L);

        when(userRepository.findBySupabaseUserId("supabase-123"))
                .thenReturn(Optional.of(organisateur));

        when(salleRepository.findById(1L))
                .thenReturn(Optional.of(salle));

        when(eventMapper.toEntity(request))
                .thenReturn(event);

        when(eventRepository.save(event))
                .thenReturn(eventSauvegarde);

        when(eventMapper.toResponse(eventSauvegarde))
                .thenReturn(response);

        EventResponse result =
                eventService.creerEvent(request, "supabase-123");

        assertThat(result).isSameAs(response);

        verify(userRepository)
                .findBySupabaseUserId("supabase-123");

        assertThat(event.getOrganisateur())
                .isSameAs(organisateur);

        assertThat(event.getSalle())
                .isSameAs(salle);

        assertThat(event.getStatut())
                .isEqualTo(StatutEvent.BROUILLON);

        assertThat(event.getCreatedAt())
                .isNotNull();

        verify(salleRepository)
                .findById(1L);

        verify(eventMapper)
                .toEntity(request);

        verify(eventRepository)
                .save(event);

        verify(eventMapper)
                .toResponse(eventSauvegarde);
    }

    @Test
    void creerEvent_utilisateurInexistant_doitLeverException() {

        CreateEventRequest request = new CreateEventRequest();
        request.setSalleId(1L);

        when(userRepository.findBySupabaseUserId("inconnu"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                eventService.creerEvent(request, "inconnu")
        )
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository)
                .findBySupabaseUserId("inconnu");

        verifyNoInteractions(salleRepository);
        verifyNoInteractions(eventRepository);
    }

    @Test
    void creerEvent_utilisateurParticipant_doitLeverException() {

        User participant = new User();
        participant.setId(20L);
        participant.setRole(Role.PARTICIPANT);

        when(userRepository.findBySupabaseUserId("participant-123"))
                .thenReturn(Optional.of(participant));

        CreateEventRequest request = new CreateEventRequest();
        request.setSalleId(1L);

        assertThatThrownBy(() ->
                eventService.creerEvent(request, "participant-123")
        )
                .isInstanceOf(RoleInvalideException.class);

        verify(userRepository)
                .findBySupabaseUserId("participant-123");

        verifyNoInteractions(salleRepository);
        verifyNoInteractions(eventRepository);
    }

    @Test
    void creerEvent_salleInexistante_doitLeverException() {

        User organisateur = new User();
        organisateur.setId(10L);
        organisateur.setRole(Role.ORGANISATEUR);

        CreateEventRequest request = new CreateEventRequest();
        request.setSalleId(999L);

        when(userRepository.findBySupabaseUserId("supabase-123"))
                .thenReturn(Optional.of(organisateur));

        when(salleRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                eventService.creerEvent(request, "supabase-123")
        )
                .isInstanceOf(SalleNotFoundException.class);

        verify(userRepository)
                .findBySupabaseUserId("supabase-123");

        verify(salleRepository)
                .findById(999L);

        verifyNoInteractions(eventRepository);
    }

    @Test
    void modifierEvent_devraitModifierEvent() {

        String supabaseUserId = "supabase-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setSupabaseUserId(supabaseUserId);
        organisateur.setRole(Role.ORGANISATEUR);

        Salle ancienneSalle = new Salle();
        ancienneSalle.setId(1L);

        Salle nouvelleSalle = new Salle();
        nouvelleSalle.setId(2L);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(organisateur);
        event.setSalle(ancienneSalle);
        event.setStatut(StatutEvent.BROUILLON);

        UpdateEventRequest request = new UpdateEventRequest();
        request.setTitre("Nouveau titre");
        request.setDescription("Nouvelle description de l'événement");
        request.setDateHeure(
                OffsetDateTime.now().plusDays(10)
        );
        request.setSalleId(2L);

        EventResponse response = new EventResponse();
        response.setId(10L);
        response.setTitre("Nouveau titre");

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(salleRepository.findById(2L))
                .thenReturn(Optional.of(nouvelleSalle));

        when(eventRepository.save(event))
                .thenReturn(event);

        when(eventMapper.toResponse(event))
                .thenReturn(response);

        EventResponse resultat = eventService.modifierEvent(
                10L,
                request,
                supabaseUserId
        );

        assertThat(resultat).isSameAs(response);

        verify(eventMapper).updateEntity(request, event);

        assertThat(event.getSalle()).isSameAs(nouvelleSalle);

        verify(eventRepository).save(event);
        verify(eventMapper).toResponse(event);
    }

    @Test
    void modifierEvent_devraitLeverExceptionSiUtilisateurIntrouvable() {

        String supabaseUserId = "inconnu";

        UpdateEventRequest request = new UpdateEventRequest();

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                eventService.modifierEvent(
                        10L,
                        request,
                        supabaseUserId
                )
        )
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(eventRepository);
        verifyNoInteractions(salleRepository);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void modifierEvent_devraitLeverExceptionSiUtilisateurNonOrganisateur() {

        String supabaseUserId = "participant-123";

        User participant = new User();
        participant.setId(2L);
        participant.setSupabaseUserId(supabaseUserId);
        participant.setRole(Role.PARTICIPANT);

        UpdateEventRequest request = new UpdateEventRequest();

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(participant));

        assertThatThrownBy(() ->
                eventService.modifierEvent(
                        10L,
                        request,
                        supabaseUserId
                )
        )
                .isInstanceOf(RoleInvalideException.class);

        verifyNoInteractions(eventRepository);
        verifyNoInteractions(salleRepository);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void modifierEvent_devraitLeverExceptionSiEventIntrouvable() {

        String supabaseUserId = "supabase-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setRole(Role.ORGANISATEUR);

        UpdateEventRequest request = new UpdateEventRequest();

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                eventService.modifierEvent(
                        10L,
                        request,
                        supabaseUserId
                )
        )
                .isInstanceOf(EventNotFoundException.class);

        verify(eventRepository).findById(10L);
        verifyNoInteractions(salleRepository);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void modifierEvent_devraitRefuserSiOrganisateurNonProprietaire() {

        String supabaseUserId = "organisateur-2";

        User organisateurConnecte = new User();
        organisateurConnecte.setId(2L);
        organisateurConnecte.setRole(Role.ORGANISATEUR);

        User proprietaire = new User();
        proprietaire.setId(1L);
        proprietaire.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(proprietaire);
        event.setStatut(StatutEvent.BROUILLON);

        UpdateEventRequest request = new UpdateEventRequest();

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateurConnecte));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        assertThatThrownBy(() ->
                eventService.modifierEvent(
                        10L,
                        request,
                        supabaseUserId
                )
        )
                .isInstanceOf(EventAccessDeniedException.class);

        verifyNoInteractions(salleRepository);
        verifyNoInteractions(eventMapper);
        verify(eventRepository, never()).save(any());
    }


    @Test
    void modifierEvent_devraitRefuserSiEventDejaPublie() {

        String supabaseUserId = "organisateur-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(organisateur);
        event.setStatut(StatutEvent.PUBLIE);

        UpdateEventRequest request = new UpdateEventRequest();

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        assertThatThrownBy(() ->
                eventService.modifierEvent(
                        10L,
                        request,
                        supabaseUserId
                )
        )
                .isInstanceOf(EventModificationException.class);

        verifyNoInteractions(salleRepository);
        verifyNoInteractions(eventMapper);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void modifierEvent_devraitLeverExceptionSiSalleIntrouvable() {

        String supabaseUserId = "organisateur-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(organisateur);
        event.setStatut(StatutEvent.BROUILLON);

        UpdateEventRequest request = new UpdateEventRequest();
        request.setSalleId(99L);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(salleRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                eventService.modifierEvent(
                        10L,
                        request,
                        supabaseUserId
                )
        )
                .isInstanceOf(SalleNotFoundException.class);

        verifyNoInteractions(eventMapper);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void publierEvent_devraitPublierEvent() {

        String supabaseUserId = "organisateur-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setSupabaseUserId(supabaseUserId);
        organisateur.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(organisateur);
        event.setStatut(StatutEvent.BROUILLON);

        EventResponse response = new EventResponse();
        response.setId(10L);
        response.setStatut(StatutEvent.PUBLIE);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(eventRepository.save(event))
                .thenReturn(event);

        when(eventMapper.toResponse(event))
                .thenReturn(response);

        EventResponse resultat =
                eventService.publierEvent(10L, supabaseUserId);

        assertThat(event.getStatut())
                .isEqualTo(StatutEvent.PUBLIE);

        assertThat(resultat)
                .isSameAs(response);

        verify(eventRepository).save(event);
        verify(eventMapper).toResponse(event);
    }

    @Test
    void publierEvent_devraitLeverExceptionSiUtilisateurIntrouvable() {

        String supabaseUserId = "inconnu";

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                eventService.publierEvent(10L, supabaseUserId)
        )
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(eventRepository);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void publierEvent_devraitLeverExceptionSiUtilisateurNonOrganisateur() {

        String supabaseUserId = "participant-123";

        User participant = new User();
        participant.setId(2L);
        participant.setRole(Role.PARTICIPANT);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(participant));

        assertThatThrownBy(() ->
                eventService.publierEvent(10L, supabaseUserId)
        )
                .isInstanceOf(RoleInvalideException.class);

        verifyNoInteractions(eventRepository);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void publierEvent_devraitLeverExceptionSiEventIntrouvable() {

        String supabaseUserId = "organisateur-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setRole(Role.ORGANISATEUR);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                eventService.publierEvent(10L, supabaseUserId)
        )
                .isInstanceOf(EventNotFoundException.class);

        verify(eventRepository).findById(10L);
        verify(eventRepository, never()).save(any());
        verifyNoInteractions(eventMapper);
    }

    @Test
    void publierEvent_devraitRefuserSiOrganisateurNonProprietaire() {

        String supabaseUserId = "organisateur-2";

        User organisateurConnecte = new User();
        organisateurConnecte.setId(2L);
        organisateurConnecte.setRole(Role.ORGANISATEUR);

        User proprietaire = new User();
        proprietaire.setId(1L);
        proprietaire.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(proprietaire);
        event.setStatut(StatutEvent.BROUILLON);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateurConnecte));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        assertThatThrownBy(() ->
                eventService.publierEvent(10L, supabaseUserId)
        )
                .isInstanceOf(EventAccessDeniedException.class);

        verify(eventRepository, never()).save(any());
        verifyNoInteractions(eventMapper);
    }

    @Test
    void publierEvent_devraitRefuserSiEventDejaPublie() {

        String supabaseUserId = "organisateur-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(organisateur);
        event.setStatut(StatutEvent.PUBLIE);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        assertThatThrownBy(() ->
                eventService.publierEvent(10L, supabaseUserId)
        )
                .isInstanceOf(EventModificationException.class);

        verify(eventRepository, never()).save(any());
        verifyNoInteractions(eventMapper);
    }

    @Test
    void publierEvent_devraitRefuserSiEventAnnule() {

        String supabaseUserId = "organisateur-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(organisateur);
        event.setStatut(StatutEvent.ANNULE);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        assertThatThrownBy(() ->
                eventService.publierEvent(10L, supabaseUserId)
        )
                .isInstanceOf(EventModificationException.class);

        verify(eventRepository, never()).save(any());
        verifyNoInteractions(eventMapper);
    }

    @Test
    void annulerEvent_devraitAnnulerEventEtReservationsActives() {

        String supabaseUserId = "organisateur-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setSupabaseUserId(supabaseUserId);
        organisateur.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(organisateur);
        event.setStatut(StatutEvent.PUBLIE);

        EventResponse response = new EventResponse();
        response.setId(10L);
        response.setStatut(StatutEvent.ANNULE);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(eventRepository.save(event))
                .thenReturn(event);

        when(eventMapper.toResponse(event))
                .thenReturn(response);

        EventResponse resultat =
                eventService.annulerEvent(10L, supabaseUserId);

        assertThat(event.getStatut())
                .isEqualTo(StatutEvent.ANNULE);

        assertThat(resultat)
                .isSameAs(response);

        verify(reservationRepository).annulerReservationsActives(
                10L,
                List.of(
                        StatutReservation.EN_ATTENTE,
                        StatutReservation.CONFIRMEE
                ),
                StatutReservation.ANNULEE
        );

        verify(eventRepository).save(event);
        verify(eventMapper).toResponse(event);
    }

    @Test
    void annulerEvent_devraitLeverExceptionSiUtilisateurIntrouvable() {

        String supabaseUserId = "inconnu";

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                eventService.annulerEvent(10L, supabaseUserId)
        )
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(eventRepository);
        verifyNoInteractions(reservationRepository);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void annulerEvent_devraitLeverExceptionSiUtilisateurNonOrganisateur() {

        String supabaseUserId = "participant-123";

        User participant = new User();
        participant.setId(2L);
        participant.setRole(Role.PARTICIPANT);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(participant));

        assertThatThrownBy(() ->
                eventService.annulerEvent(10L, supabaseUserId)
        )
                .isInstanceOf(RoleInvalideException.class);

        verifyNoInteractions(eventRepository);
        verifyNoInteractions(reservationRepository);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void annulerEvent_devraitLeverExceptionSiEventIntrouvable() {

        String supabaseUserId = "organisateur-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setRole(Role.ORGANISATEUR);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                eventService.annulerEvent(10L, supabaseUserId)
        )
                .isInstanceOf(EventNotFoundException.class);

        verify(eventRepository).findById(10L);
        verify(eventRepository, never()).save(any());

        verifyNoInteractions(reservationRepository);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void annulerEvent_devraitRefuserSiOrganisateurNonProprietaire() {

        String supabaseUserId = "organisateur-2";

        User organisateurConnecte = new User();
        organisateurConnecte.setId(2L);
        organisateurConnecte.setRole(Role.ORGANISATEUR);

        User proprietaire = new User();
        proprietaire.setId(1L);
        proprietaire.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(proprietaire);
        event.setStatut(StatutEvent.PUBLIE);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateurConnecte));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        assertThatThrownBy(() ->
                eventService.annulerEvent(10L, supabaseUserId)
        )
                .isInstanceOf(EventAccessDeniedException.class);

        verify(eventRepository, never()).save(any());
        verifyNoInteractions(reservationRepository);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void annulerEvent_devraitRefuserSiEventDejaAnnule() {

        String supabaseUserId = "organisateur-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(organisateur);
        event.setStatut(StatutEvent.ANNULE);

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        assertThatThrownBy(() ->
                eventService.annulerEvent(10L, supabaseUserId)
        )
                .isInstanceOf(EventModificationException.class);

        verify(eventRepository, never()).save(any());
        verifyNoInteractions(reservationRepository);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void annulerEvent_devraitAnnulerUnEventEnBrouillon() {

        String supabaseUserId = "organisateur-123";

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setRole(Role.ORGANISATEUR);

        Event event = new Event();
        event.setId(10L);
        event.setOrganisateur(organisateur);
        event.setStatut(StatutEvent.BROUILLON);

        EventResponse response = new EventResponse();

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(organisateur));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(eventRepository.save(event))
                .thenReturn(event);

        when(eventMapper.toResponse(event))
                .thenReturn(response);

        EventResponse resultat =
                eventService.annulerEvent(10L, supabaseUserId);

        assertThat(event.getStatut())
                .isEqualTo(StatutEvent.ANNULE);

        assertThat(resultat)
                .isSameAs(response);

        verify(reservationRepository).annulerReservationsActives(
                10L,
                List.of(
                        StatutReservation.EN_ATTENTE,
                        StatutReservation.CONFIRMEE
                ),
                StatutReservation.ANNULEE
        );

        verify(eventRepository).save(event);
    }

    @Test
    void listerEvenementsPublies_devraitRetournerEvenementsPublies() {

        Event event1 = new Event();
        event1.setId(1L);
        event1.setStatut(StatutEvent.PUBLIE);

        Event event2 = new Event();
        event2.setId(2L);
        event2.setStatut(StatutEvent.PUBLIE);

        EventResponse response1 = new EventResponse();
        response1.setId(1L);

        EventResponse response2 = new EventResponse();
        response2.setId(2L);

        when(eventRepository.findByStatut(StatutEvent.PUBLIE))
                .thenReturn(List.of(event1, event2));

        when(eventMapper.toResponse(event1))
                .thenReturn(response1);

        when(eventMapper.toResponse(event2))
                .thenReturn(response2);

        List<EventResponse> resultat =
                eventService.listerEvenementsPublies();

        assertThat(resultat)
                .containsExactly(response1, response2);

        verify(eventRepository)
                .findByStatut(StatutEvent.PUBLIE);

        verify(eventMapper).toResponse(event1);
        verify(eventMapper).toResponse(event2);
    }

    @Test
    void listerEvenementsPublies_devraitRetournerListeVideSiAucunEventPublie() {

        when(eventRepository.findByStatut(StatutEvent.PUBLIE))
                .thenReturn(List.of());

        List<EventResponse> resultat =
                eventService.listerEvenementsPublies();

        assertThat(resultat)
                .isEmpty();

        verify(eventRepository)
                .findByStatut(StatutEvent.PUBLIE);

        verifyNoInteractions(eventMapper);
    }

    @Test
    void trouverEvent_devraitRetournerEvent() {

        Event event = new Event();
        event.setId(10L);
        event.setTitre("Concert Dakar");

        EventResponse response = new EventResponse();
        response.setId(10L);
        response.setTitre("Concert Dakar");

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(eventMapper.toResponse(event))
                .thenReturn(response);

        EventResponse resultat =
                eventService.trouverEvent(10L);

        assertThat(resultat)
                .isSameAs(response);

        verify(eventRepository)
                .findById(10L);

        verify(eventMapper)
                .toResponse(event);
    }

    @Test
    void trouverEvent_devraitLeverExceptionSiEventIntrouvable() {

        when(eventRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                eventService.trouverEvent(10L)
        )
                .isInstanceOf(EventNotFoundException.class);

        verify(eventRepository)
                .findById(10L);

        verifyNoInteractions(eventMapper);
    }

}