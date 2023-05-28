package com.example.demo.controllers;

import com.example.demo.entities.*;
import com.example.demo.services.*;
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

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ServerController {
    private final PatientService patientService;
    private final HealthStateService healthStateService;
    private final MedicalCardService medicalCardService;
    private final PatientHistoryService patientHistoryService;
    private final DoctorService doctorService;
    private final VitalsGenerator vitalsGenerator;
    private final DepartmentService departmentService;

    @Autowired
    public ServerController(PatientService patientService, HealthStateService healthStateService,
                            MedicalCardService medicalCardService, PatientHistoryService patientHistoryService,
                            DoctorService doctorService, VitalsGenerator vitalsGenerator,
                            DepartmentService departmentService) {
        this.patientService = patientService;
        this.healthStateService = healthStateService;
        this.medicalCardService = medicalCardService;
        this.patientHistoryService = patientHistoryService;
        this.doctorService = doctorService;
        this.vitalsGenerator = vitalsGenerator;
        this.departmentService = departmentService;
    }

    @GetMapping(value = "/home")
    public String home() {
        return "home";
    }

    @PostMapping(value = "/patients", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createPatient(@ModelAttribute @Validated Patient patient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "error";
        }

        try {
            patientService.createPatient(patient);

            HealthState healthState = new HealthState();
            healthState.setPatient(patient);
            healthStateService.saveHealthState(healthState);

            vitalsGenerator.startGeneratingData(healthState);

            return "/success";
        } catch (Exception e) {
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
        Optional<Patient> optionalPatient = patientService.getPatientById(id);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            HealthState healthState = healthStateService.getHealthStateByPatient(patient);
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
        Optional<Patient> optionalPatient = patientService.getPatientById(id);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            return healthStateService.getHealthStateByPatient(patient);
        } else {
            return null;
        }
    }


    @GetMapping("/fetchPatients")
    @ResponseBody
    public List<Patient> fetchPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/monitorPatient")
    public String monitorPatient() {
        return "monitorPatient";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Doctor());
        return "register";
    }


    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> registerUser(@RequestBody @Validated Doctor doctor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("{\"message\": \"Validation error\"}");
        }

        try {
            doctorService.createDoctor(doctor);

            return ResponseEntity.ok("{\"message\": \"Doctor registered successfully\"}");
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Database error\"}");
        } catch (Exception e) {
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
        Optional<Patient> optionalPatient = patientService.getPatientById(patientId);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            List<PatientHistory> patientHistory = patientHistoryService.getPatientHistoryByPatient(patient);

            response.put("patient", patient);
            response.put("patientHistory", patientHistory);
        } else {
            response.put("patient", null);
            response.put("patientHistory", null);
        }
        return response;
    }

    @GetMapping("/patientConclusion")
    public String showPatientConclusion(Model model) {
        List<Patient> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        model.addAttribute("medicalCard", new MedicalCard());
        return "patientConclusion";
    }

    @GetMapping("/getConclusion/{patientId}")
    @ResponseBody
    public ResponseEntity<String> getConclusion(@PathVariable("patientId") int patientId) {
        Optional<Patient> optionalPatient = patientService.getPatientById(patientId);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            MedicalCard medicalCard = patient.getMedicalCard();
            if (medicalCard != null) {
                String conclusion = medicalCard.getConclusion();
                if (conclusion != null) {
                    return ResponseEntity.ok(conclusion);
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/saveConclusion/{patientId}")
    @ResponseBody
    public ResponseEntity<String> saveConclusion(@PathVariable("patientId") int patientId, @RequestBody Map<String, String> requestBody) {
        String conclusion = requestBody.get("conclusion");
        if (conclusion == null) {
            return ResponseEntity.badRequest().body("Conclusion not provided");
        }

        Optional<Patient> optionalPatient = patientService.getPatientById(patientId);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            MedicalCard medicalCard = patient.getMedicalCard();
            if (medicalCard == null) {
                medicalCard = new MedicalCard();
                medicalCard.setPatient(patient);
                patient.setMedicalCard(medicalCard);
            }
            medicalCard.setConclusion(conclusion);
            medicalCardService.saveMedicalCard(medicalCard);
            return ResponseEntity.ok("Conclusion saved successfully!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/departments")
    public String showDepartmentForm(Model model) {
        model.addAttribute("department", new Department());
        return "createDepartment";
    }

    @PostMapping("/departments")
    public String createDepartment(@Valid @ModelAttribute("department") Department department, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "createDepartment";
        }

        departmentService.saveDepartment(department); // Assuming the method to save/update a department is named 'saveDepartment'
        return "redirect:/departments";
    }
}
