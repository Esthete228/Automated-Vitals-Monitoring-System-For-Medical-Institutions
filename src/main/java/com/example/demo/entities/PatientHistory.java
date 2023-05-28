package com.example.demo.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "patient_history")
public class PatientHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private int ID;
    @OneToOne @JoinColumn(name = "patientID") private Patient patient;

    @Column(name = "Heart_Rate") private int heart_rate;

    @Column(name = "SystolicBP") private int systolicBP;

    @Column(name = "DiastolicBP") private int diastolicBP;

    @Column(name = "Oxygen_Saturation") private int oxygen_saturation;

    @Column(name = "Temperature") private double temperature;

    @Column(name = "Timestamp") private java.util.Date timestamp;


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
        return heart_rate;
    }

    public void setHeart_rate(int heart_rate) {
        this.heart_rate = heart_rate;
    }

    public int getSystolicBP() {
        return systolicBP;
    }

    public void setSystolicBP(int systolicBP) {
        this.systolicBP = systolicBP;
    }

    public int getDiastolicBP() {
        return diastolicBP;
    }

    public void setDiastolicBP(int diastolicBP) {
        this.diastolicBP = diastolicBP;
    }

    public int getOxygen_saturation() {
        return oxygen_saturation;
    }

    public void setOxygen_saturation(int oxygen_saturation) {
        this.oxygen_saturation = oxygen_saturation;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
