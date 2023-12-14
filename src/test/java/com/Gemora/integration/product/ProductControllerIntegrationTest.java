package com.Gemora.integration.product;

import com.gemora.GemoraApplication;
import com.gemora.product.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.Gemora.unit.TestUtils.asJsonString;
import static com.Gemora.unit.product.ProductTestHelper.createProductDto;
import static com.Gemora.unit.product.ProductTestHelper.createProductRequest;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = GemoraApplication.class)
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getProductById_ReturnsOkStatus_ValidProduct() throws Exception {
        //given
        int productId = 100;
        double price = 100;
        String category = "RINGS";
        String productName = "Product name";

        ProductDto productDto = createProductDto(productId, productName, price, category);

        when(productService.getProductById(eq(productId))).thenReturn(Optional.of(productDto));

        //when
        ResultActions result = mockMvc.perform(get("/api/products/{id}", productId));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value(productName))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.manufacturer").value("Product manufacturer"))
                .andExpect(jsonPath("$.category").value(category))
                .andExpect(jsonPath("$.description").value("Product description"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getProductById_ReturnsNotFoundStatus_ProductDoesNotExist() throws Exception {
        //given
        int productId = 2;

        when(productService.getProductById(eq(productId))).thenReturn(Optional.empty());

        //when
        ResultActions result = mockMvc.perform(get("/api/products/{id}", productId));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void getAllProducts_ReturnsOkStatus_ValidProducts() throws Exception {
        //given
        String category = "GEMSTONES";

        List<ProductDto> productDtoList = createProductDtosMockedList(category);

        when(productService.getAllProducts(eq("ascending"))).thenReturn(productDtoList);

        //when
        ResultActions result = mockMvc.perform(get("/api/products?sortBy=ascending"));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllProducts_ReturnsNotFoundStatus_ProductListAreEmpty() throws Exception {
        //given
        String sortType = "descending";

        when(productService.getAllProducts(eq(sortType))).thenReturn(Collections.emptyList());

        //when
        ResultActions result = mockMvc.perform(get("/api/products?sortBy=descending"));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void createProduct_ReturnsIsCreatedStatus_ProductRequestIsValid() throws Exception {
        //given
        ProductRequest productRequest = createProductRequest();

        doNothing().when(productService).createProduct(any(ProductRequest.class));

        //when
        ResultActions result = mockMvc.perform(post("/api/products")
                .content(asJsonString(productRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isCreated())
                .andExpect(content().string("Product added successfully."));
    }

    @Test
    void createProduct_ReturnsConflictStatus_ThrowProductAlreadyExistException() throws Exception {
        //given
        ProductRequest productRequest = createProductRequest();

        doThrow(ProductAlreadyExistsException.class).when(productService).createProduct(any(ProductRequest.class));

        //when
        ResultActions result = mockMvc.perform(post("/api/products")
                .content(asJsonString(productRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isConflict());
    }

    @Test
    public void getProductsByCategory_ReturnsOkStatus_ProductRequestIsValid() throws Exception {
        //given
        String category = "EARRINGS";

        List<ProductDto> productDtoList = createProductDtosMockedList(category);

        when(productService.getProductsByCategory(eq(category))).thenReturn(productDtoList);

        //when
        ResultActions result = mockMvc.perform(get("/api/products/category/{category}", category));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getProductsByCategory_ReturnsNotFoundStatus_ProductsAreEmpty() throws Exception {
        //given
        String category = "ENGAGEMENTS";

        when(productService.getProductsByCategory(eq(category))).thenReturn(Collections.emptyList());

        //when
        ResultActions result = mockMvc.perform(get("/api/products/category/{category}", category));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    public void getSortedProducts_ReturnsOkStatus_ProductsAreSortedByCategory() throws Exception {
        //given
        String sortType = "ascending";
        String category = "PENDANTS";

        List<ProductDto> productDtoList = createProductDtosMockedList(category);

        when(productService.getSortedProducts(eq(category), eq(sortType))).thenReturn(productDtoList);

        //when
        ResultActions result = mockMvc.perform(get("/api/products/sorted")
                .param("category", category)
                .param("sort", sortType));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSortedProducts_ReturnsNotFoundStatus_ProductsAreEmpty() throws Exception {
        //given
        String sortType = "newest";
        String category = "RINGS";

        when(productService.getSortedProducts(eq(category), eq(sortType))).thenReturn(Collections.emptyList());

        //when
        ResultActions result = mockMvc.perform(get("/api/products/sorted")
                .param("category", category)
                .param("sort", sortType));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    public void getFeaturedProducts_ReturnsOkStatus_FeaturedProductsArePresent() throws Exception {
        //given
        String category = "FEATURED";

        List<ProductDto> productDtoList = createProductDtosMockedList(category);

        when(productService.getFeaturedProducts()).thenReturn(productDtoList);

        //when
        ResultActions result = mockMvc.perform(get("/api/products/featured"));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getFeaturedProducts_ReturnsNotFoundStatus_FeaturedProductsListIsEmpty() throws Exception {
        //given
        when(productService.getFeaturedProducts()).thenReturn(Collections.emptyList());

        //when
        ResultActions result = mockMvc.perform(get("/api/products/featured"));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void deleteProductById_ReturnsOkStatus_ProductDeletedSuccessfully() throws Exception {
        //given
        int productId = 1;

        doNothing().when(productService).deleteProductById(eq(productId));

        //when
        ResultActions result = mockMvc.perform(delete("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully."));
    }

    @Test
    void deleteProductById_ReturnsNotFoundStatus_ThrowProductDoesNotExistException() throws Exception {
        //given
        int productId = 21;

        doThrow(ProductNotFoundException.class).when(productService).deleteProductById(anyInt());

        //when
        ResultActions result = mockMvc.perform(delete("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    public void updateProductById_ReturnsOkStatus_ProductRequestIsValid() throws Exception {
        //given
        int productId = 1;

        ProductRequest productRequest = createProductRequest();

        doNothing().when(productService).updateProductById(eq(productId), eq(productRequest));

        //when
        ResultActions result = mockMvc.perform(put("/api/products/edit/{id}", productId)
                .content(asJsonString(productRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string("Product updated successfully."));
    }

    @Test
    void updateProductById_ReturnsNotFoundStatus_ThrowProductDoesNotExistException() throws Exception {
        //given
        int productId = 1;

        ProductRequest productRequest = createProductRequest();

        doThrow(ProductNotFoundException.class).when(productService).updateProductById(anyInt(), any(ProductRequest.class));

        //when
        ResultActions result = mockMvc.perform(put("/api/products/edit/{id}", productId)
                .content(asJsonString(productRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void getProductBySearchTerm_ReturnsOkStatus_ProductsWithCorrectSearchTerm() throws Exception {
        //given
        String searchTerm = "Product";
        String sortType = "ascending";
        String category = "RINGS";

        List<ProductDto> productDtoList = createProductDtosMockedList(category);

        when(productService.getProductBySearchTerm(eq(searchTerm), eq(sortType))).thenReturn(productDtoList);

        //when
        ResultActions result = mockMvc.perform(get("/api/products/search")
                .param("searchTerm", searchTerm)
                .param("sort", sortType));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getProductBySearchTerm_ReturnsNotFoundStatus_ProductListIsEmpty() throws Exception {
        //given
        String searchTerm = "Jewellery";
        String sortType = "descending";

        when(productService.getProductBySearchTerm(eq(searchTerm), eq(sortType))).thenReturn(Collections.emptyList());

        //when
        ResultActions result = mockMvc.perform(get("/api/products/search")
                .param("searchTerm", searchTerm)
                .param("sort", sortType));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    private List<ProductDto> createProductDtosMockedList(String category) {
        ProductDto productDto1 = createProductDto(1, "Product name 1", 100, category);
        ProductDto productDto2 = createProductDto(2, "Product name 2", 200, category);

        return List.of(productDto1, productDto2);
    }
}
