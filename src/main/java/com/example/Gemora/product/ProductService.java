package com.example.Gemora.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<ProductDto> getSortedProducts(String category, String sortType) {
        List<Product> products = productRepository.findByCategory(category);
        sortProducts(products, sortType);
        return products.stream().map(product ->
                        ProductDto.builder()
                                .name(product
                                        .getName())
                                .manufacturer(product.getManufacturer())
                                .price(product.getPrice())
                                .description(product.getDescription())
                                .category(product.getCategory())
                                .image(Base64.getEncoder().encodeToString(product.getImage()))
                                .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Product> getFeaturedProducts() {
        return productRepository.findByCategory("FEATURED");
    }

    private void sortProducts(List<Product> products, String sortType) {
        SortType enumSortType = SortType.from(sortType);

        switch (enumSortType) {
            case ASCENDING:
                products.sort(Comparator.comparing(Product::getPrice));
                break;
            case DESCENDING:
                products.sort(Comparator.comparing(Product::getPrice).reversed());
                break;
            case NEWEST:
                products.sort(Comparator.comparing(Product::getPostingDate).reversed());
                break;
            default:
                throw new IllegalArgumentException("Unsupported sort type: " + sortType);
        }
    }

}
