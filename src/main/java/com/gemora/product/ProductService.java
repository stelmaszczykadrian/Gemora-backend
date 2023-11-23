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

    public void deleteProductById(int id) {
        productRepository.deleteById(id);
    }

    public void updateProductById(int id, ProductDto product) {
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
}
