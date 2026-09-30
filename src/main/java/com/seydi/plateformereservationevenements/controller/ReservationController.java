package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.dto.request.CreateReservationRequest;
import com.seydi.plateformereservationevenements.dto.response.EventReservationResponse;
import com.seydi.plateformereservationevenements.dto.response.ReservationResponse;
import com.seydi.plateformereservationevenements.service.ReservationService;
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
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(
            summary = "Créer une réservation",
            description = "Permet à un participant authentifié de réserver une place pour un événement."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Réservation créée avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données de réservation invalides"
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
                    description = "Événement ou place introuvable"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "La place est déjà réservée pour cet événement"
            )
    })
    @PostMapping("/events/{eventId}/reservations")
    public ResponseEntity<ReservationResponse> creerReservation(
            @PathVariable Long eventId,
            @Valid @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String supabaseUserId = jwt.getSubject();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        reservationService.creerReservation(
                                eventId,
                                request,
                                supabaseUserId
                        )
                );
    }

    @Operation(
            summary = "Lister mes réservations",
            description = "Retourne les réservations du participant authentifié."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Liste des réservations récupérée avec succès"
    )
    @GetMapping("/reservations/me")
    public ResponseEntity<List<ReservationResponse>> listerMesReservations(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String supabaseUserId = jwt.getSubject();

        return ResponseEntity.ok(
                reservationService.listerMesReservations(
                        supabaseUserId
                )
        );
    }

    @Operation(
            summary = "Annuler une réservation",
            description = "Permet à un participant d'annuler sa propre réservation."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Réservation annulée avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "L'utilisateur n'est pas autorisé à annuler cette réservation"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Réservation introuvable"
            )
    })
    @PatchMapping("/reservations/{id}/cancel")
    public ResponseEntity<Void> annulerReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String supabaseUserId = jwt.getSubject();

        reservationService.annulerReservation(
                id,
                supabaseUserId
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Lister les réservations d'un événement",
            description = "Permet à l'organisateur de consulter les réservations de son événement."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des réservations récupérée avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "L'utilisateur n'est pas autorisé à consulter cet événement"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Événement introuvable"
            )
    })
    @GetMapping("/events/{eventId}/reservations")
    public ResponseEntity<List<EventReservationResponse>> listerReservationsEvenement(
            @PathVariable Long eventId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String supabaseUserId = jwt.getSubject();

        return ResponseEntity.ok(
                reservationService.listerReservationsEvenement(
                        eventId,
                        supabaseUserId
                )
        );
    }
}