package com.example.demo.repositories;

import com.example.demo.entities.Patient;
import com.example.demo.entities.PatientHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientHistoryRepository extends JpaRepository<PatientHistory, Long> {
    List<PatientHistory> findByPatient(Patient patient);
}