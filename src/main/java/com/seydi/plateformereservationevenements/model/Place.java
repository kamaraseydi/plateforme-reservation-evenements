package com.seydi.plateformereservationevenements.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "place",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_place_salle_numero",
                        columnNames = {"salle_id", "numero"}
                )
        }
)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    public Place() {}

    public Place(Long id, String numero, Salle salle) {
        this.id = id;
        this.numero = numero;
        this.salle = salle;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Salle getSalle() { return salle; }
    public void setSalle(Salle salle) { this.salle = salle; }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                '}';
    }
}