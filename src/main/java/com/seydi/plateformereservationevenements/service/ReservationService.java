package com.seydi.plateformereservationevenements.service;

import com.seydi.plateformereservationevenements.dto.request.CreateReservationRequest;
import com.seydi.plateformereservationevenements.dto.response.ReservationResponse;
import com.seydi.plateformereservationevenements.model.Event;
import com.seydi.plateformereservationevenements.model.Place;
import com.seydi.plateformereservationevenements.model.Reservation;
import com.seydi.plateformereservationevenements.model.User;
import com.seydi.plateformereservationevenements.model.Role;
import com.seydi.plateformereservationevenements.model.StatutEvent;
import com.seydi.plateformereservationevenements.model.StatutReservation;
import com.seydi.plateformereservationevenements.exception.EventNotFoundException;
import com.seydi.plateformereservationevenements.exception.PlaceNotFoundException;
import com.seydi.plateformereservationevenements.exception.ReservationException;
import com.seydi.plateformereservationevenements.exception.RoleInvalideException;
import com.seydi.plateformereservationevenements.exception.UserNotFoundException;
import com.seydi.plateformereservationevenements.mapper.ReservationMapper;
import com.seydi.plateformereservationevenements.repository.EventRepository;
import com.seydi.plateformereservationevenements.repository.PlaceRepository;
import com.seydi.plateformereservationevenements.repository.ReservationRepository;
import com.seydi.plateformereservationevenements.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;

    public ReservationService(
            ReservationRepository reservationRepository,
            EventRepository eventRepository,
            PlaceRepository placeRepository,
            UserRepository userRepository,
            ReservationMapper reservationMapper
    ) {
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
        this.reservationMapper = reservationMapper;
    }

    private User trouverUserOuLeverException(String supabaseUserId){
        return userRepository.findBySupabaseUserId(supabaseUserId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable : " + supabaseUserId));
    }

    private Event trouverEventOuLeverException(Long id){
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Evenement introuvable : " + id));
    }

    private Place trouverPlaceOuLeverException(Long id){
        return placeRepository.findById(id)
                .orElseThrow(() -> new PlaceNotFoundException("Place introuvable : " + id));
    }

    @Transactional
    public ReservationResponse creerReservation(
            Long eventId,
            CreateReservationRequest request,
            String supabaseUserId
    ) {

        // 1. Récupérer le participant
        User participant = trouverUserOuLeverException(supabaseUserId);

        // 2. Vérifier son rôle
        if (participant.getRole() != Role.PARTICIPANT) {
            throw new RoleInvalideException(
                    "Seul un participant peut effectuer une réservation"
            );
        }

        // 3. Récupérer l'événement
        Event event = trouverEventOuLeverException(eventId);

        // 4. Vérifier que l'événement est publié
        if (event.getStatut() != StatutEvent.PUBLIE) {
            throw new ReservationException(
                    "Cet événement n'est pas disponible à la réservation"
            );
        }

        // 5. Récupérer la place
        Place place = trouverPlaceOuLeverException(request.getPlaceId());

        // 6. Vérifier que la place appartient
        //    à la salle de l'événement
        if (!place.getSalle().getId().equals(event.getSalle().getId())) {

            throw new ReservationException(
                    "Cette place n'appartient pas à la salle de l'événement"
            );
        }

        // 7. Créer la réservation
        Reservation reservation = new Reservation();

        reservation.setParticipant(participant);
        reservation.setEvent(event);
        reservation.setPlace(place);
        reservation.setStatut(
                StatutReservation.EN_ATTENTE
        );

        reservation.setCreatedAt(OffsetDateTime.now());

        try {
            Reservation reservationSauvegardee = reservationRepository.saveAndFlush(reservation);
            return reservationMapper.toResponse(reservationSauvegardee);

        } catch (DataIntegrityViolationException e) {
            throw new ReservationException("Cette place est déjà réservée pour cet événement");
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> listerMesReservations(
            String supabaseUserId
    ) {
        User participant = trouverUserOuLeverException(supabaseUserId);

        if (participant.getRole() != Role.PARTICIPANT) {
            throw new RoleInvalideException(
                    "Seul un participant peut consulter ses réservations"
            );
        }

        return reservationRepository
                .findByParticipantId(participant.getId())
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Transactional
    public void annulerReservation(
            Long reservationId,
            String supabaseUserId
    ) {
        User participant = trouverUserOuLeverException(supabaseUserId);

        if (participant.getRole() != Role.PARTICIPANT) {
            throw new RoleInvalideException(
                    "Seul un participant peut annuler une réservation"
            );
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new ReservationException(
                                "Réservation introuvable : " + reservationId
                        )
                );

        if (!reservation.getParticipant().getId()
                .equals(participant.getId())) {

            throw new ReservationException(
                    "Vous ne pouvez pas annuler cette réservation"
            );
        }

        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new ReservationException(
                    "Cette réservation est déjà annulée"
            );
        }

        reservation.setStatut(StatutReservation.ANNULEE);

        reservationRepository.save(reservation);
    }



}