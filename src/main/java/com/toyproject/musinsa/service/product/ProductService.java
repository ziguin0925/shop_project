package com.toyproject.musinsa.service.product;

import com.toyproject.musinsa.entity.product.Product;
import com.toyproject.musinsa.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAll() {
        final List<Product> products = productRepository.findAll();
        return products;
    }
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product findByName(String name) {
        Optional<Product> product = productRepository.findByProductName(name);
        return product.orElse(null);
    }
}
