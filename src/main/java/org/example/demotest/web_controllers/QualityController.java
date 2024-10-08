package org.example.demotest.web_controllers;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestProduct;
import org.example.demotest.dto.ServiceRequestQuality;
import org.example.demotest.entities.Product;
import org.example.demotest.entities.Quality;
import org.example.demotest.services.ProductService;
import org.example.demotest.services.QualityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class QualityController {
    private final QualityService qualityService;

    @GetMapping("/quality-api/v1/qualities")
    public List<Quality> findAllProducts(){
        return qualityService.findAllQualities();
    }

    @GetMapping("/quality-api/v1/qualities/{id}")
    public Quality getQualityById(@PathVariable Long id) {
        return qualityService.findQualityById(id);
    }

    @GetMapping("/quality-api/v1/inspection-dates/qualities/{productName}")
    public ResponseEntity<Quality> getQualityByInspectionDate(@PathVariable Date inspectionDate) {
        return qualityService.findByInspectionDate(inspectionDate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/quality-api/v1/qualities")
    public ResponseEntity<Quality> processRequest(@RequestBody ServiceRequestQuality serviceRequestQuality) {
        return new ResponseEntity<>(qualityService.createQuality(serviceRequestQuality), HttpStatus.CREATED);
    }

    @DeleteMapping("/quality-api/v1/qualities/{id}")
    public ResponseEntity<Void> deleteQuality(@PathVariable Long id) {
        qualityService.deleteQuality(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/quality-api/v1/qualities/{id}")
    public ResponseEntity<Quality> updateQuality(@PathVariable Long id, @RequestBody ServiceRequestQuality updatedQuality) {
        return qualityService.updatedQuality(id, updatedQuality)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
