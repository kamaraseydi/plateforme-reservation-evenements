package com.seydi.plateformereservationevenements.repository;

import com.seydi.plateformereservationevenements.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place,Long> {

    List<Place> findBySalleId(Long salleId);
}
