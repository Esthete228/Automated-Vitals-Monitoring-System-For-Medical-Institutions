package com.example.demo.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "patient_history")
public class PatientHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private int ID;
    @OneToOne @JoinColumn(name = "patientID") private Patient patient;

    @Column(name = "Heart_Rate") private int Heart_rate;

    @Column(name = "SystolicBP") private int SystolicBP;

    @Column(name = "DiastolicBP") private int DiastolicBP;

    @Column(name = "Oxygen_Saturation") private int Oxygen_Saturation;

    @Column(name = "Temperature") private double Temperature;

    @Column(name = "Timestamp") private java.util.Date Timestamp;


    // Геттери та сеттери


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public int getHeart_rate() {
        return Heart_rate;
    }

    public void setHeart_rate(int heart_rate) {
        Heart_rate = heart_rate;
    }

    public int getSystolicBP() {
        return SystolicBP;
    }

    public void setSystolicBP(int systolicBP) {
        SystolicBP = systolicBP;
    }

    public int getDiastolicBP() {
        return DiastolicBP;
    }

    public void setDiastolicBP(int diastolicBP) {
        DiastolicBP = diastolicBP;
    }

    public int getOxygen_Saturation() {
        return Oxygen_Saturation;
    }

    public void setOxygen_Saturation(int oxygen_Saturation) {
        Oxygen_Saturation = oxygen_Saturation;
    }

    public double getTemperature() {
        return Temperature;
    }

    public void setTemperature(double temperature) {
        Temperature = temperature;
    }

    public Date getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(Date timestamp) {
        Timestamp = timestamp;
    }
}
