package com.gemora.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{
    List<Product> findByCategory(String category);
    Optional<Product> findProductByName(String name);
    List<Product> findProductByNameContainingIgnoreCase(String searchTerm);
}

