package com.spring.book.management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.book.management.dto.shoppingcart.AddToCartRequestDto;
import com.spring.book.management.dto.shoppingcart.ShoppingCartResponseDto;
import com.spring.book.management.dto.shoppingcart.UpdateCartItemRequestDto;
import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context) {
        mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup
                        .SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) {
        executeSqlScripts(dataSource,
                "database/book/add-default-books.sql",
                "database/user/add-default-user.sql",
                "database/shoppingcart/add-default-shopping-cart.sql"
        );
    }

    @AfterAll
    static void tearDown(@Autowired DataSource dataSource) {
        executeSqlScripts(dataSource,
                "database/shoppingcart/remove-all-shopping-carts.sql",
                "database/book/remove-all-books.sql",
                "database/user/remove-user.sql"
        );
    }

    private static void executeSqlScripts(DataSource dataSource, String... scriptPaths) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            for (String path : scriptPaths) {
                ScriptUtils.executeSqlScript(connection, new ClassPathResource(path));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing SQL scripts", e);
        }
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void getCart_ReturnsCartWithItems() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);

        assertThat(response).isNotNull();
        assertThat(response.cartItems()).isNotEmpty();
    }

    @Test
    void getCart_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void addToCart_ValidRequest_Success() throws Exception {
        String json = createAddToCartRequestJson(2L, 2);

        MvcResult result = mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);

        assertThat(response.cartItems())
                .anyMatch(item -> item.bookId().equals(2L));
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void addToCart_ValidRequest_BadRequest() throws Exception {
        String json = createAddToCartRequestJson(999L, 2);

        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void updateCartItem_ValidRequest_Success() throws Exception {
        String json = createUpdatedCartItemRequestJson(5);

        MvcResult result = mockMvc.perform(put("/api/cart/cart-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);

        assertThat(response.cartItems())
                .anyMatch(item -> item.quantity() == 5);
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void updateCartItem_ValidRequest_BadRequest() throws Exception {
        String json = createUpdatedCartItemRequestJson(5);

        mockMvc.perform(put("/api/cart/cart-items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void deleteCartItem_ValidRequest_Success() throws Exception {
        mockMvc.perform(delete("/api/cart/cart-items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void deleteCartItem_ValidRequest_ThrowsException() throws Exception {
        mockMvc.perform(delete("/api/cart/cart-items/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private String createUpdatedCartItemRequestJson(int quantity) throws Exception {
        UpdateCartItemRequestDto dto = new UpdateCartItemRequestDto(quantity);
        return objectMapper.writeValueAsString(dto);
    }

    private String createAddToCartRequestJson(Long bookId, int quantity) throws Exception {
        AddToCartRequestDto dto = new AddToCartRequestDto(bookId, quantity);
        return objectMapper.writeValueAsString(dto);
    }
}
