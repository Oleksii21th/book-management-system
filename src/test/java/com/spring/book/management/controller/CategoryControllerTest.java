package com.spring.book.management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.book.management.dto.CategoryDto;
import com.spring.book.management.dto.book.BookDtoWithoutCategoryIds;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource, @Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/book/add-default-books.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/category/"
                            + "add-default-categories-and-combined-with-book.sql"));
        } catch (SQLException exception) {
            throw new RuntimeException("Failed adding categories to database", exception);
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/category/remove-combined-book-and-category.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/book/remove-all-books.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/category/remove-all-category.sql"));
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to tear down database", exception);
        }
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void findAllCategories_ReturnsList() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> categories = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(categories).isNotEmpty();
    }

    @Test
    void findAllCategories_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void findCategoryById_ReturnsCategory() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto category = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertThat(category.id()).isEqualTo(2L);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void findCategoryById_NonExistentId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/categories/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createCategory_ValidRequestDto_Success() throws Exception {
        CategoryDto requestDto = createCategoryDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertThat(actual.id()).isNotNull();
        assertThat(actual.id()).isGreaterThan(0);
        assertThat(actual.name()).isEqualTo("Test");
        assertThat(actual.description()).isEqualTo("Test");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createCategory_MissingRequiredName_ReturnsBadRequest() throws Exception {
        CategoryDto invalidCategory = new CategoryDto(
                null,
                null,
                null);
        String json = objectMapper.writeValueAsString(invalidCategory);

        MvcResult result = mockMvc.perform(post("/api/categories")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(response).contains("Name must not be blank");
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void createCategory_AsUser_ReturnsForbidden() throws Exception {
        String json = objectMapper.writeValueAsString(createCategoryDto());

        mockMvc.perform(post("/api/categories")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateCategory_ReturnsUpdatedCategory() throws Exception {
        CategoryDto updateDto = createCategoryDto();
        String updateJson = objectMapper.writeValueAsString(updateDto);

        MvcResult result = mockMvc.perform(put("/api/categories/3")
                        .content(updateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto updated = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertThat(updated.name()).isEqualTo("Test");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateCategory_NonExistentId_ReturnsNotFound() throws Exception {
        String json = objectMapper.writeValueAsString(createCategoryDto());

        mockMvc.perform(put("/api/categories/999")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteCategory_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/categories/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteCategory_NonExistentId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/categories/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getBooksByCategoryId_ReturnsBooksList() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/2/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> books = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(books).isNotEmpty();
        assertThat(books.get(0).title()).isNotBlank();
    }

    private CategoryDto createCategoryDto() {
        return new CategoryDto(null, "Test", "Test");
    }
}
