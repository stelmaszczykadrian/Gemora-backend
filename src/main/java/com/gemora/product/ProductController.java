package com.gemora.product;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.gemora.validation.ValidationHelper.handleBindingResultErrors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable int id) {
        Optional<ProductDto> productDtoOptional = productService.getProductById(id);

        return productDtoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(@RequestParam String sortBy) {
        List<ProductDto> allProducts = productService.getAllProducts(sortBy);

        return allProducts.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(allProducts);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<String> createProduct(
            @Valid  @RequestBody ProductRequest productRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleBindingResultErrors(bindingResult);
        }

        try {
            productService.createProduct(productRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Product added successfully.");
        } catch (ProductAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @GetMapping("category/{category}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable String category){
        List<ProductDto> categoryProducts = productService.getProductsByCategory(category);

        return categoryProducts.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(categoryProducts);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<ProductDto>> getSortedProducts(
            @RequestParam("category") String category,
            @RequestParam("sort") String sortType) {
        List<ProductDto> sortedProducts = productService.getSortedProducts(category, sortType);

        return sortedProducts.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(sortedProducts);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductDto>> getFeaturedProducts(){
        List<ProductDto> featuredProducts = productService.getFeaturedProducts();

        return featuredProducts.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(featuredProducts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable int id){
        try {
            productService.deleteProductById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Product deleted successfully.");
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<String> updateProductById(
            @PathVariable int id,
            @Valid  @RequestBody ProductRequest productRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleBindingResultErrors(bindingResult);
        }

        try {
            productService.updateProductById(id, productRequest);
            return ResponseEntity.ok("Product updated successfully.");
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> getProductBySearchTerm(
            @RequestParam("searchTerm") String searchTerm,
            @RequestParam("sort") String sortType) {
        List<ProductDto> searchedProducts = productService.getProductBySearchTerm(searchTerm, sortType);

        return searchedProducts.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(searchedProducts);
    }

}
