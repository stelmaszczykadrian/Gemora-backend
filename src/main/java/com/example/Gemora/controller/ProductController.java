package com.example.Gemora.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Gemora.model.Product;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        // TODO: Implement logic to retrieve product by id from ProductService
        return null;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        // TODO: Implement logic to retrieve all products from ProductService
        return null;
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody Product product) {
        // TODO: Implement logic to create a new product using ProductService
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable int id, @RequestBody Product product) {
        // TODO: Implement logic to update product by id using ProductService
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        // TODO: Implement logic to delete product by id using ProductService
        return null;
    }
}
