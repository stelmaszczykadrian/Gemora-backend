package com.gemora.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<ProductDto> getProductById(int id) {
        Optional<Product> productOptional = productRepository.findById(id);

        return productOptional.map(this::mapProductToDto);
    }

    public List<ProductDto> getAllProducts(String sortType) {
        List<Product> products = productRepository.findAll();
        sortProducts(products, sortType);
        return products.stream()
                .map(this::mapProductToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProductDto> getProductsByCategory(String category) {
        ProductCategory categoryEnum = ProductCategory.from(category);

        List<Product> products = productRepository.findByCategory(categoryEnum.name());

        return products.stream()
                .map(this::mapProductToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProductDto> getSortedProducts(String category, String sortType) {
        ProductCategory categoryEnum = ProductCategory.from(category);

        List<Product> products = productRepository.findByCategory(categoryEnum.name());

        sortProducts(products, sortType);
        return products.stream()
                .map(this::mapProductToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProductDto> getFeaturedProducts() {
        ProductCategory category = ProductCategory.FEATURED;

        List<Product> products = productRepository.findByCategory(category.name());

        return products.stream()
                .map(this::mapProductToDto)
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

    private ProductDto mapProductToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .manufacturer(product.getManufacturer())
                .description(product.getDescription())
                .category(product.getCategory())
                .image(Base64.getEncoder().encodeToString(product.getImage()))
                .build();
    }

    public void createProduct(ProductRequest productRequest) {
        if (productRequest == null) {
            throw new IllegalArgumentException("Product request cannot be null");
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
}
