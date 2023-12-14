package com.Gemora.e2e.product;

import com.gemora.product.Product;
import com.gemora.product.ProductDto;
import com.gemora.product.ProductRepository;
import com.gemora.product.ProductRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static com.Gemora.unit.product.ProductTestHelper.*;

import java.util.*;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void getProductById_ReturnsProductDto_ExistingProductId() {
        //given
        initializeProductData();

        int productId = 1;

        String baseUrl = "http://localhost:" + port + "/api/products/" + productId;

        //when
        ResponseEntity<ProductDto> response = restTemplate.getForEntity(baseUrl, ProductDto.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ProductDto productDto = response.getBody();
        assertEquals(productId, productDto.getId());
    }

    @Test
    public void getProductById_ReturnsNotFoundStatus_NonExistentProductId() {
        //given
        int nonExistentProductId = 999;

        String baseUrl = "http://localhost:" + port + "/api/products/" + nonExistentProductId;

        //when
        ResponseEntity<ProductDto> response = restTemplate.getForEntity(baseUrl, ProductDto.class);

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void getAllProducts_ReturnsOkStatusWithProducts_ProductsExist() {
        //given
        initializeProductData();

        String sortBy = "ascending";

        String baseUrl = "http://localhost:" + port + "/api/products?sortBy=" + sortBy;

        //when
        ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<ProductDto> actualProducts = response.getBody();
        assertEquals(3, actualProducts.size());
    }

    @Test
    void createProduct_ReturnsCreatedStatus_ValidProduct() {
        //given
        ProductRequest productRequest = createProductRequest();

        String baseUrl = "http://localhost:" + port + "/api/products";

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, productRequest, String.class);

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product added successfully.", response.getBody());
    }

    @Test
    void createProduct_ReturnsConflictStatus_ProductAlreadyExists() {
        //given
        ProductRequest productRequest = createProductRequest();

        String baseUrl = "http://localhost:" + port + "/api/products";

        //when
        ResponseEntity<String> response1 = restTemplate.postForEntity(baseUrl, productRequest, String.class);
        ResponseEntity<String> response2 = restTemplate.postForEntity(baseUrl, productRequest, String.class);

        //then
        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals(HttpStatus.CONFLICT, response2.getStatusCode());

        assertTrue(Objects.requireNonNull(response1.getBody()).contains("Product added successfully"));
        assertTrue(Objects.requireNonNull(response2.getBody()).contains("Product already exists in the database."));
    }

    @Test
    void getProductsByCategory_ReturnsOkStatusWithProducts_ValidCategory() {
        //given
        initializeProductData();

        String category = "RINGS";

        String baseUrl = "http://localhost:" + port + "/api/products/category/" + category;

        //when
        ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ProductDto> products = response.getBody();
        assertEquals(3, products.size());
    }

    @Test
    void getSortedProducts_ReturnsOkStatusWithSortedProducts_ValidCategoryAndSortType() {
        //given
        initializeProductData();

        String category = "RINGS";
        String sortType = "ascending";

        String baseUrl = "http://localhost:" + port + "/api/products/sorted?category=" + category + "&sort=" + sortType;

        // when
        ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ProductDto> sortedProducts = response.getBody();
        assertEquals(3, sortedProducts.size());

        assertTrue(sortedProducts.get(0).getPrice() <= sortedProducts.get(1).getPrice());
        assertTrue(sortedProducts.get(1).getPrice() <= sortedProducts.get(2).getPrice());
    }

    private void initializeProductData() {
        Product product1 = createProduct(1, "Product 1", 20, "RINGS", null);
        Product product2 = createProduct(2, "Product 2", 200, "RINGS", null);
        Product product3 = createProduct(3, "Product 3", 100, "RINGS", null);

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
    }
}
