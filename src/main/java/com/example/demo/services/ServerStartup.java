package com.example.demo.services;

import com.example.demo.entities.VitalsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServerStartup {

    private final VitalsGenerator vitalsGenerator;

    @Autowired
    public ServerStartup(VitalsGenerator vitalsGenerator) {
        this.vitalsGenerator = vitalsGenerator;
    }

    @PostConstruct
    public void initialize() {
        vitalsGenerator.startGeneratingDataForAllHealthStates();
    }
}
