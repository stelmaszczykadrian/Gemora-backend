package com.Gemora.unit.product;

import com.gemora.GemoraApplication;
import com.gemora.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.Gemora.unit.product.ProductTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class ProductServiceTest {
    private ProductService productService;

    @Mock
    private ProductRepository productRepositoryMock;

    @BeforeEach
    void init() {
        productService = new ProductService(productRepositoryMock);
    }

    @Test
    void getProductById_ReturnsProductDto_ProductExists() {
        //given
        int productId = 1;
        String category = "RINGS";
        ProductDto expectedProductDto = createProductDto(productId, "Product name 1", 100, category);

        Product product = createProduct(productId, "Product name 1", 100, category, null);

        when(productRepositoryMock.findById(productId)).thenReturn(Optional.of(product));

        //when
        Optional<ProductDto> productDtoOptional = productService.getProductById(productId);

        //then
        assertThat(productDtoOptional).isPresent();
        assertProductDtoEquals(expectedProductDto, productDtoOptional.get());
    }

    @Test
    void getProductById_ReturnsEmptyOptional_ProductIdDoesNotExist() {
        //given
        int productId = 100;

        when(productRepositoryMock.findById(productId)).thenReturn(Optional.empty());

        //when
        Optional<ProductDto> productDto = productService.getProductById(productId);

        //then
        assertTrue(productDto.isEmpty());
    }

    @Test
    void getAllProducts_ReturnsListOfAllSortedProducts_ProductListContainsProducts() {
        //given
        String category = "FEATURED";
        String sortType = "newest";

        ProductDto productDto1 = createProductDto(1, "Product name 1", 100, category);
        ProductDto productDto2 = createProductDto(2, "Product name 2", 200, category);

        List<Product> unsortedProducts = createMockedProducts();

        when(productRepositoryMock.findAll()).thenReturn(unsortedProducts);

        //when
        List<ProductDto> sortedProducts = productService.getAllProducts(sortType);

        //then
        verify(productRepositoryMock, times(1)).findAll();

        assertThat(sortedProducts).hasSize(2);
        assertProductDtoEquals(productDto2, sortedProducts.get(0));
        assertProductDtoEquals(productDto1, sortedProducts.get(1));
    }

    @Test
    void getAllProducts_ReturnsEmptyList_ProductListIsEmpty() {
        //given
        String sortType = "newest";

        when(productRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        //when
        List<ProductDto> products = productService.getAllProducts(sortType);

        //then
        assertThat(products).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(ProductCategory.class)
    void getProductsByCategory_ReturnsListWithAllProductsFromCategory_CategoryExists(ProductCategory productCategory) {
        //given
        int productId = 1;
        String productName = "Product name";
        double price = 100;

        String productCategoryValue = String.valueOf(productCategory);

        Product product = createProduct(productId, productName, price, productCategoryValue, null);
        ProductDto productDto = createProductDto(productId, productName, price, productCategoryValue);

        List<ProductDto> expectedValue = List.of(productDto);

        when(productRepositoryMock.findByCategory(productCategory.name())).thenReturn(List.of(product));

        //when
        List<ProductDto> result = productService.getProductsByCategory(productCategoryValue);

        //then
        assertIterableEquals(expectedValue, result);
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    void getProductsByCategory_ThrowRuntimeExceptionException_CategoryDoesNotExist() {
        // given
        String category = "NonExistentCategory";
        String message = "Unknown product category type: NonExistentCategory";

        //when & then
        assertThrows(RuntimeException.class, () -> productService.getProductsByCategory(category), message);
    }

    @Test
    void getSortedProducts_ReturnsSortedProductsByCategory_SortTypeIsPriceAsc() {
        //given
        String category = "RINGS";
        String sortType = "ascending";

        List<Product> products = createMockedProducts();

        when(productRepositoryMock.findByCategory(category)).thenReturn(products);

        //when
        List<ProductDto> sortedProducts = productService.getSortedProducts(category, sortType);

        //then
        assertThat(sortedProducts).isNotEmpty();
        assertThat(sortedProducts.get(0).getPrice()).isLessThan(sortedProducts.get(1).getPrice());
    }

    @Test
    void getFeaturedProducts_ReturnsListOfFeaturedProducts_CategoryIsFeatured() {
        //given
        String category = "FEATURED";

        List<Product> products = createMockedProducts();

        when(productRepositoryMock.findByCategory(category)).thenReturn(products);

        //when
        List<ProductDto> featuredProducts = productService.getFeaturedProducts();

        //then
        assertThat(featuredProducts).isNotNull();
        assertThat(featuredProducts.size()).isEqualTo(2);
    }

    @Test
    void getFeaturedProducts_ReturnsEmptyList_NoFeaturedProducts() {
        //given
        String category = "FEATURED";

        when(productRepositoryMock.findByCategory(category)).thenReturn(Collections.emptyList());

        //when
        List<ProductDto> featuredProducts = productService.getFeaturedProducts();

        //then
        assertThat(featuredProducts).isEmpty();
    }

    @Test
    void sortProducts_ReturnsListOfSortedProducts_ProductsAreSortedAscending() {
        //given
        String sortType = "ascending";

        List<Product> products = createMockedProducts();

        //when
        productService.sortProducts(products, sortType);

        //then
        assertEquals(100, products.get(0).getPrice());
        assertEquals(200, products.get(1).getPrice());
    }

    @Test
    void sortProducts_ReturnsListOfSortedProducts_ProductsAreSortedDescending() {
        //given
        String sortType = "descending";

        List<Product> products = createMockedProducts();

        //when
        productService.sortProducts(products, sortType);

        //then
        assertEquals(200, products.get(0).getPrice());
        assertEquals(100, products.get(1).getPrice());
    }

    @Test
    void sortProducts_ReturnsListOfSortedProducts_ProductsAreSortedByNewest() {
        //given
        String sortType = "newest";

        List<Product> products = createMockedProducts();

        //when
        productService.sortProducts(products, sortType);

        //then
        assertTrue(products.get(0).getPostingDate().isAfter(products.get(1).getPostingDate()));
    }

    @Test
    void sortProducts_ThrowRuntimeExceptionException_SortTypeIsUnsupported() {
        //given
        String sortType = "unsupportedSortType";
        String message = "Unknown sort type: unsupportedSortType";

        List<Product> products = createMockedProducts();

        //when & then
        assertThrows(RuntimeException.class, () -> productService.sortProducts(products, sortType), message);
    }

    @Test
    void sortProducts_ReturnsSortedProducts_ProductListIsEmptyList() {
        //given
        String sortType = "ascending";

        List<Product> products = new ArrayList<>();

        //when
        productService.sortProducts(products, sortType);

        //then
        assertTrue(products.isEmpty());
    }

    @Test
    void sortProducts_ReturnsSortedProducts_ProductListContainsOneProduct() {
        //given
        String sortType = "ascending";
        String category = "BRACELETS";

        Product product = createProduct(1, "Product name 1", 100, category, null);

        List<Product> products = new ArrayList<>();
        products.add(product);

        //when
        productService.sortProducts(products, sortType);

        //then
        assertEquals(product, products.get(0));
    }

    @Test
    void createProduct_ProductRequestWasPassed_SaveWasCalledOnce() {
        //given
        ProductRequest productRequest = createProductRequest();

        //when
        productService.createProduct(productRequest);

        //then
        verify(productRepositoryMock, times(1)).save(any());
    }

    @Test
    void createProduct_ThrowProductAlreadyExistsException_WhenProductExists() {
        //given
        String productName = "Product name";

        ProductRequest productRequest = createProductRequest();

        when(productRepositoryMock.findProductByName(productName)).thenReturn(Optional.of(new Product()));

        //when & then
        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(productRequest));

        verify(productRepositoryMock, times(1)).findProductByName(productName);
        verify(productRepositoryMock, never()).save(any());
    }

    @Test
    void deleteProductById_DeletesProduct_WhenProductExists() {
        //given
        int productId = 100;
        String category = "FEATURED";

        Product product = createProduct(productId, "Product name 1", 100, category, null);

        when(productRepositoryMock.findById(productId)).thenReturn(Optional.of(product));

        //when
        productService.deleteProductById(productId);

        //then
        verify(productRepositoryMock, times(1)).deleteById(productId);
    }

    @Test
    void deleteProductById_ThrowProductNotFoundException_ProductDoesNotExist() {
        //given
        int nonExistingProductId = 1000;

        when(productRepositoryMock.findById(nonExistingProductId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProductById(nonExistingProductId));

        verify(productRepositoryMock, times(1)).findById(nonExistingProductId);
        verify(productRepositoryMock, never()).deleteById(any());
    }

    @Test
    void updateProductById_UpdatesProduct_ValidProductData() {
        //given
        int productId = 1;

        ProductRequest productRequest = createProductRequest();

        Product productToUpdate = new Product(productId, productRequest.getName(), productRequest.getPrice(), productRequest.getManufacturer(),
                productRequest.getDescription(), productRequest.getCategory(), productRequest.getImage().getBytes(), LocalDateTime.now());

        when(productRepositoryMock.findById(productId)).thenReturn(Optional.of(productToUpdate));

        //when
        productService.updateProductById(productId, productRequest);

        //then
        verify(productRepositoryMock, times(1)).save(productToUpdate);
    }

    @Test
    void updateProductById_ThrowProductNotFoundException_ProductDoesNotExist() {
        //given
        int nonExistingProductId = 100;

        ProductRequest productRequest = createProductRequest();

        when(productRepositoryMock.findById(nonExistingProductId)).thenReturn(Optional.empty());

        //when
        assertThrows(ProductNotFoundException.class, () -> productService.updateProductById(nonExistingProductId, productRequest));

        //then
        verify(productRepositoryMock, times(1)).findById(nonExistingProductId);
        verify(productRepositoryMock, never()).save(any());
    }

    @Test
    void getProductBySearchTerm_ReturnsExpectedProducts_SearchTermExists() {
        //given
        String searchTerm = "Product";
        String sortType = "newest";

        List<Product> products = createMockedProducts();

        when(productRepositoryMock.findProductByNameContainingIgnoreCase(searchTerm)).thenReturn(products);

        //when
        List<ProductDto> actualProducts = productService.getProductBySearchTerm(searchTerm, sortType);

        //then
        assertEquals(products.size(), actualProducts.size());
    }

    @Test
    void getProductBySearchTerm_ReturnsEmptyList_SearchTermNoExists() {
        //given
        String searchTerm = "nonExistingSearchTerm";
        String sortType = "descending";

        when(productRepositoryMock.findProductByNameContainingIgnoreCase(searchTerm)).thenReturn(Collections.emptyList());

        //when
        List<ProductDto> actualProducts = productService.getProductBySearchTerm(searchTerm, sortType);

        //then
        assertEquals(0, actualProducts.size());
    }

    private void assertProductDtoEquals(ProductDto expected, ProductDto actual) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
        assertThat(actual.getManufacturer()).isEqualTo(expected.getManufacturer());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getCategory()).isEqualTo(expected.getCategory());
        assertThat(actual.getImage()).isEqualTo(expected.getImage());
    }

    private List<Product> createMockedProducts() {
        LocalDateTime date = LocalDateTime.now();
        String category = "FEATURED";

        Product product1 = createProduct(1, "Product name 1", 100, category, date.minusDays(1));
        Product product2 = createProduct(2, "Product name 2", 200, category, date);

        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        return products;
    }

}