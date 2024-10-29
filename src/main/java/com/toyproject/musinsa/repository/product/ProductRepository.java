package com.toyproject.musinsa.repository.product;

import com.toyproject.musinsa.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByProductName(String name);
    void deleteByProductName(String name);
    Optional<List<Product>> findTop9ByOrderByIdDesc();
}
