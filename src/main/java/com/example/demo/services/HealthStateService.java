    package com.example.demo.services;

    import com.example.demo.entities.HealthState;
    import com.example.demo.entities.Patient;
    import com.example.demo.repositories.HealthStateRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    public class HealthStateService {
        private final HealthStateRepository healthStateRepository;

        @Autowired
        public HealthStateService(HealthStateRepository healthStateRepository) {
            this.healthStateRepository = healthStateRepository;
        }

        public HealthState getHealthStateByPatient(Patient patient) {
            return healthStateRepository.findByPatient(patient);
        }

        public void saveHealthState(HealthState healthState) {
            healthStateRepository.save(healthState);
        }

        public List<HealthState> getAllHealthStates() {
            return healthStateRepository.findAll();
        }
    }
