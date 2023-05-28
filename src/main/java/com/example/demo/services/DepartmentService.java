package com.example.demo.services;

import com.example.demo.entities.Department;
import com.example.demo.entities.Doctor;
import com.example.demo.repositories.DepartmentRepository;
import com.example.demo.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, DoctorRepository doctorRepository) {
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
    }

    public Department getDepartmentByDoctorId(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor != null) {
            return departmentRepository.findById(doctor.getDepartment().getId()).orElse(null);
        }
        return null;
    }

    public void saveDepartment(Department department) {
        departmentRepository.save(department);
    }
}
