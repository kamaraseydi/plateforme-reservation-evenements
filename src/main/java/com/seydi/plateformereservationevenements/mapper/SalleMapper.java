package com.seydi.plateformereservationevenements.mapper;

import com.seydi.plateformereservationevenements.dto.request.CreateSalleRequest;
import com.seydi.plateformereservationevenements.dto.request.UpdateSalleRequest;
import com.seydi.plateformereservationevenements.dto.response.SalleResponse;
import com.seydi.plateformereservationevenements.model.Place;
import com.seydi.plateformereservationevenements.model.Salle;

import org.springframework.stereotype.Component;

@Component
public class SalleMapper {

    public Salle toEntity(CreateSalleRequest request) {

        Salle salle = new Salle();

        salle.setNom(request.getNom());
        salle.setAdresse(request.getAdresse());

        return salle;
    }

    public SalleResponse toResponse(Salle salle) {

        SalleResponse response = new SalleResponse();

        response.setId(salle.getId());
        response.setNom(salle.getNom());
        response.setAdresse(salle.getAdresse());
        response.setCapacite(salle.getCapacite());
        response.setCreatedAt(salle.getCreatedAt());

        if (salle.getPlaces() != null) {
            response.setPlaces(
                    salle.getPlaces()
                            .stream()
                            .map(Place::getNumero)
                            .toList()
            );
        }

        return response;
    }

    public void updateEntity(
            UpdateSalleRequest request,
            Salle salle
    ) {

        salle.setNom(request.getNom());
        salle.setAdresse(request.getAdresse());
    }
}