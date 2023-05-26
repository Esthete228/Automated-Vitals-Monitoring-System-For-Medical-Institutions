package com.example.demo.services;

import com.example.demo.entities.HealthState;
import com.example.demo.entities.PatientHistory;
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
            // запланування запуску завдання кожні 5 секунд
            timer.scheduleAtFixedRate(
                    task, 0, 5000); // Налаштування інтервалу

            timers.put(healthState.getID(), timer);
        }
    }

    private void generateAndSaveData(
            HealthState healthState) {
        // Генерація нових даних для стану здоров'я
        // Оновлення стану здоров'я згенерованими даними
        // Збереження оновленого стану здоров'я в базі даних

        Random random = new Random();

        // Середні значення
        int middleHeartRate = 80;
        int middleSystolicBP = 120;
        int middleDiastolicBP = 80;
        double middleTemperature = 37.0;
        int middleOxygenSaturation = 95;

        // Діапазон мінливості
        int variabilityRange = 5;

        // Генерація значення навколо середніх значень з мінливістю
        int heartRate = random.nextInt(variabilityRange * 2 + 1) + (middleHeartRate - variabilityRange);
        int systolicBP = random.nextInt(variabilityRange * 2 + 1) + (middleSystolicBP - variabilityRange);
        int diastolicBP = random.nextInt(variabilityRange * 2 + 1) + (middleDiastolicBP - variabilityRange);
        double temperature = random.nextDouble() * (variabilityRange * 2) + (middleTemperature - variabilityRange);
        int oxygenSaturation = random.nextInt(variabilityRange * 2 + 1) + (middleOxygenSaturation - variabilityRange);

        // Оновлення значення стану здоров'я
        healthState.setHeart_rate(heartRate);
        healthState.setSystolicBP(systolicBP);
        healthState.setDiastolicBP(diastolicBP);
        healthState.setTemperature(temperature);
        healthState.setOxygen_saturation(oxygenSaturation);

        // Збереження оновленого стану здоров'я в базі даних
        healthStateRepository.save(healthState);



        // Створрення нового запису в історії хвороби
        PatientHistory patientHistoryEntry = new PatientHistory();
        patientHistoryEntry.setPatient(healthState.getPatient());
        patientHistoryEntry.setHeart_rate(heartRate);
        patientHistoryEntry.setSystolicBP(systolicBP);
        patientHistoryEntry.setDiastolicBP(diastolicBP);
        patientHistoryEntry.setTemperature(temperature);
        patientHistoryEntry.setOxygen_saturation(oxygenSaturation);
        patientHistoryEntry.setTimestamp(new Date());

        // Збереження історії хвороби пацієнта в базі даних
        patientHistoryRepository.save(patientHistoryEntry);
    }
}