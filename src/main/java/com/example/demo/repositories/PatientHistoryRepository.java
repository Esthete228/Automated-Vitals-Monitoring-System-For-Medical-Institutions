package com.example.demo.repositories;

import com.example.demo.entities.Patient;
import com.example.demo.entities.PatientHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientHistoryRepository extends JpaRepository<PatientHistory, Integer> {
    List<PatientHistory> findByPatient(Patient patient);
}