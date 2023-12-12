package com.Gemora.unit.product;

import com.gemora.GemoraApplication;
import com.gemora.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.*;

import static com.Gemora.unit.TestUtils.getBindingResult;
import static com.Gemora.unit.product.ProductTestHelper.createProductDto;
import static com.Gemora.unit.product.ProductTestHelper.createProductRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class ProductControllerTest {
    private ProductController productController;

    @Mock
    private ProductService productService;

    @BeforeEach
    void init() {
        productController = new ProductController(productService);
    }

    @Test
    void getProductById_ReturnsProductDto_ProductExists() {
        //given
        String category = "PENDANTS";
        int productId = 10;

        ProductDto expectedProduct = createProductDto(productId, "Product name 1", 100, category);
        when(productService.getProductById(productId)).thenReturn(Optional.of(expectedProduct));

        //when
        ResponseEntity<ProductDto> response = productController.getProductById(productId);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedProduct);
    }

    @Test
    void getProductById_ReturnsNotFoundStatus_ProductNotExist() {
        //given
        int productId = 999;

        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        //when
        ResponseEntity<ProductDto> response = productController.getProductById(productId);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @ParameterizedTest
    @EnumSource(SortType.class)
    void getAllProducts_ReturnExpectedList_ForAnySortByOptions(SortType sortType) {
        //given
        String category = "RINGS";
        String sortTypeValue = String.valueOf(sortType);

        ProductDto productDto1 = createProductDto(1, "Product name 1", 100, category);
        ProductDto productDto2 = createProductDto(2, "Product name 2", 200, category);

        List<ProductDto> expectedProducts = List.of(productDto1,productDto2);

        when(productService.getAllProducts(sortTypeValue)).thenReturn(expectedProducts);

        //when
        ResponseEntity<List<ProductDto>> response = productController.getAllProducts(sortTypeValue);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);

        List<ProductDto> allProducts = response.getBody();
        assertThat(allProducts).hasSize(2);
        assertThat(allProducts).containsExactlyElementsOf(expectedProducts);

    }

    @Test
    void getAllProducts_ReturnsNotFoundStatus_ForEmptyList() {
        //given
        String sortType= "ascending";

        when(productService.getAllProducts(sortType)).thenReturn(Collections.emptyList());

        //when
        ResponseEntity<List<ProductDto>> response = productController.getAllProducts(sortType);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void createProduct_ValidProductRequest_CreateProductWasCalledOnce() {
        //given
        ProductRequest productRequest = createProductRequest();

        BindingResult bindingResult = getBindingResult(false);

        //when
        ResponseEntity<String> response = productController.createProduct(productRequest, bindingResult);

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Product added successfully.", response.getBody());
        verify(productService, times(1)).createProduct(productRequest);
    }

    @Test
    void createProduct_ReturnsBadRequest_WhenBindingErrors() {
        //given
        ProductRequest productRequest = createProductRequest();

        BindingResult bindingResult = getBindingResult(true);

        //when
        ResponseEntity<String> response = productController.createProduct(productRequest, bindingResult);

        //then
        verifyNoInteractions(productService);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createProduct_ReturnsConflict_ThrownProductAlreadyExistsException() {
        //given
        ProductRequest productRequest = createProductRequest();

        BindingResult bindingResult = getBindingResult(false);

        String expectedErrorMessage = "Product with name Product name already exists";

        doThrow(new ProductAlreadyExistsException(expectedErrorMessage))
                .when(productService)
                .createProduct(any(ProductRequest.class));

        //when
        ResponseEntity<String> response = productController.createProduct(productRequest, bindingResult);

        //then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody());
    }

    @ParameterizedTest
    @EnumSource(ProductCategory.class)
    void getProductsByCategory_ReturnExpectedProductList_ForValidCategory(ProductCategory productCategory) {
        //given
        String productCategoryValue = String.valueOf(productCategory);

        ProductDto productDto1 = createProductDto(1, "Product name 1", 100, productCategoryValue);
        ProductDto productDto2 = createProductDto(2, "Product name 2", 200, productCategoryValue);

        List<ProductDto> expectedProductDtos = List.of(productDto1, productDto2);

        when(productService.getProductsByCategory(productCategoryValue)).thenReturn(expectedProductDtos);

        //when
        ResponseEntity<List<ProductDto>> response = productController.getProductsByCategory(productCategoryValue);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProductDtos, response.getBody());
    }

    @Test
    void getProductsByCategory_ReturnsNotFound_InvalidCategory() {
        //given
        String invalidCategoryName = "InvalidCategory";
        when(productService.getProductsByCategory(invalidCategoryName)).thenReturn(List.of());

        //when
        ResponseEntity<List<ProductDto>> response = productController.getProductsByCategory(invalidCategoryName);

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @ParameterizedTest
    @EnumSource(SortType.class)
    void getSortedProducts_ReturnExpectedProductList_ValidSortTypeAndCategory(SortType sortType) {
        //given
        String category = "BRACELETS";
        String sortTypeValue = String.valueOf(sortType);

        ProductDto productDto1 = createProductDto(1, "Product name 1", 100, category);
        ProductDto productDto2 = createProductDto(2, "Product name 2", 200, category);

        List<ProductDto> expectedProductDtos = List.of(productDto1, productDto2);

        when(productService.getSortedProducts(category, sortTypeValue)).thenReturn(expectedProductDtos);

        //when
        ResponseEntity<List<ProductDto>> response = productController.getSortedProducts(category, sortTypeValue);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProductDtos, response.getBody());
        assertThat(expectedProductDtos.get(0).getImage()).isNotBlank();
        assertThat(expectedProductDtos.get(1).getImage()).isNotBlank();
    }

    @Test
    void getSortedProducts_ReturnsNotFound_InvalidSortType() {
        //given
        String category = "BRACELETS";
        String invalidSortType = "InvalidSortType";

        when(productService.getSortedProducts(category, invalidSortType)).thenReturn(List.of());

        //when
        ResponseEntity<List<ProductDto>> response = productController.getSortedProducts(category, invalidSortType);

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getSortedProducts_ReturnsNotFound_InvalidCategory() {
        //given
        String invalidCategory = "InvalidCategory";
        String sortType = "ascending";

        when(productService.getSortedProducts(invalidCategory, sortType)).thenReturn(List.of());

        //when
        ResponseEntity<List<ProductDto>> response = productController.getSortedProducts(invalidCategory, sortType);

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getFeaturedProducts_ReturnExpectedList_CategoryIsFeatured() {
        //given
        String category = "FEATURED";

        ProductDto featuredProduct1 = createProductDto(1, "Product name 1", 100, category);
        ProductDto featuredProduct2 = createProductDto(2, "Product name 2", 200, category);

        List<ProductDto> expectedFeaturedProducts = List.of(featuredProduct1, featuredProduct2);

        when(productService.getFeaturedProducts()).thenReturn(expectedFeaturedProducts);

        //when
        ResponseEntity<List<ProductDto>> response = productController.getFeaturedProducts();

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedFeaturedProducts, response.getBody());
    }

    @Test
    void getFeaturedProducts_ReturnNotFound_FeaturedProductsEmpty() {
        //given
        when(productService.getFeaturedProducts()).thenReturn(List.of());

        //when
        ResponseEntity<List<ProductDto>> response = productController.getFeaturedProducts();

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteProductById_ReturnsOkStatus_ProductDeleted() {
        //when
        int productId = 1;

        ResponseEntity<String> response = productController.deleteProductById(productId);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully.", response.getBody());
        verify(productService, times(1)).deleteProductById(productId);
    }

    @Test
    void deleteProductById_ReturnsNotFound_ProductNotFound() {
        //given
        int productId = 100;

        String errorMessage = "Product not found";

        doThrow(new ProductNotFoundException(errorMessage)).when(productService).deleteProductById(productId);

        //when
        ResponseEntity<String> response = productController.deleteProductById(productId);

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(productService, times(1)).deleteProductById(productId);
    }

    @Test
    void updateProductById_UpdatesProductWithValidData_ProductWasUpdated() {
        //given
        int productId = 1;
        ProductRequest productRequest = createProductRequest();
        BindingResult bindingResult = getBindingResult(false);

        //when
        ResponseEntity<String> response = productController.updateProductById(productId, productRequest, bindingResult);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product updated successfully.", response.getBody());
        verify(productService, times(1)).updateProductById(productId, productRequest);
    }

    @Test
    void updateProductById_ReturnsNotFound_ProductNotFound() {
        //given
        int productId = 1;
        ProductRequest productRequest = new ProductRequest();
        BindingResult bindingResult = getBindingResult(false);

        doThrow(new ProductNotFoundException("Product not found")).when(productService).updateProductById(productId, productRequest);

        //when
        ResponseEntity<String> response = productController.updateProductById(productId, productRequest, bindingResult);

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody());
        verify(productService, times(1)).updateProductById(productId, productRequest);
    }

    @Test
    void updateProductById_ReturnsBadRequest_WhenBindingErrors() {
        //given
        int productId = 1;
        ProductRequest productRequest = createProductRequest();

        BindingResult bindingResult = getBindingResult(true);

        //when
        ResponseEntity<String> response = productController.updateProductById(productId,productRequest, bindingResult);

        //then
        verifyNoInteractions(productService);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getProductBySearchTerm_ReturnSearchResults_WhenMatchingProductsExist() {
        //given
        String category = "GEMSTONES";
        String searchTerm = "Product";
        String sortType = "newest";

        ProductDto productDto1 = createProductDto(1, "Product name 1", 100, category);
        ProductDto productDto2 = createProductDto(2, "Product name 2", 200, category);

        List<ProductDto> expectedSearchResults = List.of(productDto1, productDto2);

        when(productService.getProductBySearchTerm(searchTerm, sortType)).thenReturn(expectedSearchResults);

        //when
        ResponseEntity<List<ProductDto>> response = productController.getProductBySearchTerm(searchTerm, sortType);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSearchResults, response.getBody());
    }

    @Test
    void getProductBySearchTerm_ReturnNotFound_NoMatchingProducts() {
        //given
        String searchTerm = "InvalidSearchTerm";
        String sortType = "descending";

        when(productService.getProductBySearchTerm(searchTerm, sortType)).thenReturn(List.of());

        //when
        ResponseEntity<List<ProductDto>> response = productController.getProductBySearchTerm(searchTerm, sortType);

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getProductBySearchTerm_ReturnsNotFound_InvalidSortType() {
        //given
        String searchTerm = "Test";
        String invalidSortType = "InvalidSortType";

        when(productService.getProductBySearchTerm(searchTerm, invalidSortType)).thenReturn(List.of());

        //when
        ResponseEntity<List<ProductDto>> response = productController.getProductBySearchTerm(searchTerm, invalidSortType);

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
