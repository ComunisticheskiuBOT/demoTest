package org.example.demotest.services;

import org.example.demotest.entities.Product;
import org.example.demotest.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    public List<Product> findAll() { return productRepository.findAll(); }
    public Product addProduct(Product product){ return productRepository.save(product); }
}
