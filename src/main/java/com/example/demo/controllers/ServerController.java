package com.example.demo.controllers;

import com.example.demo.entities.*;
import com.example.demo.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ServerController {
    private final PatientService patientService;
    private final HealthStateService healthStateService;
    private final MedicalCardService medicalCardService;
    private final PatientHistoryService patientHistoryService;
    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final AssignmentService assignmentService;

    private final AppointmentService appointmentService;

    @Autowired
    public ServerController(PatientService patientService, HealthStateService healthStateService,
                            MedicalCardService medicalCardService, PatientHistoryService patientHistoryService,
                            DoctorService doctorService,
                            DepartmentService departmentService, PasswordEncoder passwordEncoder, AssignmentService assignmentService, AppointmentService appointmentService) {
        this.patientService = patientService;
        this.healthStateService = healthStateService;
        this.medicalCardService = medicalCardService;
        this.patientHistoryService = patientHistoryService;
        this.doctorService = doctorService;
        this.departmentService = departmentService;
        this.assignmentService = assignmentService;
        this.appointmentService = appointmentService;
    }

    @GetMapping(value = "/home")
    public String home() {
        return "home";
    }


    @GetMapping("/patients")
    public String patients(Model model) {
        model.addAttribute("patient", new Patient());
        return "createPatient";
    }
    @PostMapping("/patients")
    public ResponseEntity<String> registerPatient(@Validated @RequestBody Patient patient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("{\"message\": \"Validation failed\"}");
        }

        try {

            // Save the patient and the associated medical card
            patientService.savePatient(patient);

            return ResponseEntity.ok("{\"message\": \"Patient registered successfully\"}");
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Database error\"}");
        } catch (Exception e) {
            e.printStackTrace(); // Print the exception stack trace
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Unknown error\"}");
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Validated @RequestBody Doctor doctor, BindingResult bindingResult,
                                               HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Department department = (Department) session.getAttribute("department");
            if (department != null) {
                System.out.println("Department found in session: " + department.getName());
            } else {
                System.out.println("Department not found in session");
            }
        }

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body("{\"message\": \"Validation failed\"}");
        }

        try {
            doctorService.saveDoctor(doctor);
            return ResponseEntity.ok("{\"message\": \"Doctor registered successfully\"}");
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Database error\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Unknown error\"}");
        }
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
    public ResponseEntity<HealthState> getVitalsData(@PathVariable("id") int id) {
        Optional<Patient> optionalPatient = patientService.getPatientById(id);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            HealthState healthState = healthStateService.getHealthStateByPatient(patient);
            return ResponseEntity.ok(healthState);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/fetchPatients")
    @ResponseBody
    public List<Patient> fetchPatients(HttpServletRequest request) {
        // Get the logged-in doctor's department ID
        Doctor authenticatedDoctor = getAuthenticatedDoctor(request);
        int departmentId = authenticatedDoctor.getDepartment().getId();

        // Fetch patients based on the department ID

        return patientService.getPatientsByDepartmentId(departmentId);
    }

    @GetMapping("/fetchDoctors")
    @ResponseBody
    public List<Doctor> fetchDoctors(HttpServletRequest request) {
        Doctor authenticatedDoctor = getAuthenticatedDoctor(request);
        String position = authenticatedDoctor.getPosition();
        System.out.println("User position: " + position);

        List<Doctor> doctors;

        if ("receptionist".equalsIgnoreCase(position)) {
            // Fetch all doctors excluding those with the administrative department
            doctors = doctorService.getAllDoctors().stream()
                    .filter(doctor -> !isAdministrativeDepartment(doctor.getDepartment()))
                    .collect(Collectors.toList());

            System.out.println("Fetching all doctors for receptionist (excluding administrative department)");
        } else {
            int departmentId = authenticatedDoctor.getDepartment().getId();
            doctors = doctorService.getDoctorsByDepartmentId(departmentId);
            System.out.println("Fetching doctors for department: " + departmentId);
        }

        System.out.println("Fetched doctors: " + doctors);
        return doctors;
    }

    private boolean isAdministrativeDepartment(Department department) {
        // Define the ID of the administrative department
        int administrativeDepartmentId = 1; // Replace with the actual ID

        return department != null && department.getId() == administrativeDepartmentId;
    }


    @GetMapping("/fetchAssignedPatients")
    @ResponseBody
    public List<Patient> fetchAssignedPatients(HttpServletRequest request){
        // Get the logged-in doctor's department ID
        Doctor authenticatedDoctor = getAuthenticatedDoctor(request);
        int doctorId = authenticatedDoctor.getID();
        String position = authenticatedDoctor.getPosition();
        // Check if the doctor has a senior position
        if (position.equals("senior")) {
            // Fetch all patients from the department
            int departmentId = authenticatedDoctor.getDepartment().getId();
            return patientService.getPatientsByDepartmentId(departmentId);
        } else {
            // Fetch patients based on the doctor's ID from the assignment table
            return assignmentService.getAssignedPatientsByDoctorId(doctorId);
        }
    }


    private Doctor getAuthenticatedDoctor(HttpServletRequest request) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return doctorService.findByUsername(authenticatedUsername);
    }

    @GetMapping("/monitorPatient")
    public String monitorPatient() {
        return "monitorPatient";
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
    public String createDepartment(@RequestBody Department department, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "createDepartment";
        }

        departmentService.saveDepartment(department); // Assuming the method to save/update a department is named 'saveDepartment'
        return "redirect:/home";
    }

    @GetMapping("/departments/list")
    @ResponseBody
    public List<Department> getDepartmentList() {
        return departmentService.getAllDepartments();
    }


    @GetMapping("/assign")
    public String assignPatients(Model model) {
        List<Patient> patients = patientService.getAllPatients();
        List<Doctor> doctors = doctorService.getAllDoctors();
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("patients", patients);
        model.addAttribute("doctors", doctors);
        model.addAttribute("departments", departments);
        return "assignPatients";
    }

    @PostMapping("/assign")
    public String assignDoctorToPatient(@RequestParam("doctorId") int doctorId, @RequestParam("patientId") int patientId) {
        // Retrieve the doctor and patient objects
        Optional<Doctor> optionalDoctor = doctorService.getDoctorById(doctorId);
        Optional<Patient> optionalPatient = patientService.getPatientById(patientId);

        if (optionalDoctor.isPresent() && optionalPatient.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            Patient patient = optionalPatient.get();

            // Create a new assignment
            Assignment assignment = new Assignment();
            assignment.setDoctor(doctor);
            assignment.setPatient(patient);
            assignmentService.saveAssignment(assignment);

        } else {
            // Handle the case where either the doctor or patient is not found
            // Display an error message or redirect to an error page
            return "error";
        }

        // Redirect to a success page or the assign page
        return "redirect:/assign";
    }

    @GetMapping("/appointment")
    public String appointment(Model model) {
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        return "appointment";
    }

    @PostMapping("/bookAppointment")
    public ResponseEntity<String> bookAppointment(@RequestParam("doctorId") int doctorId,
                                                  @RequestParam("appointmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate) {
        try {
            // Retrieve the selected doctor
            Optional<Doctor> optionalDoctor = doctorService.getDoctorById(doctorId);
            if (optionalDoctor.isPresent()) {
                Doctor doctor = optionalDoctor.get();

                // Create an appointment
                Appointment appointment = new Appointment();
                appointment.setDoctor(doctor);
                appointment.setDate(Date.valueOf(appointmentDate));

                // Save the appointment
                appointmentService.saveAppointment(appointment);

                return ResponseEntity.ok("Appointment booked successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error booking appointment");
        }
    }

    @GetMapping("/doctorAppointments")
    public String doctorAppointments(Model model, HttpServletRequest request) {
        Doctor authenticatedDoctor = getAuthenticatedDoctor(request);
        int doctorID = authenticatedDoctor.getID();

        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctorID);

        model.addAttribute("appointments", appointments);
        return "doctorAppointments";
    }
}