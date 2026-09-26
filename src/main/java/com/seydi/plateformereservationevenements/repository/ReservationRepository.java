package com.seydi.plateformereservationevenements.repository;

import com.seydi.plateformereservationevenements.model.Reservation;
import com.seydi.plateformereservationevenements.model.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Modifying
    @Query("""
        UPDATE Reservation r
        SET r.statut = :nouveauStatut
        WHERE r.event.id = :eventId
        AND r.statut IN :statuts
    """)
    int annulerReservationsActives(
            @Param("eventId") Long eventId,
            @Param("statuts") List<StatutReservation> statuts,
            @Param("nouveauStatut") StatutReservation nouveauStatut
    );

    List<Reservation> findByParticipantId(Long participantId);
}