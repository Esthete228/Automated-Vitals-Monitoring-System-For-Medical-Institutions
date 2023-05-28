package com.example.demo.repositories;

import com.example.demo.entities.Department;
import com.example.demo.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    @Query("SELECT p FROM Patient p WHERE p.department = :department")
    List<Patient> findAllByDepartment(@Param("department") Department department);

    Optional<Patient> findByID(int id);
}