package org.example.demotest.web_controllers;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestClient;
import org.example.demotest.dto.ServiceRequestMeasurement;
import org.example.demotest.entities.Client;
import org.example.demotest.entities.Measurement;
import org.example.demotest.services.ClientService;
import org.example.demotest.services.MeasurementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MeasurementController {
    private final MeasurementService measurementService;

    @GetMapping("/measurement-api/v1/measurements")
    public List<Measurement> findAllClients(){
        return measurementService.findAllMeasurements();
    }

    @GetMapping("/measurement-api/v1/measurements/{id}")
    public Measurement getMeasurementById(@PathVariable Long id) {
        return measurementService.findMeasurementById(id);
    }

    @GetMapping("/measurement-api/v1/measurements/{name}")
    public Measurement getMeasurementByPhoneNumber(@PathVariable String name) {
        return measurementService.findMeasurementByName(name);
    }

    @PostMapping("/measurement-api/v1/measurements")
    public ResponseEntity<Measurement> processRequest(@RequestBody ServiceRequestMeasurement serviceRequestMeasurement) {
        return new ResponseEntity<>(measurementService.createMeasurement(serviceRequestMeasurement), HttpStatus.CREATED);
    }

    @DeleteMapping("/measurement-api/v1/measurements/{id}")
    public ResponseEntity<Void> deleteMeasurement(@PathVariable Long id) {
        measurementService.deleteMeasurement(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/measurement-api/v1/measurements/{id}")
    public ResponseEntity<Measurement> updateMeasurement(@PathVariable Long id, @RequestBody ServiceRequestMeasurement updatedMeasurement) {
        return measurementService.updatedMeasurement(id, updatedMeasurement)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
