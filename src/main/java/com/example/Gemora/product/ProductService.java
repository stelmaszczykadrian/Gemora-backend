package com.example.Gemora.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts(String sortType) {
        List<Product> products = productRepository.findAll();
        sortProducts(products, sortType);
        return products;

    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public void updateProduct(Product product) {
        productRepository.save(product);
    }

    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    @Transactional
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Transactional
    public List<Product> getSortedProducts(String category, String sortType) {
        List<Product> products = productRepository.findByCategory(category);
        sortProducts(products, sortType);
        return products;
    }

    @Transactional
    public List<Product> getFeaturedProducts() {
        return productRepository.findByCategory("FEATURED");
    }

    private void sortProducts(List<Product> products, String sortType) {
        if ("ascending".equalsIgnoreCase(sortType)) {
            products.sort(Comparator.comparing(Product::getPrice));
        } else if ("descending".equalsIgnoreCase(sortType)) {
            products.sort(Comparator.comparing(Product::getPrice).reversed());
        } else if ("newest".equalsIgnoreCase(sortType)) {
            products.sort(Comparator.comparing(Product::getPostingDate).reversed());
        }
    }
}
