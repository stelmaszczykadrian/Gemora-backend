package com.example.Gemora.product;

import com.gemora.GemoraApplication;
import com.gemora.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = GemoraApplication.class)
public class ProductTest {
    private final String PRODUCT_NAME_FIRST = "Product 1";
    private final String PRODUCT_NAME_SECOND = "Product 2";
    private final Integer PRODUCT_ID_1 = 1;
    private final Integer PRODUCT_ID_2 = 2;
    private final String PRODUCT_MANUFACTURER = "Acme Corporation";
    private final String PRODUCT_DESCRIPTION = "This is the best product ever!";
    private final String PRODUCT_CATEGORY = "Electronics";
    private final byte[] PRODUCT_IMAGE_FIRST = new byte[100];
    private final byte[] PRODUCT_IMAGE_SECOND = new byte[200];
    private final double PRODUCT_PRICE = 100;

    @ParameterizedTest
    @MethodSource("productProvider")
    void testGettersAndSetters_ReturnCorrectValues_ProductIsValid(Product product) {
        assertEquals(product.getId(), product.getId());
        assertEquals(product.getName(), product.getName());
        assertEquals(product.getPrice(), product.getPrice());
        assertEquals(product.getManufacturer(), product.getManufacturer());
        assertEquals(product.getDescription(), product.getDescription());
        assertEquals(product.getCategory(), product.getCategory());
        assertEquals(product.getImage().length, product.getImage().length);
        assertEquals(product.getPostingDate(), product.getPostingDate());
    }

    @Test
    void testBuilder_BuildsValidProduct_AllPropertiesAreSet() {
        //given
        int imageLength = 100;

        Product product = Product.builder()
                .id(PRODUCT_ID_1)
                .name(PRODUCT_NAME_FIRST)
                .price(PRODUCT_PRICE)
                .manufacturer(PRODUCT_MANUFACTURER)
                .description(PRODUCT_DESCRIPTION)
                .category(PRODUCT_CATEGORY)
                .image(PRODUCT_IMAGE_FIRST)
                .postingDate(LocalDateTime.now())
                .build();

        //when
        assertEquals(PRODUCT_ID_1, product.getId());
        assertEquals(PRODUCT_NAME_FIRST, product.getName());
        assertEquals(PRODUCT_PRICE, product.getPrice());
        assertEquals(PRODUCT_MANUFACTURER, product.getManufacturer());
        assertEquals(PRODUCT_DESCRIPTION, product.getDescription());
        assertEquals(PRODUCT_CATEGORY, product.getCategory());
        assertEquals(imageLength, product.getImage().length);
        assertEquals(LocalDateTime.now(), product.getPostingDate());
    }

    @Test
    void testProductsAreEqualAndHaveSameHashCode_ProductsHaveSameValues() {
        //given
        Product product1 = newProductAndInitialize();
        Product product2 = newProductAndInitialize();

        //when
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void testEqualsMethod_ReturnsTrue_ProductsAreSame() {
        //given
        Product product1 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_FIRST);

        //when
        assertEquals(product1, product1);
    }

    @Test
    void testEqualsMethod_ReturnsTrue_ProductsHaveSameValues() {
        //given
        Product product1 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_FIRST);
        Product product2 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_FIRST);

        //when
        assertEquals(product1, product2);
    }

    @Test
    void testEqualsMethod_AreNotEqual_ProductsWithDifferentIds() {
        //given
        Product product1 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_FIRST);
        Product product2 = createProduct(PRODUCT_ID_2, PRODUCT_NAME_FIRST);

        //when
        assertNotEquals(product1, product2);
    }

    @Test
    void testEqualsMethod_AreNotEqual_ProductsWithDifferentNames() {
        //given
        Product product1 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_FIRST);
        Product product2 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_SECOND);

        //when
        assertNotEquals(product1, product2);
    }

    @Test
    void testEqualsMethod_ProductIsNotEqualToString() {
        //given
        Product product = new Product();

        //when
        assertNotEquals(product, PRODUCT_NAME_FIRST);
    }

    @Test
    void testEqualsMethod_ReturnsTrueForSameValueObject() {
        //given
        Product product1 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_FIRST);

        //when
        assertEquals(product1.hashCode(), product1.hashCode());
    }

    @Test
    void testEqualsAndHashCodeMethods_ReturnCorrectValues_ProductsWithSameValues() {
        //given
        Product product1 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_FIRST);
        Product product2 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_FIRST);

        //then
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void testHashCodeMethods_ReturnDifferentValues_ProductsHasDifferentIds() {
        //given
        Product product1 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_FIRST);
        Product product2 = createProduct(PRODUCT_ID_2, PRODUCT_NAME_FIRST);

        //then
        assertNotEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void testTwoProducts_AreNotEqual_ProductsHasDifferentHashCodes() {
        //given
        Product product1 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_FIRST);
        Product product2 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_SECOND);

        //then
        assertNotEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void testProductWithNullId_AreNotEqual_ProductWithNonNullId() {
        //given
        Product product1 = new Product();
        product1.setId(null);
        product1.setName(PRODUCT_NAME_FIRST);

        Product product2 = new Product();
        product2.setId(PRODUCT_ID_1);
        product2.setName(PRODUCT_NAME_FIRST);

        //then
        assertNotEquals(product1, product2);
    }

    @Test
    void testProductWithNullName_AreNotEqual_ProductWithNonNullName() {
        //given
        Product product1 = new Product();
        product1.setId(PRODUCT_ID_1);
        product1.setName(null);

        Product product2 = new Product();
        product2.setId(PRODUCT_ID_1);
        product2.setName(PRODUCT_NAME_FIRST);

        //then
        assertNotEquals(product1, product2);
    }

    @Test
    void testTwoProducts_AreNotEqual_ProductHasDifferentImages() {
        //given
        Product product1 = new Product();
        product1.setId(PRODUCT_ID_1);
        product1.setName(PRODUCT_NAME_FIRST);
        product1.setImage(PRODUCT_IMAGE_FIRST);

        Product product2 = new Product();
        product2.setId(PRODUCT_ID_1);
        product2.setName(PRODUCT_NAME_FIRST);
        product2.setImage(PRODUCT_IMAGE_SECOND);

        //then
        assertFalse(Arrays.equals(product1.getImage(), product2.getImage()));
    }

    @Test
    void testTwoProducts_AreNotEqual_ProductsHasDifferentPostingDates() {
        //given
        Product product1 = new Product();
        product1.setId(PRODUCT_ID_1);
        product1.setName(PRODUCT_NAME_FIRST);
        product1.setPostingDate(LocalDateTime.now());

        Product product2 = new Product();
        product2.setId(PRODUCT_ID_1);
        product2.setName(PRODUCT_NAME_FIRST);
        product2.setPostingDate(LocalDateTime.now().plusDays(1));

        //then
        assertNotEquals(product1, product2);
    }

    private Product newProductAndInitialize() {
        Product product1 = createProduct(PRODUCT_ID_1, PRODUCT_NAME_FIRST);
        product1.setPrice(PRODUCT_PRICE);
        product1.setManufacturer(PRODUCT_MANUFACTURER);
        product1.setDescription(PRODUCT_DESCRIPTION);
        product1.setCategory(PRODUCT_CATEGORY);
        product1.setImage(PRODUCT_IMAGE_FIRST);
        product1.setPostingDate(LocalDateTime.now());
        return product1;
    }

    private Product createProduct(int id, String name) {
        Product product1 = new Product();
        product1.setId(id);
        product1.setName(name);
        return product1;
    }

    public List<Product> productProvider() {
        int priceSecondProduct = 200;
        String manufacturerSecondProduct = "Another Corporation";
        String descriptionSecondProduct = "This is another great product!";
        String categorySecondProduct = "Clothing";

        List<Product> products = new ArrayList<>();

        Product product1 = new Product();
        product1.setId(PRODUCT_ID_1);
        product1.setName(PRODUCT_NAME_FIRST);
        product1.setPrice(PRODUCT_PRICE);
        product1.setManufacturer(PRODUCT_MANUFACTURER);
        product1.setDescription(PRODUCT_DESCRIPTION);
        product1.setCategory(PRODUCT_CATEGORY);
        product1.setImage(PRODUCT_IMAGE_FIRST);
        product1.setPostingDate(LocalDateTime.now());

        products.add(product1);

        Product product2 = new Product();
        product2.setId(PRODUCT_ID_2);
        product2.setName(PRODUCT_NAME_SECOND);
        product2.setPrice(priceSecondProduct);
        product2.setManufacturer(manufacturerSecondProduct);
        product2.setDescription(descriptionSecondProduct);
        product2.setCategory(categorySecondProduct);
        product2.setImage(PRODUCT_IMAGE_SECOND);
        product2.setPostingDate(LocalDateTime.now().plusDays(1));

        products.add(product2);

        return products;
    }
}
