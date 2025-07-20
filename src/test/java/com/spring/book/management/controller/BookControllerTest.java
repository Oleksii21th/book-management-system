package com.spring.book.management.controller;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    private static MockMvc mockMvc;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context) {
        mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup.
                        SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeEach
    void setupDatabase(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/book/remove-all-books.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/book/add-default-books.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    static void teardown(DataSource dataSource) {
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
    @WithMockUser(roles = {"USER"})
    void findBookById_ReturnsBook() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto book = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertThat(book.getIsbn()).isEqualTo("ISBN2");
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
    void updateBook_ReturnsUpdatedBook() throws Exception {
        CreateBookRequestDto createRequest = createBookRequestDto();
        String createJson = objectMapper.writeValueAsString(createRequest);

        MvcResult createResult = mockMvc.perform(post("/api/books")
                        .content(createJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto createdBook = objectMapper.readValue(createResult.getResponse().getContentAsString(), BookDto.class);

        CreateBookRequestDto updateRequest = createRequest;
        updateRequest.setAuthor("Updated");
        updateRequest.setTitle("Updated");

        String updateJson = objectMapper.writeValueAsString(updateRequest);

        MvcResult updateResult = mockMvc.perform(put("/api/books/" + createdBook.getId())
                        .content(updateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto updatedBook = objectMapper.readValue(updateResult.getResponse().getContentAsString(), BookDto.class);

        assertThat(updatedBook.getTitle()).isEqualTo("Updated");
        assertThat(updatedBook.getAuthor()).isEqualTo("Updated");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteBookById_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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

    private CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto dto = new CreateBookRequestDto(
                "NewTest",
                "test",
                "ISBN3",
                BigDecimal.ONE,
                "test",
                null);
        dto.setCategoryIds(Set.of(1L));
        return dto;
    }
}
