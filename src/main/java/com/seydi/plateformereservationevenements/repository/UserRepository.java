package com.seydi.plateformereservationevenements.repository;

import com.seydi.plateformereservationevenements.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
