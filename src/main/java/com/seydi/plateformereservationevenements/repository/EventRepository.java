package com.seydi.plateformereservationevenements.repository;

import com.seydi.plateformereservationevenements.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event,Long> {
}
