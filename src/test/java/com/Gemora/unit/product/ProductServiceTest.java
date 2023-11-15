package com.Gemora.unit.product;

import com.gemora.GemoraApplication;
import com.gemora.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class ProductServiceTest {

    private final String CATEGORY_NAME = "FEATURED";
    private final String SORT_TYPE_ASC = "ascending";
    private final String PRODUCT_MANUFACTURER = "Acme Corporation";
    private final String PRODUCT_DESCRIPTION = "This is a product description.";
    private final String SORT_TYPE_NEW = "newest";
    private final String PRODUCT_1_NAME = "Product 1";
    private final String PRODUCT_2_NAME = "Product 2";
    private final Integer PRODUCT_1_ID = 1;
    private final Integer PRODUCT_2_ID = 2;
    private final byte[] IMAGE_BYTES = new byte[]{1, 2, 3};
    private final String BASE64_ENCODED_IMAGE = Base64.getEncoder().encodeToString(IMAGE_BYTES);
    private final double LOWER_PRICE = 100.00;
    private final double HIGHER_PRICE = 200.00;
    private final int FIRST_PRODUCT_INDEX = 0;
    private final int SECOND_PRODUCT_INDEX = 1;
    private final Product product = new Product(1, PRODUCT_1_NAME, LOWER_PRICE, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, CATEGORY_NAME, IMAGE_BYTES, LocalDateTime.now());
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
        ProductDto expectedProductDto = new ProductDto(PRODUCT_1_ID, PRODUCT_1_NAME, LOWER_PRICE, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, CATEGORY_NAME, BASE64_ENCODED_IMAGE);
        when(productRepositoryMock.findById(PRODUCT_1_ID)).thenReturn(Optional.of(product));

        //when
        Optional<ProductDto> productDtoOptional = productService.getProductById(PRODUCT_1_ID);

        //then
        assertThat(productDtoOptional).isPresent();
        assertProductDtoEquals(expectedProductDto, productDtoOptional.get());
    }

    @Test
    void getProductById_ReturnsEmptyOptional_ProductIdDoesNotExist() {
        //given
        int id = 100;
        when(productRepositoryMock.findById(PRODUCT_1_ID)).thenReturn(Optional.of(product));

        //when
        Optional<ProductDto> productDto = productService.getProductById(id);

        //then
        assertTrue(productDto.isEmpty());
    }

    @Test
    void getAllProducts_ReturnsListOfAllSortedProducts_ProductListContainsProducts() {
        //given
        int expectedSize = 2;

        List<Product> unsortedProducts = getMockedProducts(LOWER_PRICE, CATEGORY_NAME, HIGHER_PRICE);

        when(productRepositoryMock.findAll()).thenReturn(unsortedProducts);

        ProductDto productDto1 = new ProductDto(PRODUCT_1_ID, PRODUCT_1_NAME, LOWER_PRICE, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, CATEGORY_NAME, BASE64_ENCODED_IMAGE);
        ProductDto productDto2 = new ProductDto(PRODUCT_2_ID, PRODUCT_2_NAME, HIGHER_PRICE, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, CATEGORY_NAME, BASE64_ENCODED_IMAGE);

        //when
        List<ProductDto> sortedProducts = productService.getAllProducts(SORT_TYPE_NEW);

        //then
        verify(productRepositoryMock, times(1)).findAll();

        assertThat(sortedProducts).hasSize(expectedSize);
        assertProductDtoEquals(productDto2, sortedProducts.get(FIRST_PRODUCT_INDEX));
        assertProductDtoEquals(productDto1, sortedProducts.get(SECOND_PRODUCT_INDEX));
    }

    @Test
    void getAllProducts_ReturnsEmptyList_ProductListIsEmpty() {
        //given
        List<Product> emptyProducts = new ArrayList<>();

        when(productRepositoryMock.findAll()).thenReturn(emptyProducts);

        //when
        List<ProductDto> products = productService.getAllProducts(SORT_TYPE_NEW);

        //then
        assertThat(products).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(ProductCategory.class)
    void getProductsByCategory_ReturnsListWithAllProductsFromCategory_CategoryExists(ProductCategory productCategory) {
        //given
        List<ProductDto> expectedValue = List.of(new ProductDto(PRODUCT_1_ID, PRODUCT_1_NAME, HIGHER_PRICE, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, CATEGORY_NAME, BASE64_ENCODED_IMAGE));

        when(productRepositoryMock.findByCategory(productCategory.name())).thenReturn(List.of(new Product(PRODUCT_1_ID, PRODUCT_1_NAME, HIGHER_PRICE, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, CATEGORY_NAME, IMAGE_BYTES, LocalDateTime.now())));

        //when
        List<ProductDto> result = productService.getProductsByCategory(productCategory.name());

        //then
        assertIterableEquals(expectedValue, result);
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    void getProductsByCategory_ThrowsException_CategoryDoesNotExist() {
        // given
        String category = "NonExistentCategory";

        //when & then
        assertThrows(RuntimeException.class, () -> productService.getProductsByCategory(category), "Unknown product category type: NonExistentCategory");
    }

    @Test
    void getSortedProducts_ReturnsSortedProductsByCategory_SortTypeIsPriceAsc() {
        //given
        String category = "RINGS";

        List<Product> products = getMockedProducts(HIGHER_PRICE, category, LOWER_PRICE);

        when(productRepositoryMock.findByCategory(category)).thenReturn(products);

        //when
        List<ProductDto> sortedProducts = productService.getSortedProducts(category, SORT_TYPE_ASC);

        //then
        assertThat(sortedProducts).isNotEmpty();
        assertThat(sortedProducts.get(FIRST_PRODUCT_INDEX).getPrice()).isLessThan(sortedProducts.get(SECOND_PRODUCT_INDEX).getPrice());
    }

    @Test
    void getFeaturedProducts_ReturnsListOfFeaturedProducts_CategoryIsFeatured() {
        //given
        int expectedSize = 2;

        List<Product> products = getMockedProducts(LOWER_PRICE, CATEGORY_NAME, HIGHER_PRICE);

        when(productRepositoryMock.findByCategory(CATEGORY_NAME)).thenReturn(products);

        //when
        List<ProductDto> featuredProducts = productService.getFeaturedProducts();

        //then
        assertThat(featuredProducts).isNotNull();
        assertThat(featuredProducts.size()).isEqualTo(expectedSize);
    }

    @Test
    void getFeaturedProducts_ReturnsEmptyList_NoFeaturedProducts() {
        //given
        when(productRepositoryMock.findByCategory(CATEGORY_NAME)).thenReturn(new ArrayList<>());

        //when
        List<ProductDto> featuredProducts = productService.getFeaturedProducts();

        //then
        assertThat(featuredProducts).isEmpty();
    }

    @Test
    void sortProducts_ReturnsListOfSortedProducts_ProductsAreSortedAscending() {
        //given
        List<Product> products = getMockedProducts(LOWER_PRICE, CATEGORY_NAME, HIGHER_PRICE);

        //when
        productService.sortProducts(products, SORT_TYPE_ASC);

        //then
        assertEquals(LOWER_PRICE, products.get(FIRST_PRODUCT_INDEX).getPrice());
        assertEquals(HIGHER_PRICE, products.get(SECOND_PRODUCT_INDEX).getPrice());
    }

    @Test
    void sortProducts_ReturnsListOfSortedProducts_ProductsAreSortedDescending() {
        //given
        List<Product> products = getMockedProducts(LOWER_PRICE, CATEGORY_NAME, HIGHER_PRICE);

        //when
        String SORT_TYPE_DESC = "descending";
        productService.sortProducts(products, SORT_TYPE_DESC);

        //then
        assertEquals(HIGHER_PRICE, products.get(FIRST_PRODUCT_INDEX).getPrice());
        assertEquals(LOWER_PRICE, products.get(SECOND_PRODUCT_INDEX).getPrice());
    }

    @Test
    void sortProducts_ReturnsListOfSortedProducts_ProductsAreSortedByNewest() {
        //given
        List<Product> products = getMockedProducts(LOWER_PRICE, CATEGORY_NAME, HIGHER_PRICE);

        //when
        productService.sortProducts(products, SORT_TYPE_NEW);

        //then
        assertTrue(products.get(FIRST_PRODUCT_INDEX).getPostingDate().isAfter(products.get(SECOND_PRODUCT_INDEX).getPostingDate()));
    }

    @Test
    void sortProducts_ThrowsException_SortTypeIsUnsupported() {
        //given
        List<Product> products = getMockedProducts(LOWER_PRICE, CATEGORY_NAME, HIGHER_PRICE);

        //when & then
        assertThrows(RuntimeException.class, () -> productService.sortProducts(products, "unsupportedSortType"), "Unknown sort type: unsupportedSortType");
    }

    @Test
    void sortProducts_ReturnsSortedProducts_ProductListIsEmptyList() {
        //given
        List<Product> products = new ArrayList<>();

        //when
        productService.sortProducts(products, SORT_TYPE_ASC);

        //then
        assertTrue(products.isEmpty());
    }

    @Test
    void sortProducts_ReturnsSortedProducts_ProductListContainsOneProduct() {
        //given
        List<Product> products = new ArrayList<>();
        products.add(product);

        //when
        productService.sortProducts(products, SORT_TYPE_ASC);

        //then
        assertEquals(product, products.get(FIRST_PRODUCT_INDEX));
    }

    @Test
    void createProduct_ProductRequestWasPassed_SaveWasCalledOnce() {
        //given
        ProductRequest productRequest = ProductRequest.builder()
                .name(PRODUCT_1_NAME)
                .price(LOWER_PRICE)
                .category(CATEGORY_NAME)
                .description(PRODUCT_DESCRIPTION)
                .image(BASE64_ENCODED_IMAGE)
                .manufacturer(PRODUCT_MANUFACTURER)
                .build();

        //when
        productService.createProduct(productRequest);

        //then
        verify(productRepositoryMock, times(1)).save(any());
    }

    @Test
    void createProduct_ProductRequestIsNull_SaveWasNeverCalled() {
        //when & then
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(null));
    }

    @Test
    void deleteProductById_DeleteProductFromDatabase_DeleteWasCalledOnce() {
        //when
        productService.deleteProductById(PRODUCT_1_ID);

        //then
        verify(productRepositoryMock, times(1)).deleteById(PRODUCT_1_ID);

        Product productAfterDelete = productRepositoryMock.findById(PRODUCT_1_ID).orElse(null);
        assertThat(productAfterDelete).isNull();
    }

    @Test
    public void updateProduct_ValidProductData_UpdatesProduct() {
        //given
        int id = 1;
        String BASE64_ENCODED_IMAGE = Base64.getEncoder().encodeToString(IMAGE_BYTES);

        ProductDto productDto = new ProductDto(id, "Update Product", 300, "Update Manufacturer", "Update description", CATEGORY_NAME, BASE64_ENCODED_IMAGE );

        Product productToUpdate = new Product(productDto.getId(), productDto.getName(), productDto.getPrice(), productDto.getManufacturer(),
                productDto.getDescription(), productDto.getCategory(), productDto.getImage().getBytes(), LocalDateTime.now());

        when(productRepositoryMock.findById(id)).thenReturn(Optional.of(productToUpdate));

        //when
        productService.updateProductById(id, productDto);

        //then
        verify(productRepositoryMock, times(1)).save(productToUpdate);
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

    private List<Product> getMockedProducts(double price, String FEATURED, double price1) {
        LocalDateTime now = LocalDateTime.now();
        Product product1 = new Product(PRODUCT_1_ID, PRODUCT_1_NAME, price, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, FEATURED, IMAGE_BYTES, now.minusDays(1));
        Product product2 = new Product(PRODUCT_2_ID, PRODUCT_2_NAME, price1, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, FEATURED, IMAGE_BYTES, now);

        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        return products;
    }
}