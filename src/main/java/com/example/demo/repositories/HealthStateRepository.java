package com.example.demo.repositories;

import com.example.demo.entities.HealthState;
import com.example.demo.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HealthStateRepository extends JpaRepository<HealthState, Long> {
    HealthState findByPatient(Patient patient);
}