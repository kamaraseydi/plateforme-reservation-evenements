package com.seydi.plateformereservationevenements.repository;

import com.seydi.plateformereservationevenements.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place,Long> {
}
