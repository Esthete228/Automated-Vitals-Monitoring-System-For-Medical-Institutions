package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "doctor")
public class Doctor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private int ID;

    @Column(name = "username") private String username;

    @Column(name = "password") private String password;

    @Column(name = "position") private String position;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;


    // Геттери та сеттери
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}