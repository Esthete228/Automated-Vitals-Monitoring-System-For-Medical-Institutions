package com.example.demo.entities;

import com.example.demo.repositories.HealthStateRepository;
import com.example.demo.repositories.PatientHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VitalsGenerator {
    private final HealthStateRepository healthStateRepository;
    private final PatientHistoryRepository patientHistoryRepository;
    private final Map<Integer, Timer> timers;

    @Autowired
    public VitalsGenerator(
            HealthStateRepository healthStateRepository, PatientHistoryRepository patientHistoryRepository) {
        this.healthStateRepository = healthStateRepository;
        this.patientHistoryRepository = patientHistoryRepository;
        this.timers = new HashMap<>();
    }

    public void startGeneratingDataForAllHealthStates() {
        List<HealthState> healthStates =
                healthStateRepository.findAll();
        for (HealthState healthState : healthStates) {
            startGeneratingData(healthState);
        }
    }

    public void startGeneratingData(HealthState healthState) {
        if (!timers.containsKey(healthState.getID())) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    generateAndSaveData(healthState);
                }
            };

            Timer timer = new Timer();
            // Schedule the task to run every 5 seconds
            timer.scheduleAtFixedRate(
                    task, 0, 5000); // Adjust the interval

            timers.put(healthState.getID(), timer);
        }
    }

    private void generateAndSaveData(
            HealthState healthState) {
        // Generate new data for the health state
        // Update the health state with the generated data
        // Save the updated health state to the database

        Random random = new Random();

// Middle values
        int middleHeartRate = 80;
        int middleSystolicBP = 120;
        int middleDiastolicBP = 80;
        double middleTemperature = 37.0;
        int middleOxygenSaturation = 95;

// Variability range
        int variabilityRange = 5;

// Generate values around the middle values with variability
        int heartRate = random.nextInt(variabilityRange * 2 + 1) + (middleHeartRate - variabilityRange);
        int systolicBP = random.nextInt(variabilityRange * 2 + 1) + (middleSystolicBP - variabilityRange);
        int diastolicBP = random.nextInt(variabilityRange * 2 + 1) + (middleDiastolicBP - variabilityRange);
        double temperature = random.nextDouble() * (variabilityRange * 2) + (middleTemperature - variabilityRange);
        int oxygenSaturation = random.nextInt(variabilityRange * 2 + 1) + (middleOxygenSaturation - variabilityRange);

        // Update the health state values
        healthState.setHeart_rate(heartRate);
        healthState.setSystolicBP(systolicBP);
        healthState.setDiastolicBP(diastolicBP);
        healthState.setTemperature(temperature);
        healthState.setOxygen_saturation(oxygenSaturation);

        // Save the updated health state to the database
        healthStateRepository.save(healthState);



        // Create a new patient history entry
        PatientHistory patientHistoryEntry = new PatientHistory();
        patientHistoryEntry.setPatient(healthState.getPatient());
        patientHistoryEntry.setHeart_rate(heartRate);
        patientHistoryEntry.setSystolicBP(systolicBP);
        patientHistoryEntry.setDiastolicBP(diastolicBP);
        patientHistoryEntry.setTemperature(temperature);
        patientHistoryEntry.setOxygen_Saturation(oxygenSaturation);
        patientHistoryEntry.setTimestamp(new Date());

        // Save the patient history entry to the database
        patientHistoryRepository.save(patientHistoryEntry);
    }
}