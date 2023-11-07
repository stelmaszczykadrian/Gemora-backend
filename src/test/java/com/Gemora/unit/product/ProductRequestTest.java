package com.Gemora.unit.product;

import com.gemora.GemoraApplication;
import com.gemora.product.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = GemoraApplication.class)
public class ProductRequestTest {
    private final String PRODUCT_NAME = "Sample Product";
    private final double PRODUCT_PRICE = 100;
    private final String PRODUCT_MANUFACTURER = "Sample Manufacturer";
    private final String PRODUCT_DESCRIPTION = "Sample Description";
    private final String PRODUCT_CATEGORY = "FEATURED";
    private final String PRODUCT_IMAGE = "Sample Image Data";
    private ProductRequest productRequest;

    @BeforeEach
    public void setUp() {
        productRequest = createProductRequest(PRODUCT_PRICE);
    }

    @Test
    void createProductRequest_ReturnCorrectValues_AllFieldsAreCorrect() {
        //given
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName(PRODUCT_NAME);
        productRequest.setPrice(PRODUCT_PRICE);
        productRequest.setManufacturer(PRODUCT_MANUFACTURER);
        productRequest.setDescription(PRODUCT_DESCRIPTION);
        productRequest.setCategory(PRODUCT_CATEGORY);
        productRequest.setImage(PRODUCT_IMAGE);

        //then
        assertThat(productRequest)
                .hasFieldOrPropertyWithValue("name", PRODUCT_NAME)
                .hasFieldOrPropertyWithValue("price", PRODUCT_PRICE)
                .hasFieldOrPropertyWithValue("manufacturer", PRODUCT_MANUFACTURER)
                .hasFieldOrPropertyWithValue("description", PRODUCT_DESCRIPTION)
                .hasFieldOrPropertyWithValue("category", PRODUCT_CATEGORY)
                .hasFieldOrPropertyWithValue("image", PRODUCT_IMAGE);
    }

    @Test
    void createProductRequestWithBuilder_ReturnCorrectValues_ProductRequestIsValid() {
        assertAll(
                () -> assertEquals(PRODUCT_NAME, productRequest.getName()),
                () -> assertEquals(PRODUCT_PRICE, productRequest.getPrice()),
                () -> assertEquals(PRODUCT_MANUFACTURER, productRequest.getManufacturer()),
                () -> assertEquals(PRODUCT_DESCRIPTION, productRequest.getDescription()),
                () -> assertEquals(PRODUCT_CATEGORY, productRequest.getCategory()),
                () -> assertEquals(PRODUCT_IMAGE, productRequest.getImage())
        );
    }

    @Test
    void equals_AreEqual_TwoIdenticalProducts() {
        //given
        ProductRequest product1 = createProductRequest(PRODUCT_PRICE);
        ProductRequest product2 = createProductRequest(PRODUCT_PRICE);

        //then
        assertEquals(product1, product2);
    }

    @Test
    void equals_AreNotEqual_DifferentProducts() {
        //given
        ProductRequest product1 = createProductRequest(PRODUCT_PRICE);
        ProductRequest differentProduct = createProductRequest(50);

        //then
        assertNotEquals(product1, differentProduct);
    }

    @Test
    void hashCode_AreEquals_TwoIdenticalProducts() {
        //given
        ProductRequest product1 = createProductRequest(PRODUCT_PRICE);
        ProductRequest product2 = createProductRequest(PRODUCT_PRICE);

        //then
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void hashCode_AreNotEquals_DifferentProducts() {
        //given
        ProductRequest product1 = createProductRequest(PRODUCT_PRICE);
        ProductRequest differentProduct = createProductRequest(50);

        //then
        assertNotEquals(product1.hashCode(), differentProduct.hashCode());
    }

    @Test
    void equalsAndHashCode_AreEqual_EqualsAndHashCodeAreTrue() {
        //given
        ProductRequest product1 = createProductRequest(99.99);
        ProductRequest product2 = createProductRequest(99.99);

        boolean equal = product1.equals(product2);
        int hashCode1 = product1.hashCode();
        int hashCode2 = product2.hashCode();

        //then
        assertTrue(equal);
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void toString_ReturnsCorrectString_StringIsCalled() {
        //given
        String expectedToString = "ProductRequest(name=Sample Product, price=100.0, manufacturer=Sample Manufacturer, description=Sample Description, category=FEATURED, image=Sample Image Data)";

        //then
        assertEquals(expectedToString, productRequest.toString());
    }

    private ProductRequest createProductRequest(double price) {
        return ProductRequest.builder()
                .name(PRODUCT_NAME)
                .price(price)
                .manufacturer(PRODUCT_MANUFACTURER)
                .description(PRODUCT_DESCRIPTION)
                .category(PRODUCT_CATEGORY)
                .image(PRODUCT_IMAGE)
                .build();
    }
}

