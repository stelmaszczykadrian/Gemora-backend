package com.gemora.product;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Optional<ProductDto> getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public List<ProductDto> getAllProducts(
            @RequestParam String sortBy) {
        return productService.getAllProducts(sortBy);
    }

    @PostMapping
    public void createProduct(
            @RequestBody ProductRequest productRequest) {
        productService.createProduct(productRequest);
    }

    @GetMapping("category/{category}")
    public List<ProductDto> getProductsByCategory(@PathVariable String category){
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/sorted")
    public List<ProductDto> getSortedProducts(
            @RequestParam("category") String category,
            @RequestParam("sort") String sortType) {
        return productService.getSortedProducts(category, sortType);
    }

    @GetMapping("/featured")
    public List<ProductDto> getFeaturedProducts(){
        return productService.getFeaturedProducts();
    }

    @DeleteMapping("/{id}")
    public void deleteProductById(@PathVariable int id){
        productService.deleteProductById(id);
    }


}
