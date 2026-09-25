package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.dto.request.CreateEventRequest;
import com.seydi.plateformereservationevenements.dto.request.UpdateEventRequest;
import com.seydi.plateformereservationevenements.dto.response.EventResponse;
import com.seydi.plateformereservationevenements.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET /api/events
    @GetMapping
    public ResponseEntity<List<EventResponse>> listerEvenementsPublies() {

        List<EventResponse> events = eventService.listerEvenementsPublies();

        return ResponseEntity.ok(events);
    }

    // GET /api/events/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> trouverEvent(@PathVariable Long id) {

        EventResponse event = eventService.trouverEvent(id);

        return ResponseEntity.ok(event);
    }

    // POST /api/events
    @PostMapping
    public ResponseEntity<EventResponse> creerEvent(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        String supabaseUserId = jwt.getSubject();

        EventResponse event = eventService.creerEvent(request, supabaseUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(event);
    }

    // PUT /api/events/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> modifierEvent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        String supabaseUserId = jwt.getSubject();

        EventResponse event = eventService.modifierEvent(id, request, supabaseUserId);

        return ResponseEntity.ok(event);
    }

    // PATCH /api/events/{id}/publish
    @PatchMapping("/{id}/publish")
    public ResponseEntity<EventResponse> publierEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        String supabaseUserId = jwt.getSubject();

        EventResponse event = eventService.publierEvent(id, supabaseUserId);

        return ResponseEntity.ok(event);
    }

    // PATCH /api/events/{id}/cancel
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventResponse> annulerEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        String supabaseUserId = jwt.getSubject();

        EventResponse event = eventService.annulerEvent(id, supabaseUserId);

        return ResponseEntity.ok(event);
    }
}