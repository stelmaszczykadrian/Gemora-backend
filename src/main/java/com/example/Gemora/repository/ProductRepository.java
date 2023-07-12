package com.example.Gemora.repository;

import com.example.Gemora.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Integer>{
}

