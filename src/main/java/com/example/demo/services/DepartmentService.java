package com.example.demo.services;

import com.example.demo.entities.Department;
import com.example.demo.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(int id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Department not found"));
    }

    public void createDepartment(Department department) {
        departmentRepository.save(department);
    }

    public Department updateDepartment(int id, Department updatedDepartment) {
        Department department = getDepartmentById(id);

        department.setName(updatedDepartment.getName());

        return departmentRepository.save(department);
    }

    public void deleteDepartment(int id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }

    public void saveDepartment(Department department) {
        departmentRepository.save(department);
    }

    public boolean doesDepartmentExist(int departmentId) {
        return departmentRepository.existsById(departmentId);
    }
}