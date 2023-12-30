package com.example.demo.repositories;

import com.example.demo.entities.Doctor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    Doctor findByUsername(String username);
    @NotNull List<Doctor> findAll();
    List<Doctor> findByDepartmentId(int departmentId);
}