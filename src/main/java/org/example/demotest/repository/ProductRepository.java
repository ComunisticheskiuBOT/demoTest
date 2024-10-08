package org.example.demotest.repository;

import org.example.demotest.entities.Product;
import org.example.demotest.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findProductByProductId(Long project_id);
    Optional<Product> findProductByProductName(String productName);
}
