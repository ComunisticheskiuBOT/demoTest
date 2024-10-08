package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestQuality;
import org.example.demotest.entities.Employee;
import org.example.demotest.entities.Product;
import org.example.demotest.entities.Quality;
import org.example.demotest.repository.EmployeeRepository;
import org.example.demotest.repository.ProductRepository;
import org.example.demotest.repository.QualityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QualityService {
    private final QualityRepository qualityRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Quality> findAllQualities(){
        return qualityRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Quality createQuality(ServiceRequestQuality serviceRequestQuality) {
        try {
            return qualityRepository.save(Quality.builder()
                    .inspector(serviceRequestQuality.getInspector())
                    .product(serviceRequestQuality.getProduct())
                    .inspectionDate(serviceRequestQuality.getInspectionDate())
                    .result(serviceRequestQuality.getResult())
                    .comments(serviceRequestQuality.getComments())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании проверки качества", e);
        }
    }

    @Transactional(readOnly = true)
    public Quality findQualityById(Long quality_id) {
        return qualityRepository.findQualityByQualityId(quality_id);
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Инженер с ID '" + employeeId + "' не найден"));
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продукт с ID '" + productId + "' не найден"));
    }

    @Transactional(readOnly = true)
    public Optional<Quality> findByInspectionDate(Date inspectionDate) {
        return qualityRepository.findQualityByInspectionDate(inspectionDate);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuality(Long id) {
        qualityRepository.deleteById(id);
    }

    public boolean engineerExists(Long employeeId) {
        return employeeRepository.findById(employeeId).isPresent();
    }

    public boolean productExists(Long productId) {
        return productRepository.findById(productId).isPresent();
    }

    public Optional<Quality> updatedQuality(Long id, ServiceRequestQuality updatedQuality) {
        return qualityRepository.findById(id).map(quality -> {
            if (updatedQuality.getInspector() != null) {
                quality.setInspector(updatedQuality.getInspector());
            }
            if (updatedQuality.getProduct() != null) {
                quality.setProduct(updatedQuality.getProduct());
            }
            if (updatedQuality.getInspectionDate() != null) {
                quality.setInspectionDate(updatedQuality.getInspectionDate());
            }
            if (updatedQuality.getResult() != null) {
                quality.setResult(updatedQuality.getResult());
            }
            if (updatedQuality.getComments() != null) {
                quality.setComments(updatedQuality.getComments());
            }

            return qualityRepository.save(quality);
        });
    }
}
