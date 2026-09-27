package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.dto.response.PlaceResponse;
import com.seydi.plateformereservationevenements.service.PlaceService;
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

    @GetMapping("/events/{eventId}/seats")
    public ResponseEntity<List<PlaceResponse>> listerPlacesDisponibles(
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(
                placeService.listerPlacesDisponibles(eventId)
        );
    }
}