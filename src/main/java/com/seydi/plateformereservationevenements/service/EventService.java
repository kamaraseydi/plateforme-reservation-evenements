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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final SalleRepository salleRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final EventMapper eventMapper;


    public EventService(
            EventRepository eventRepository,
            SalleRepository salleRepository,
            UserRepository userRepository,
            ReservationRepository reservationRepository,
            EventMapper eventMapper
    ) {
        this.eventRepository = eventRepository;
        this.salleRepository = salleRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.reservationRepository = reservationRepository;
    }

    private User trouverUserOuLeverException(String supabaseUserId){
        return userRepository.findBySupabaseUserId(supabaseUserId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable : " + supabaseUserId));
    }

    private Salle trouverSalleOuLeverException(Long id){
        return salleRepository.findById(id)
                .orElseThrow(() -> new SalleNotFoundException("Salle introuvable"));
    }

    private Event trouverEventOuLeverException(Long id){
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Événement introuvable : "));
    }

    @Transactional
    public EventResponse creerEvent(CreateEventRequest request, String supabaseUserId) {

        // 1. Récupérer l'organisateur
        User organisateur = trouverUserOuLeverException(supabaseUserId);

        // 2. Vérifier le rôle
        if (organisateur.getRole() != Role.ORGANISATEUR) {
            throw new RoleInvalideException(
                    "Seul un ORGANISATEUR peut créer un événement"
            );
        }

        // 3. Récupérer la salle
        Salle salle = trouverSalleOuLeverException(request.getSalleId());

        // 4. Transformer le DTO en Event
        Event event = eventMapper.toEntity(request);

        // 5. Compléter les informations métier
        event.setOrganisateur(organisateur);
        event.setSalle(salle);
        event.setStatut(StatutEvent.BROUILLON);
        event.setCreatedAt(OffsetDateTime.now());

        // 6. Sauvegarder
        Event eventSauvegarde = eventRepository.save(event);

        // 7. Retourner le DTO
        return eventMapper.toResponse(eventSauvegarde);
    }

    public EventResponse modifierEvent(
            Long eventId,
            UpdateEventRequest request,
            String supabaseUserId
    ) {
        User organisateur = trouverUserOuLeverException(supabaseUserId);

        if (organisateur.getRole() != Role.ORGANISATEUR) {
            throw new RoleInvalideException(
                    "Seul un ORGANISATEUR peut modifier un événement"
            );
        }

        Event event = trouverEventOuLeverException(eventId);

        if (!event.getOrganisateur().getId().equals(organisateur.getId())) {
            throw new EventAccessDeniedException(
                    "Vous ne pouvez modifier que vos propres événements"
            );
        }

        if (event.getStatut() != StatutEvent.BROUILLON) {
            throw new EventModificationException(
                    "Un événement ne peut être modifié que lorsqu'il est en brouillon"
            );
        }

        Salle salle = trouverSalleOuLeverException(request.getSalleId());

        eventMapper.updateEntity(request, event);

        event.setSalle(salle);

        Event eventModifie = eventRepository.save(event);

        return eventMapper.toResponse(eventModifie);
    }

    public EventResponse publierEvent(
            Long eventId,
            String supabaseUserId
    ) {
        User organisateur = trouverUserOuLeverException(supabaseUserId);

        if (organisateur.getRole() != Role.ORGANISATEUR) {
            throw new RoleInvalideException(
                    "Seul un ORGANISATEUR peut publier un événement"
            );
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException(
                                "Événement introuvable : " + eventId
                        )
                );

        if (!event.getOrganisateur().getId().equals(organisateur.getId())) {
            throw new EventAccessDeniedException(
                    "Vous ne pouvez publier que vos propres événements"
            );
        }

        if (event.getStatut() != StatutEvent.BROUILLON) {
            throw new EventModificationException(
                    "Seul un événement en brouillon peut être publié"
            );
        }

        event.setStatut(StatutEvent.PUBLIE);

        Event eventPublie = eventRepository.save(event);

        return eventMapper.toResponse(eventPublie);
    }

    private static final List<StatutReservation> STATUTS_ACTIFS = List.of(
            StatutReservation.EN_ATTENTE,
            StatutReservation.CONFIRMEE
    );

    @Transactional
    public EventResponse annulerEvent(
            Long eventId,
            String supabaseUserId
    ) {
        User organisateur = trouverUserOuLeverException(supabaseUserId);

        if (organisateur.getRole() != Role.ORGANISATEUR) {
            throw new RoleInvalideException(
                    "Seul un ORGANISATEUR peut annuler un événement"
            );
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        "Événement introuvable : " + eventId
                ));

        if (!event.getOrganisateur().getId().equals(organisateur.getId())) {
            throw new EventAccessDeniedException(
                    "Vous ne pouvez annuler que vos propres événements"
            );
        }

        if (event.getStatut() == StatutEvent.ANNULE) {
            throw new EventModificationException(
                    "L'événement est déjà annulé"
            );
        }

        reservationRepository.annulerReservationsActives(
                eventId,
                STATUTS_ACTIFS,
                StatutReservation.ANNULEE
        );

        event.setStatut(StatutEvent.ANNULE);

        Event eventAnnule = eventRepository.save(event);

        return eventMapper.toResponse(eventAnnule);
    }

    public List<EventResponse> listerEvenementsPublies() {

        List<Event> events = eventRepository.findByStatut(StatutEvent.PUBLIE);

        return events.stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    public EventResponse trouverEvent(Long eventId) {

        Event event = trouverEventOuLeverException(eventId);

        return eventMapper.toResponse(event);
    }
}