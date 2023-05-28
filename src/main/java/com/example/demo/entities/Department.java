package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;


    // Constructors, getters, and setters

    public Department(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Department() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}