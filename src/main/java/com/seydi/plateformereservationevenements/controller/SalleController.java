package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.dto.request.CreateSalleRequest;
import com.seydi.plateformereservationevenements.dto.request.UpdateSalleRequest;
import com.seydi.plateformereservationevenements.dto.response.SalleResponse;
import com.seydi.plateformereservationevenements.service.SalleService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salles")
public class SalleController {

    private final SalleService salleService;

    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @GetMapping
    public ResponseEntity<List<SalleResponse>> listerSalles() {

        return ResponseEntity.ok(
                salleService.listerSalles()
        );
    }

    @PostMapping
    public ResponseEntity<SalleResponse> creerSalle(
            @Valid @RequestBody CreateSalleRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        String supabaseUserId = jwt.getSubject();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        salleService.creerSalle(
                                request,
                                supabaseUserId
                        )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalleResponse> modifierSalle(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSalleRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        String supabaseUserId = jwt.getSubject();

        return ResponseEntity.ok(
                salleService.modifierSalle(
                        id,
                        request,
                        supabaseUserId
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerSalle(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        String supabaseUserId = jwt.getSubject();

        salleService.supprimerSalle(
                id,
                supabaseUserId
        );

        return ResponseEntity.noContent().build();
    }
}