///*
//package com.example.demo;
//
//import com.example.demo.entities.HealthState;
//import com.example.demo.entities.PatientHistory;
//import com.example.demo.repositories.HealthStateRepository;
//import com.example.demo.repositories.PatientHistoryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//@Component
//public class MedicalEquipmentConnector implements MedicalEquipmentAPI {
//    private final HealthStateRepository healthStateRepository;
//    private final PatientHistoryRepository patientHistoryRepository;
//    private final Map<Integer, Timer> timers;
//    private final MedicalEquipmentAPI medicalEquipmentAPI;
//
//    @Autowired
//    public MedicalEquipmentConnector (
//            HealthStateRepository healthStateRepository, PatientHistoryRepository patientHistoryRepository,
//            MedicalEquipmentAPI medicalEquipmentAPI) {
//        this.healthStateRepository = healthStateRepository;
//        this.patientHistoryRepository = patientHistoryRepository;
//        this.medicalEquipmentAPI = medicalEquipmentAPI;
//        this.timers = new HashMap<>();
//    }
//
//    public void startFetchingDataForAllHealthStates() {
//        List<HealthState> healthStates = healthStateRepository.findAll();
//        for (HealthState healthState : healthStates) {
//            startFetchingData(healthState);
//        }
//    }
//
//    public void startFetchingData(HealthState healthState) {
//        if (!timers.containsKey(healthState.getID())) {
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
//                    fetchAndSaveData(healthState);
//                }
//            };
//
//            Timer timer = new Timer();
//            // Запланувати запуск завдання кожні 5 секунд
//            timer.scheduleAtFixedRate(task, 0, 5000);
//
//            timers.put(healthState.getID(), timer);
//        }
//    }
//
//    private void fetchAndSaveData(HealthState healthState) {
//        // Підключити до API медичного обладнання щоб отримати дані в реальному часі
//        String equipmentId = healthState.getPatient().getEquipmentId();
//        int heartRate = medicalEquipmentAPI.getHeartRate(equipmentId);
//        int systolicBP = medicalEquipmentAPI.getSystolicBloodPressure(equipmentId);
//        int diastolicBP = medicalEquipmentAPI.getDiastolicBloodPressure(equipmentId);
//        int oxygenSaturation = medicalEquipmentAPI.getOxygenSaturation(equipmentId);
//        double temperature = medicalEquipmentAPI.getTemperature(equipmentId);
//
//        // Оновити стан здоров'я отриманими даними
//        healthState.setHeart_rate(heartRate);
//        healthState.setSystolicBP(systolicBP);
//        healthState.setDiastolicBP(diastolicBP);
//        healthState.setOxygen_saturation(oxygenSaturation);
//        healthState.setTemperature(temperature);
//
//        // Зберегти оновлений стан здоров'я в базі даних
//        healthStateRepository.save(healthState);
//
//        // Створити новий запис в історії хвороби пацієнта
//        PatientHistory patientHistoryEntry = new PatientHistory();
//        patientHistoryEntry.setPatient(healthState.getPatient());
//        patientHistoryEntry.setHeart_rate(heartRate);
//        patientHistoryEntry.setSystolicBP(systolicBP);
//        patientHistoryEntry.setDiastolicBP(diastolicBP);
//        patientHistoryEntry.setOxygen_saturation(oxygenSaturation);
//        patientHistoryEntry.setTemperature(temperature);
//        patientHistoryEntry.setTimestamp(new Date());
//
//        // Зберегти запис історії хвороби пацієнта в базі даних
//        patientHistoryRepository.save(patientHistoryEntry);
//    }
//
//    */
///**
//     * @return
//     *//*
//
//    @Override
//    public String getEquipmentId() {
//        return null;
//    }
//
//    */
///**
//     * @return
//     *//*
//
//    @Override
//    public int getHeartRate() {
//        return 0;
//    }
//
//    */
///**
//     * @return
//     *//*
//
//    @Override
//    public int getSystolicBloodPressure() {
//        return 0;
//    }
//
//    */
///**
//     * @return
//     *//*
//
//    @Override
//    public int getDiastolicBloodPressure() {
//        return 0;
//    }
//
//    */
///**
//     * @return
//     *//*
//
//    @Override
//    public int getOxygenSaturation() {
//        return 0;
//    }
//
//    */
///**
//     * @return
//     *//*
//
//    @Override
//    public double getTemperature() {
//        return 0;
//    }
//}
//*/
