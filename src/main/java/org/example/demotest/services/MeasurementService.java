package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestMeasurement;
import org.example.demotest.entities.Measurement;
import org.example.demotest.repository.MeasurementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeasurementService {
    private final MeasurementRepository measurementRepository;

    @Transactional(readOnly = true)
    public List<Measurement> findAllMeasurements(){
        return measurementRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Measurement createMeasurement(ServiceRequestMeasurement serviceRequestMeasurement) {
        try {
            return measurementRepository.save(Measurement.builder()
                    .name(serviceRequestMeasurement.getName())
                    .description(serviceRequestMeasurement.getDescription())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании единицы измерения", e);
        }
    }

    @Transactional(readOnly = true)
    public Measurement findMeasurementById(Long measurement_id) {
        return measurementRepository.findClientByMeasurementId(measurement_id);
    }


    @Transactional(readOnly = true)
    public Measurement findMeasurementByName(String name) {
        return measurementRepository.findByName(name);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteMeasurement(Long id) {
        measurementRepository.deleteById(id);
    }

    public Optional<Measurement> updatedMeasurement(Long id, ServiceRequestMeasurement updatedMeasurement) {
        return measurementRepository.findById(id).map(measurement -> {
            if (updatedMeasurement.getName() != null) {
                measurement.setName(updatedMeasurement.getName());
            }
            if (updatedMeasurement.getDescription() != null) {
                measurement.setDescription(updatedMeasurement.getDescription());
            }

            return measurementRepository.save(measurement);
        });
    }
}
