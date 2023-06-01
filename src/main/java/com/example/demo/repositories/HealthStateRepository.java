package com.example.demo.repositories;

import com.example.demo.entities.HealthState;
import com.example.demo.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HealthStateRepository extends JpaRepository<HealthState, Integer> {
    HealthState findByPatient(Patient patient);
}