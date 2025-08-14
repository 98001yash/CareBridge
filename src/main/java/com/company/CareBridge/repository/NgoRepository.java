package com.company.CareBridge.repository;

import com.company.CareBridge.entity.Ngo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NgoRepository extends JpaRepository<Ngo,Long> {

    Optional<Ngo> findByEmail(String email);

    boolean existsByEmail(String email);
}
