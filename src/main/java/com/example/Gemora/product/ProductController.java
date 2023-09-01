package com.example.Gemora.product;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("manufacturer") String manufacturer,
            @RequestParam("image") MultipartFile image,
            @RequestParam("description") String description,
            @RequestParam("category") String category) throws IOException {

        Product product = Product.builder()
                .name(name)
                .price(price)
                .category(category)
                .description(description)
                .image(image.getBytes())
                .manufacturer(manufacturer)
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
    public List<Product> getSortedProducts(
            @RequestParam("category") String category,
            @RequestParam("sort") String sortType) {
        return productService.getSortedProducts(category, sortType);
    }

    @GetMapping("/featured")
    public List<Product> getFeaturedProducts(){
        return productService.getFeaturedProducts();
    }

}
