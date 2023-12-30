package com.example.demo.services;

import com.example.demo.entities.Department;
import com.example.demo.entities.Doctor;
import com.example.demo.repositories.DoctorRepository;
import com.example.demo.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, DepartmentService departmentService, PasswordEncoder passwordEncoder, PatientRepository patientRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentService = departmentService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll(); // Fetch all doctors from the repository
    }

    public Doctor findByUsername(String username) {
        return doctorRepository.findByUsername(username);
    }


    public void saveDoctor(Doctor doctor) {
        Department department = doctor.getDepartment();
        if (department != null) {
            int departmentId = department.getId();
            Optional<Department> optionalDepartment = Optional.ofNullable(departmentService.getDepartmentById(departmentId));
            if (optionalDepartment.isPresent()) {
                Department existingDepartment = optionalDepartment.get();
                doctor.setDepartment(existingDepartment);
                String encryptedPassword = passwordEncoder.encode(doctor.getPassword()); // Encrypt the password
                doctor.setPassword(encryptedPassword); // Set the encrypted password
                doctorRepository.save(doctor);
            } else {
                throw new IllegalArgumentException("Department not found");
            }
        } else {
            throw new IllegalArgumentException("Department not set for the doctor");
        }
    }

    public Optional<Doctor> getDoctorById(int id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> getDoctorsByDepartmentId(int departmentId) {
        return doctorRepository.findByDepartmentId(departmentId);
    }
}