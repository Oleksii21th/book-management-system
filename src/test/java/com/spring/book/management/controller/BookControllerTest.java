package com.spring.book.management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.book.management.dto.book.BookDto;
import com.spring.book.management.dto.book.CreateBookRequestDto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
class BookControllerTest {
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

    @BeforeEach
    void setupDatabase(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/book/remove-all-books.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/book/add-default-books.sql"));
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to tear down database", exception);
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
                    new ClassPathResource("database/book/remove-all-books.sql"));
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to tear down database", exception);
        }
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void findAllBooks_ReturnsList() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> books = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(books).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findAllBooks_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void findBookById_ReturnsBook() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto book = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertThat(book.getIsbn()).isEqualTo("ISBN");
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void findBookById_NonExistentId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/books/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual.getIsbn()).isEqualTo(requestDto.getIsbn());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createBook_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        CreateBookRequestDto invalidDto = new CreateBookRequestDto(
                null,
                null,
                null,
                null,
                null,
                null);
        String json = objectMapper.writeValueAsString(invalidDto);

        MvcResult result = mockMvc.perform(post("/api/books")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(response).contains("Title is required",
                "Author is required",
                "ISBN is required",
                "Price is required");
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void createBook_VerifyAsUser_ReturnsForbidden() throws Exception {
        String json = objectMapper.writeValueAsString(createBookRequestDto());

        mockMvc.perform(post("/api/books")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateBook_ReturnsUpdatedBook() throws Exception {
        CreateBookRequestDto createRequest = createBookRequestDto();
        String updateJson = objectMapper.writeValueAsString(createRequest);

        MvcResult updateResult = mockMvc.perform(put("/api/books/2")
                        .content(updateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto updatedBook = objectMapper.readValue(
                updateResult.getResponse().getContentAsString(), BookDto.class);

        assertThat(updatedBook.getTitle()).isEqualTo("NewTest");
        assertThat(updatedBook.getAuthor()).isEqualTo("NewTest");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateBook_NonExistentId_ReturnsNotFound() throws Exception {
        String json = objectMapper.writeValueAsString(createBookRequestDto());

        mockMvc.perform(put("/api/books/9999")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteBookById_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/books/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteBookById_NonExistentId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/books/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void searchBooks_ByTitle_ReturnsMatch() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books/search")
                        .param("titles", "test2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> resultList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(resultList).isNotEmpty();
        assertThat(resultList.get(0).getTitle()).contains("test2");
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void searchBooks_ByTitleAndAuthor_ReturnsCorrectBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books/search")
                        .param("titles", "test2")
                        .param("authors", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> books = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );

        assertThat(books).isNotEmpty();
        assertThat(books.get(0).getTitle()).containsIgnoringCase("test2");
        assertThat(books.get(0).getAuthor()).containsIgnoringCase("test");
    }

    private CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto dto = new CreateBookRequestDto(
                "NewTest",
                "NewTest",
                "ISBN3",
                BigDecimal.ONE,
                "test",
                null);
        dto.setCategoryIds(Set.of(1L));
        return dto;
    }
}
