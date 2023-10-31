package com.example.Gemora.product;

import com.gemora.GemoraApplication;
import com.gemora.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class ProductControllerTest {
    private final String CATEGORY_NAME = "FEATURED";
    private final String PRODUCT_MANUFACTURER = "Acme Corporation";
    private final String PRODUCT_DESCRIPTION = "This is a product description.";
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
        ProductDto expectedProduct = getProductDto(PRODUCT_1_ID, PRODUCT_1_NAME, LOWER_PRICE, CATEGORY_NAME);
        when(productService.getProductById(PRODUCT_1_ID)).thenReturn(Optional.of(expectedProduct));

        //when
        Optional<ProductDto> actualProductDto = productController.getProductById(PRODUCT_1_ID);

        //then
        assertThat(actualProductDto).isPresent();
        assertThat(actualProductDto.get()).isEqualTo(expectedProduct);
    }

    @Test
    void getProductById_ReturnsEmptyOptional_ProductNotExist() {
        //given
        int productId = 999;

        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        //when
        Optional<ProductDto> actualProduct = productController.getProductById(productId);

        //then
        assertThat(actualProduct).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(SortType.class)
    void getAllProducts_ReturnExpectedList_ForAnySortByOptions(SortType sortType) {
        //given
        String sortTypeValue = String.valueOf(sortType);

        ProductDto productDto1 = getProductDto(PRODUCT_1_ID, PRODUCT_1_NAME, LOWER_PRICE, CATEGORY_NAME);
        ProductDto productDto2 = getProductDto(PRODUCT_2_ID, PRODUCT_2_NAME, HIGHER_PRICE, CATEGORY_NAME);

        List<ProductDto> expectedProducts = new ArrayList<>();
        expectedProducts.add(productDto1);
        expectedProducts.add(productDto2);

        when(productService.getAllProducts(sortTypeValue)).thenReturn(expectedProducts);

        //when
        List<ProductDto> allProducts = productController.getAllProducts(sortTypeValue);

        //then
        assertProductDtos(productDto1, productDto2, expectedProducts, allProducts);
    }

    @Test
    void createProduct_ValidProductRequest_CreateProductWasCalledOnce() {
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
        productController.createProduct(productRequest);

        //then
        verify(productService, times(1)).createProduct(productRequest);
    }

    @ParameterizedTest
    @EnumSource(ProductCategory.class)
    void getProductsByCategory_ReturnExpectedProductList_ForValidCategory(ProductCategory productCategory) {
        //given
        String productCategoryValue = String.valueOf(productCategory);

        ProductDto productDto1 = getProductDto(PRODUCT_1_ID, PRODUCT_1_NAME, LOWER_PRICE, productCategoryValue);
        ProductDto productDto2 = getProductDto(PRODUCT_2_ID, PRODUCT_2_NAME, HIGHER_PRICE, productCategoryValue);

        List<ProductDto> expectedProductDtos = List.of(productDto1, productDto2);

        when(productService.getProductsByCategory(productCategoryValue)).thenReturn(expectedProductDtos);

        //when
        List<ProductDto> actualProductDtos = productController.getProductsByCategory(productCategoryValue);

        //then
        assertProductDtos(productDto1, productDto2, expectedProductDtos, actualProductDtos);
    }

    @ParameterizedTest
    @EnumSource(SortType.class)
    void getSortedProducts_ReturnExpectedProductList_ValidSortTypeAndCategory(SortType sortType) {
        //given
        String sortTypeValue = String.valueOf(sortType);

        ProductDto productDto1 = getProductDto(PRODUCT_1_ID, PRODUCT_1_NAME, LOWER_PRICE, sortTypeValue);
        ProductDto productDto2 = getProductDto(PRODUCT_2_ID, PRODUCT_2_NAME, HIGHER_PRICE, sortTypeValue);

        List<ProductDto> expectedProductDtos = List.of(productDto1, productDto2);

        when(productService.getSortedProducts(CATEGORY_NAME, sortTypeValue)).thenReturn(expectedProductDtos);

        //when
        List<ProductDto> actualProductDtos = productController.getSortedProducts(CATEGORY_NAME, sortTypeValue);

        //then
        assertProductDtos(productDto1, productDto2, expectedProductDtos, actualProductDtos);

        assertThat(actualProductDtos.get(FIRST_PRODUCT_INDEX).getImage()).isNotBlank();
        assertThat(actualProductDtos.get(SECOND_PRODUCT_INDEX).getImage()).isNotBlank();
    }

    @Test
    void getFeaturedProducts_ReturnExpectedList_CategoryIsFeatured() {
        //given
        ProductDto featuredProduct1 = getProductDto(PRODUCT_1_ID, PRODUCT_1_NAME, LOWER_PRICE, CATEGORY_NAME);
        ProductDto featuredProduct2 = getProductDto(PRODUCT_2_ID, PRODUCT_2_NAME, HIGHER_PRICE, CATEGORY_NAME);

        List<ProductDto> expectedFeaturedProducts = List.of(featuredProduct1, featuredProduct2);

        when(productService.getFeaturedProducts()).thenReturn(expectedFeaturedProducts);

        //when
        List<ProductDto> actualFeaturedProducts = productController.getFeaturedProducts();

        //then
        assertProductDtos(featuredProduct1, featuredProduct2, expectedFeaturedProducts, actualFeaturedProducts);
    }

    private ProductDto getProductDto(int id, String productName, double price, String category) {
        return new ProductDto(id, productName, price, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, category, BASE64_ENCODED_IMAGE);
    }

    private void assertProductDtos(ProductDto productDto1, ProductDto productDto2, List<ProductDto> expectedProductDtos, List<ProductDto> actualProductDtos) {
        int expectedSize = 2;

        assertThat(actualProductDtos)
                .isEqualTo(expectedProductDtos)
                .isNotNull()
                .hasSize(expectedSize);

        assertThat(actualProductDtos.get(FIRST_PRODUCT_INDEX).getName()).isEqualTo(productDto1.getName());
        assertThat(actualProductDtos.get(SECOND_PRODUCT_INDEX).getName()).isEqualTo(productDto2.getName());
    }
}
