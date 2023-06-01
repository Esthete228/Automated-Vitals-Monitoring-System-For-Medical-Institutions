package com.example.demo.services;

import com.example.demo.entities.Patient;
import com.example.demo.entities.PatientHistory;
import com.example.demo.repositories.PatientHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientHistoryService {

    private final PatientHistoryRepository patientHistoryRepository;

    @Autowired
    public PatientHistoryService(PatientHistoryRepository patientHistoryRepository) {
        this.patientHistoryRepository = patientHistoryRepository;
    }

    public List<PatientHistory> getPatientHistoryByPatient(Patient patient) {
        return patientHistoryRepository.findByPatient(patient);
    }

    public void savePatientHistory(PatientHistory patientHistory) {
        patientHistoryRepository.save(patientHistory);
    }
}
