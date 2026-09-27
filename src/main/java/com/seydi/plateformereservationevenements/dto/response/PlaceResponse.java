package com.seydi.plateformereservationevenements.dto.response;

public class PlaceResponse {

    private Long id;
    private String numero;
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