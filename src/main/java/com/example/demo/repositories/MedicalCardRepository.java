package com.example.demo.repositories;

import com.example.demo.entities.MedicalCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalCardRepository extends JpaRepository<MedicalCard, Integer> {
    MedicalCard findById(int id);
}