package com.seydi.plateformereservationevenements.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informations d'une place")
public class PlaceResponse {

    @Schema(
            description = "Identifiant unique de la place",
            example = "15"
    )
    private Long id;

    @Schema(
            description = "Numéro ou identifiant de la place",
            example = "A15"
    )
    private String numero;

    @Schema(
            description = "Indique si la place peut actuellement être réservée",
            example = "true"
    )
    private boolean disponible;

    public PlaceResponse() {
    }

    public PlaceResponse(Long id, String numero, boolean disponible) {
        this.id = id;
        this.numero = numero;
        this.disponible = disponible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}