package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.dto.request.CreateSalleRequest;
import com.seydi.plateformereservationevenements.dto.request.UpdateSalleRequest;
import com.seydi.plateformereservationevenements.dto.response.SalleResponse;
import com.seydi.plateformereservationevenements.service.SalleService;
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
@RequestMapping("/api/salles")
public class SalleController {

    private final SalleService salleService;

    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @Operation(
            summary = "Lister les salles",
            description = "Retourne la liste des salles disponibles."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des salles récupérée avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès interdit"
            )
    })
    @GetMapping
    public ResponseEntity<List<SalleResponse>> listerSalles() {

        return ResponseEntity.ok(
                salleService.listerSalles()
        );
    }

    @Operation(
            summary = "Créer une salle",
            description = "Permet à un administrateur authentifié de créer une salle et ses places."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Salle créée avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données de la salle invalides"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès réservé à un administrateur"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Une salle portant ce nom existe déjà"
            )
    })
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

    @Operation(
            summary = "Modifier une salle",
            description = "Permet à un administrateur authentifié de modifier une salle."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Salle modifiée avec succès"
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
                    description = "Accès réservé à un administrateur"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Salle introuvable"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Une salle portant ce nom existe déjà"
            )
    })
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

    @Operation(
            summary = "Supprimer une salle",
            description = "Permet à un administrateur authentifié de supprimer une salle."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Salle supprimée avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentification requise"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès réservé à un administrateur"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Salle introuvable"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "La salle ne peut pas être supprimée dans son état actuel"
            )
    })
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