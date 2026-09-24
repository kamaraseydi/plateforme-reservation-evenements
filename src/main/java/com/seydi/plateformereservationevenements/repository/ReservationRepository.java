package com.seydi.plateformereservationevenements.repository;

import com.seydi.plateformereservationevenements.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
}
