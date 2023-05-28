package com.example.demo.services;

import com.example.demo.entities.MedicalCard;
import com.example.demo.repositories.MedicalCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicalCardService {
    private final MedicalCardRepository medicalCardRepository;

    @Autowired
    public MedicalCardService(MedicalCardRepository medicalCardRepository) {
        this.medicalCardRepository = medicalCardRepository;
    }

    public void saveMedicalCard(MedicalCard medicalCard) {
        medicalCardRepository.save(medicalCard);
    }
}