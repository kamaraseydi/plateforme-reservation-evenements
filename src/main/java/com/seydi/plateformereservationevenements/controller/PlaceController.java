package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.dto.response.PlaceResponse;
import com.seydi.plateformereservationevenements.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @Operation(
            summary = "Lister les places disponibles",
            description = "Retourne les places disponibles pour un événement."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des places récupérée avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Événement introuvable"
            )
    })
    @GetMapping("/events/{eventId}/seats")
    public ResponseEntity<List<PlaceResponse>> listerPlacesDisponibles(
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(
                placeService.listerPlacesDisponibles(eventId)
        );
    }
}