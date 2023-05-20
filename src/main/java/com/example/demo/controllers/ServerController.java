package com.example.demo.controllers;

import com.example.demo.entities.*;
import com.example.demo.repositories.HealthStateRepository;
import com.example.demo.repositories.PatientHistoryRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ServerController {
    private final PatientRepository patientRepository;
    private final HealthStateRepository healthStateRepository;
    private final VitalsGenerator vitalsGenerator;

    private final UserRepository userRepository;

    private final PatientHistoryRepository patientHistoryRepository;

    @Autowired
    public ServerController(VitalsGenerator vitalsGenerator, PatientRepository patientRepository,
                            HealthStateRepository healthStateRepository, UserRepository userRepository, PatientHistoryRepository patientHistoryRepository) {

        this.patientRepository = patientRepository;
        this.healthStateRepository = healthStateRepository;
        this.vitalsGenerator = vitalsGenerator;
        this.userRepository = userRepository;
        this.patientHistoryRepository = patientHistoryRepository;
    }


    @GetMapping(value = "/home")
    public String home() {
        return "home";
    }

    @PostMapping(value = "/patients", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createPatient(@ModelAttribute @Validated Patient patient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Виникли помилки перевірки
            return "error";
        }

        try {
            // Збереження пацієнта
            patientRepository.save(patient);

            // Створення стану здоров'я для пацієнта
            HealthState healthState = new HealthState();
            healthState.setPatient(patient);
            healthStateRepository.save(healthState);
            // Почати генерування даних про стан здоров'я
            vitalsGenerator.startGeneratingData(healthState);

            return "/success";
        } catch (DataAccessException e) {
            // Обробка виключень, пов'язаних з базою даних
            return "error";
        } catch (Exception e) {
            // Обробка інших виключень
            return "error";
        }
    }

    @GetMapping("/patients")
    public String patients(Model model) {
        model.addAttribute("patient", new Patient());
        return "createPatient";
    }

    @GetMapping("/monitorPatient/{id}")
    public String monitorPatient(@PathVariable("id") int id, Model model) {
        Optional<Patient> optionalPatient = patientRepository.findById(id);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            HealthState healthState = healthStateRepository.findByPatient(patient);
            model.addAttribute("patient", patient);
            model.addAttribute("healthState", healthState);
        } else {
            model.addAttribute("patient", null);
        }
        return "monitorPatient";
    }

    @GetMapping("/monitorPatient/{id}/vitals")
    @ResponseBody
    public HealthState getVitalsData(@PathVariable("id") int id) {
        Optional<Patient> optionalPatient = patientRepository.findById(id);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            return healthStateRepository.findByPatient(patient);
        } else {
            return null;
        }
    }

    @GetMapping("/fetchPatients")
    @ResponseBody
    public List<Patient> fetchPatients() {
        return patientRepository.findAll();
    }

    @GetMapping("/monitorPatient")
    public String monitorPatient() {
        return "monitorPatient";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> registerUser(@RequestBody @Validated User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Виникли помилки валідації
            return ResponseEntity.badRequest().body("{\"message\": \"Validation error\"}");
        }

        try {
            // Зберегти користувача в базі даних
            userRepository.save(user);

            return ResponseEntity.ok("{\"message\": \"User registered successfully\"}");
        } catch (DataAccessException e) {
            // Обробка виключень, пов'язаних з базою даних
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Database error\"}");
        } catch (Exception e) {
            // Обробка інших виключень
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Unknown error\"}");
        }
    }

    @GetMapping("/patientLog")
    public String patientLog() {
        return "patientLog";
    }

    @GetMapping(value = "/patient_history/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> showPatientHistory(@PathVariable("id") int patientId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            // Отримуємо історію пацієнта на основі ідентифікатора пацієнта
            List<PatientHistory> patientHistory = patientHistoryRepository.findByPatient(patient);

            response.put("patient", patient);
            response.put("patientHistory", patientHistory);
        } else {
            response.put("patient", null);
            response.put("patientHistory", null);
        }
        return response;
    }
}