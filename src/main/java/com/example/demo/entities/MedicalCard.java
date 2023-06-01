package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_card")
public class MedicalCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "conclusion", length = 120)
    private String conclusion;

    @OneToOne
    @JoinColumn(name = "patientID")
    private Patient patient;

    // Constructors, getters, and setters

    public MedicalCard() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}