package com.seydi.plateformereservationevenements.service;

import com.seydi.plateformereservationevenements.dto.response.PlaceResponse;
import com.seydi.plateformereservationevenements.exception.EventNotFoundException;
import com.seydi.plateformereservationevenements.mapper.PlaceMapper;
import com.seydi.plateformereservationevenements.model.*;
import com.seydi.plateformereservationevenements.repository.EventRepository;
import com.seydi.plateformereservationevenements.repository.PlaceRepository;
import com.seydi.plateformereservationevenements.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PlaceService {

    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;
    private final ReservationRepository reservationRepository;
    private final PlaceMapper placeMapper;

    public PlaceService(
            EventRepository eventRepository,
            PlaceRepository placeRepository,
            ReservationRepository reservationRepository,
            PlaceMapper placeMapper
    ) {
        this.eventRepository = eventRepository;
        this.placeRepository = placeRepository;
        this.reservationRepository = reservationRepository;
        this.placeMapper = placeMapper;
    }

    private Event trouverEventOuLeverException(Long id){
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Evenement introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<PlaceResponse> listerPlacesDisponibles(Long eventId) {

        Event event = trouverEventOuLeverException(eventId);

        Long salleId = event.getSalle().getId();

        List<Place> places = placeRepository.findBySalleId(salleId);

        List<Reservation> reservationsActives =
                reservationRepository.findByEventIdAndStatutIn(
                        eventId,
                        List.of(
                                StatutReservation.EN_ATTENTE,
                                StatutReservation.CONFIRMEE
                        )
                );

        Set<Long> placesOccupees = new HashSet<>();

        for (var reservation : reservationsActives) {
            placesOccupees.add(reservation.getPlace().getId());
        }

        return places.stream()
                .map(place -> placeMapper.toResponse(place,
                                !placesOccupees.contains(place.getId())
                        )
                )
                .toList();
    }
}