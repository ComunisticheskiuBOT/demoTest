package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestProduct;
import org.example.demotest.dto.ServiceRequestProject;
import org.example.demotest.entities.Product;
import org.example.demotest.entities.Project;
import org.example.demotest.repository.EmployeeRepository;
import org.example.demotest.repository.ProductRepository;
import org.example.demotest.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<Product> findAllProducts(){
        return productRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Product createProduct(ServiceRequestProduct serviceRequestProduct) {
        try {
            return productRepository.save(Product.builder()
                    .project(serviceRequestProduct.getProject())
                    .productName(serviceRequestProduct.getProductName())
                    .productType(serviceRequestProduct.getProductType())
                    .quantity(serviceRequestProduct.getQuantity())
                    .weight(serviceRequestProduct.getWeight())
                    .cost(serviceRequestProduct.getCost())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании продукта", e);
        }
    }

    @Transactional(readOnly = true)
    public Product findProductById(Long product_id) {
        return productRepository.findProductByProductId(product_id);
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Проект с ID '" + projectId + "' не найден"));
    }

    @Transactional(readOnly = true)
    public Optional<Product> findByProductName(String productName) {
        return productRepository.findProductByProductName(productName);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    public boolean projectExists(Long projectId) {
        return projectRepository.findById(projectId).isPresent();
    }

    public Optional<Product> updatedProduct(Long id, ServiceRequestProduct updatedProduct) {
        return productRepository.findById(id).map(product -> {
            if (updatedProduct.getProductName() != null) {
                product.setProductName(updatedProduct.getProductName());
            }
            if (updatedProduct.getProductType() != null) {
                product.setProductType(updatedProduct.getProductType());
            }
            if (updatedProduct.getCost() != null) {
                product.setCost(updatedProduct.getCost());
            }
            if (updatedProduct.getWeight() != null) {
                product.setWeight(updatedProduct.getWeight());
            }
            if (updatedProduct.getProject() != null) {
                product.setProject(updatedProduct.getProject());
            }
            if (updatedProduct.getQuantity() != null) {
                product.setQuantity(updatedProduct.getQuantity());
            }
            return productRepository.save(product);
        });
    }
}
