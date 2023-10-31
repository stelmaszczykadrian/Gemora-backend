package com.example.Gemora.product;

import com.gemora.product.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductDtoTest {
    private final Integer PRODUCT_ID = 1;
    private final String PRODUCT_NAME = "Test product";
    private final double LOWER_PRICE = 100.00;
    private final String PRODUCT_MANUFACTURER = "Acme";
    private final String PRODUCT_DESCRIPTION = "This is a test product.";
    private final String PRODUCT_CATEGORY = "Electronics";
    private final String PRODUCT_IMAGE = "https://example.com/test-product.jpg";
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
    }

    @Test
    void testCreateProductDto_CreatesProduct_WithAllRequiredFields() {
        //given
        ProductDto productDto = buildProductDto(PRODUCT_ID, PRODUCT_NAME, LOWER_PRICE, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, PRODUCT_CATEGORY, PRODUCT_IMAGE);

        //then
        assertThat(productDto)
                .extracting(
                        ProductDto::getId,
                        ProductDto::getName,
                        ProductDto::getPrice,
                        ProductDto::getManufacturer,
                        ProductDto::getDescription,
                        ProductDto::getCategory,
                        ProductDto::getImage
                )
                .containsExactly(
                        PRODUCT_ID,
                        PRODUCT_NAME,
                        LOWER_PRICE,
                        PRODUCT_MANUFACTURER,
                        PRODUCT_DESCRIPTION,
                        PRODUCT_CATEGORY,
                        PRODUCT_IMAGE
                );
    }

    @Test
    void createProductDto_ReturnsProductDtoWithExpectedValues_RequiredFieldsOnly() {
        //given
        productDto = getProductDto(LOWER_PRICE, null, null, null);

        //then
        assertEquals(PRODUCT_NAME, productDto.getName());
        assertEquals(LOWER_PRICE, productDto.getPrice());
    }

    @Test
    void testSetAndGetId_ReturnsExpectedId_SettingId() {
        //when
        productDto.setId(PRODUCT_ID);

        //then
        assertEquals(PRODUCT_ID, productDto.getId());
    }

    @Test
    void testSetAndGetName_ReturnsExpectedName_SettingName() {
        //when
        productDto.setName(PRODUCT_NAME);

        //then
        assertEquals(PRODUCT_NAME, productDto.getName());
    }

    @Test
    void testSetAndGetPrice_ReturnsExpectedPrice_SettingPrice() {
        //when
        productDto.setPrice(LOWER_PRICE);

        //then
        assertEquals(LOWER_PRICE, productDto.getPrice());
    }

    @Test
    void testSetAndGetManufacturer_ReturnsExpectedManufacturer_SettingManufacturer() {
        //when
        productDto.setManufacturer(PRODUCT_MANUFACTURER);

        //then
        assertEquals(PRODUCT_MANUFACTURER, productDto.getManufacturer());
    }

    @Test
    void testSetAndGetDescription_ReturnsExpectedDescription_SettingDescription() {
        //when
        productDto.setDescription(PRODUCT_DESCRIPTION);

        //then
        assertEquals(PRODUCT_DESCRIPTION, productDto.getDescription());
    }

    @Test
    void testSetAndGetCategory_ReturnsExpectedCategory_SettingCategory() {
        //when
        productDto.setCategory(PRODUCT_CATEGORY);

        //then
        assertEquals(PRODUCT_CATEGORY, productDto.getCategory());
    }

    @Test
    void testSetAndGetImage_ReturnsExpectedImage_SettingImage() {
        //when
        productDto.setImage(PRODUCT_IMAGE);

        //then
        assertEquals(PRODUCT_IMAGE, productDto.getImage());
    }

    @Test
    void testTwoObjects_ReturnSameHashCodeForEqualObjects_TwoEqualProductDtos() {
        //given
        ProductDto productDto1 = getProductDto(LOWER_PRICE, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, PRODUCT_CATEGORY);
        ProductDto productDto2 = getProductDto(LOWER_PRICE, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, PRODUCT_CATEGORY);

        //when
        int hashCode1 = productDto1.hashCode();
        int hashCode2 = productDto2.hashCode();

        //then
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testTwoObjects_ReturnDifferentHashCodeForUnequalObjects_TwoUnequalObjects() {
        //given
        int priceSecondProduct = 200;
        String manufacturerSecondProduct = "Google";
        String descriptionSecondProduct = "This is another test product.";
        String categorySecondProduct = "Software";

        ProductDto productDto1 = getProductDto(LOWER_PRICE, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, PRODUCT_CATEGORY);
        ProductDto productDto2 = getProductDto(priceSecondProduct, manufacturerSecondProduct, descriptionSecondProduct, categorySecondProduct);

        //when
        int hashCode1 = productDto1.hashCode();
        int hashCode2 = productDto2.hashCode();

        //then
        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void testStringRepresentation_ReturnStringRepresentationOfProductDto_ContainsExpectedInformation() {
        // Arrange
        double price = 10;
        productDto = getProductDto(price, PRODUCT_MANUFACTURER, PRODUCT_DESCRIPTION, PRODUCT_CATEGORY);

        // Act
        String stringRepresentation = productDto.toString();

        // Assert
        String expectedStringRepresentation = "ProductDto(id=1, name=Test product, price=10.0, manufacturer=Acme, description=This is a test product., category=Electronics, image=null)";
        assertThat(stringRepresentation).isEqualTo(expectedStringRepresentation);
    }

    private ProductDto getProductDto(double price, String manufacturer, String description, String category) {
        return new ProductDto(PRODUCT_ID, PRODUCT_NAME, price, manufacturer, description, category, null);
    }

    private  ProductDto buildProductDto(int id, String name, double price, String manufacturer, String description, String category, String image) {
        return ProductDto.builder()
                .id(id)
                .name(name)
                .price(price)
                .manufacturer(manufacturer)
                .description(description)
                .category(category)
                .image(image)
                .build();
    }
}
