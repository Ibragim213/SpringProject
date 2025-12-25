package com.example.LibraryServer.service;

import com.example.LibraryServer.model.Product;
import com.example.LibraryServer.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.info("Fetching all products from database");
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findByIsAvailableTrue();
    }
}