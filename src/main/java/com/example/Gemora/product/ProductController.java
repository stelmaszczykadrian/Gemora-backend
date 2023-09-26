package com.example.Gemora.product;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public List<Product> getAllProducts(
            @RequestParam String sortBy) {
        return productService.getAllProducts(sortBy);
    }

    @PostMapping
    public void createProduct(
            @RequestBody ProductRequest productRequest) throws IOException {

        Product product = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .description(productRequest.getDescription())
                .image(Base64.getDecoder().decode(productRequest.getImage()))
                .manufacturer(productRequest.getManufacturer())
                .postingDate(LocalDateTime.now())
                .build();

        productService.saveProduct(product);
    }

    @PutMapping("/{id}")
    public void updateProduct(@PathVariable int id, @RequestBody Product product) {
        // TODO: Implement logic to update product by id using ProductService

    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        // TODO: Implement logic to delete product by id using ProductService

    }

    @GetMapping("category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category){
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/sorted")
    public List<ProductDto> getSortedProducts(
            @RequestParam("category") String category,
            @RequestParam("sort") String sortType) {
        return productService.getSortedProducts(category, sortType);
    }

    @GetMapping("/featured")
    public List<Product> getFeaturedProducts(){
        return productService.getFeaturedProducts();
    }

}
