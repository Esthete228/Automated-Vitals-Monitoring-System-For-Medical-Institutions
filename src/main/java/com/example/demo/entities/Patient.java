package com.example.demo.entities;
import jakarta.persistence.*;
@Entity
@Table(name = "patient")
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private int ID;


    @Column(name = "first_name") private String firstName;

    @Column(name = "last_name") private String lastName;

    @Column(name = "Age") private int age;

    @Column(name = "Gender") private String gender;


    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MedicalCard medicalCard;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;


    // Геттери та сеттери
    public int getID() {
        return ID;
    }

    public void setID(int patientID) {
        this.ID = patientID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public MedicalCard getMedicalCard() {
        return medicalCard;
    }
    public void setMedicalCard(MedicalCard medicalCard) {
        this.medicalCard = medicalCard;
    }
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}