package com.example.demo.services;

import com.example.demo.entities.HealthState;
import com.example.demo.entities.Patient;
import com.example.demo.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final HealthStateService healthStateService;
    private final VitalsGenerator vitalsGenerator;
    private final DepartmentService departmentService;

    @Autowired
    public PatientService(PatientRepository patientRepository, HealthStateService healthStateService, VitalsGenerator vitalsGenerator, DepartmentService departmentService) {
        this.patientRepository = patientRepository;
        this.healthStateService = healthStateService;
        this.vitalsGenerator = vitalsGenerator;
        this.departmentService = departmentService;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<Patient> getPatientsByDepartmentId(int departmentId) {
        return patientRepository.findByDepartmentId(departmentId);
    }
    public Optional<Patient> getPatientById(int id) {
        return patientRepository.findById(id);
    }

    public boolean validatePatient(Patient patient) {
        // Check if required fields are filled
        if (patient.getDepartment() == null || patient.getDepartment().getId() <= 0) {
            return false; // Return false if any required field is empty or invalid
        }

        // Check if the department exists
        int departmentId = patient.getDepartment().getId();
        boolean departmentExists = departmentService.doesDepartmentExist(departmentId);

        // Check if the ID already exists
        boolean patientExists = patientRepository.existsById(patient.getID());

        return departmentExists && !patientExists;
        // Return false if the department doesn't exist or if the ID already exists
        // Return true if all validation checks pass
    }

    public void createPatient(Patient patient) {
        // Save the patient
        Patient createdPatient = patientRepository.save(patient);

        // Create a health state for the patient
        HealthState healthState = new HealthState();
        healthState.setPatient(createdPatient);

        // Save the health state
        healthStateService.saveHealthState(healthState);

        // Start generating and updating data for the health state
        vitalsGenerator.startGeneratingData(healthState);
    }

    public void savePatient(Patient patient) {
        if (validatePatient(patient)) {
            // Save the patient
            createPatient(patient);
        } else {
            throw new IllegalArgumentException("Invalid patient data or ID already taken.");
        }
    }
}
