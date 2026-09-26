package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.dto.request.CreateReservationRequest;
import com.seydi.plateformereservationevenements.dto.response.ReservationResponse;
import com.seydi.plateformereservationevenements.service.ReservationService;
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
}