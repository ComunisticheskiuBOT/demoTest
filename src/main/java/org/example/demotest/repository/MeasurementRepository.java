package org.example.demotest.repository;

import org.example.demotest.entities.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    Measurement findClientByMeasurementId(Long measurement_id);
    Measurement findByName(String name);
}
