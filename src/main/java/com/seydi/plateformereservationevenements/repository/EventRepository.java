package com.seydi.plateformereservationevenements.repository;


import com.seydi.plateformereservationevenements.model.Event;
import com.seydi.plateformereservationevenements.model.StatutEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {

    List<Event> findByStatut(StatutEvent statut);

}
