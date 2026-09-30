package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.dto.request.CreateEventRequest;
import com.seydi.plateformereservationevenements.dto.request.UpdateEventRequest;
import com.seydi.plateformereservationevenements.dto.response.EventResponse;
import com.seydi.plateformereservationevenements.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(
            summary = "Lister les événements publiés",
            description = "Retourne la liste des événements actuellement publiés."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des événements récupérée avec succès"
    )
    @GetMapping
    public ResponseEntity<List<EventResponse>> listerEvenementsPublies() {

        List<EventResponse> events = eventService.listerEvenementsPublies();

        return ResponseEntity.ok(events);
    }

    // GET /api/events/{id}
    @Operation(
            summary = "Récupérer un événement",
            description = "Retourne les informations d'un événement à partir de son identifiant."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Événement trouvé"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Événement introuvable"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> trouverEvent(@PathVariable Long id) {

        EventResponse event = eventService.trouverEvent(id);

        return ResponseEntity.ok(event);
    }

    // POST /api/events
    @Operation(
            summary = "Créer un événement",
            description = "Permet à un organisateur authentifié de créer un événement.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Événement créé avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès interdit"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur ou salle introuvable"
            )
    })
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
    @Operation(
            summary = "Modifier un événement",
            description = "Permet à l'organisateur propriétaire de modifier son événement.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Événement modifié avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "L'utilisateur n'est pas autorisé à modifier cet événement"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Événement introuvable"
            )
    })
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
    @Operation(
            summary = "Publier un événement",
            description = "Publie un événement appartenant à l'organisateur authentifié.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Événement publié avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "L'utilisateur n'est pas autorisé à publier cet événement"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Événement introuvable"
            )
    })
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
    @Operation(
            summary = "Annuler un événement",
            description = "Annule un événement appartenant à l'organisateur authentifié."
                    + " Les réservations confirmées associées sont également annulées.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Événement annulé avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "L'utilisateur n'est pas autorisé à annuler cet événement"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Événement introuvable"
            )
    })
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