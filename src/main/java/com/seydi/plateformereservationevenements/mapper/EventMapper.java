package com.seydi.plateformereservationevenements.mapper;

import com.seydi.plateformereservationevenements.dto.request.CreateEventRequest;
import com.seydi.plateformereservationevenements.dto.request.UpdateEventRequest;
import com.seydi.plateformereservationevenements.dto.response.EventResponse;
import com.seydi.plateformereservationevenements.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event toEntity(CreateEventRequest request) {

        Event event = new Event();

        event.setTitre(request.getTitre());
        event.setDescription(request.getDescription());
        event.setDateHeure(request.getDateHeure());

        return event;
    }

    public EventResponse toResponse(Event event) {

        EventResponse response = new EventResponse();

        response.setId(event.getId());
        response.setTitre(event.getTitre());
        response.setDescription(event.getDescription());
        response.setDateHeure(event.getDateHeure());
        response.setImageUrl(event.getImageUrl());
        response.setStatut(event.getStatut());
        response.setCreatedAt(event.getCreatedAt());

        if (event.getOrganisateur() != null) {
            response.setOrganisateur(event.getOrganisateur().getNom());
        }

        if (event.getSalle() != null) {
            response.setSalle(event.getSalle().getNom());
            response.setAdresse(event.getSalle().getAdresse());
        }

        return response;
    }

    public void updateEntity(UpdateEventRequest request, Event event) {
        event.setTitre(request.getTitre());
        event.setDescription(request.getDescription());
        event.setDateHeure(request.getDateHeure());
    }
}