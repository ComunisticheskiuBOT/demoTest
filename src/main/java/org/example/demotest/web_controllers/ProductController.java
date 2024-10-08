package org.example.demotest.web_controllers;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestProduct;
import org.example.demotest.entities.Product;
import org.example.demotest.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/product-api/v1/products")
    public List<Product> findAllProducts(){
        return productService.findAllProducts();
    }

    @GetMapping("/product-api/v1/products/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.findProductById(id);
    }

    @GetMapping("/product-api/v1/clients/products/{productName}")
    public ResponseEntity<Product> getProductByProjectName(@PathVariable String projectName) {
        return productService.findByProductName(projectName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/product-api/v1/products")
    public ResponseEntity<Product> processRequest(@RequestBody ServiceRequestProduct serviceRequestProduct) {
        return new ResponseEntity<>(productService.createProduct(serviceRequestProduct), HttpStatus.CREATED);
    }

    @DeleteMapping("/product-api/v1/products/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/product-api/v1/products/{id}")
    public ResponseEntity<Product> updateProject(@PathVariable Long id, @RequestBody ServiceRequestProduct updatedProduct) {
        return productService.updatedProduct(id, updatedProduct)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
