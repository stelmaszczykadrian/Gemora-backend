package com.gemora.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<ProductDto> getProductById(int id) {
        Optional<Product> productOptional = productRepository.findById(id);

        return productOptional.map(ProductMapper::mapProductToDto);
    }

    public List<ProductDto> getAllProducts(String sortType) {
        List<Product> products = productRepository.findAll();
        sortProducts(products, sortType);
        return products.stream()
                .map(ProductMapper::mapProductToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProductDto> getProductsByCategory(String category) {
        ProductCategory categoryEnum = ProductCategory.from(category);

        List<Product> products = productRepository.findByCategory(categoryEnum.name());

        return products.stream()
                .map(ProductMapper::mapProductToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProductDto> getSortedProducts(String category, String sortType) {
        ProductCategory categoryEnum = ProductCategory.from(category);

        List<Product> products = productRepository.findByCategory(categoryEnum.name());

        sortProducts(products, sortType);
        return products.stream()
                .map(ProductMapper::mapProductToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProductDto> getFeaturedProducts() {
        ProductCategory category = ProductCategory.FEATURED;

        List<Product> products = productRepository.findByCategory(category.name());

        return products.stream()
                .map(ProductMapper::mapProductToDto)
                .collect(Collectors.toList());
    }

    public void sortProducts(List<Product> products, String sortType) {
        SortType enumSortType = SortType.from(sortType);

        switch (enumSortType) {
            case ASCENDING -> products.sort(Comparator.comparing(Product::getPrice));
            case DESCENDING -> products.sort(Comparator.comparing(Product::getPrice).reversed());
            case NEWEST -> products.sort(Comparator.comparing(Product::getPostingDate).reversed());
        }
    }

    public void createProduct(ProductRequest productRequest) {
        String productName = productRequest.getName();

        if (productExists(productName)) {
            log.error("Product already exists in the database.");
            throw new ProductAlreadyExistsException("Product already exists in the database.");
        }

        Product product = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .description(productRequest.getDescription())
                .image(Base64.getDecoder().decode(productRequest.getImage()))
                .manufacturer(productRequest.getManufacturer())
                .postingDate(LocalDateTime.now())
                .build();

        productRepository.save(product);
    }

    public void deleteProductById(int id) {
        if (!productExists(id)) {
            log.error("Product not exists in the database.");
            throw new ProductNotFoundException("Product not exists in the database.");
        }
        productRepository.deleteById(id);
    }

    public void updateProductById(int id, ProductRequest product) {
        if (!productExists(id)) {
            log.error("Product not exists in the database.");
            throw new ProductNotFoundException("Product not exists in the database.");
        }

        Optional<Product> productToUpdate = productRepository.findById(id);

        productToUpdate.ifPresent(p -> {
            p.setName(product.getName());
            p.setPrice(product.getPrice());
            p.setManufacturer(product.getManufacturer());
            p.setDescription(product.getDescription());
            p.setCategory(product.getCategory());
            p.setImage(Base64.getDecoder().decode(product.getImage()));

            productRepository.save(p);
        });

    }

    private boolean productExists(String name) {
        Optional<Product> existingProduct = productRepository.findProductByName(name);

        return existingProduct.isPresent();
    }

    private boolean productExists(Integer id) {
        Optional<Product> existingProduct = productRepository.findById(id);

        return existingProduct.isPresent();
    }

    @Transactional
    public List<ProductDto> getProductBySearchTerm(String searchTerm, String sortType) {
        List<Product> products = productRepository.findProductByNameContainingIgnoreCase(searchTerm);

        sortProducts(products, sortType);

        return products.stream()
                .map(ProductMapper::mapProductToDto)
                .collect(Collectors.toList());
    }
}
