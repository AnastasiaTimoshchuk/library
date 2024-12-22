package com.anastasiat.author.controller;

import com.anastasiat.author.entity.Author;
import com.anastasiat.author.service.AuthorService;
import com.anastasiat.exception.NotExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
class AuthorControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @Test
    void findAuthorById_ShouldReturnAuthor_WhenAuthorExists() throws Exception {
        Author author = new Author(1, "Тест", "Тестов", null, LocalDate.of(1980, 1, 1));
        when(authorService.findAuthorById(1)).thenReturn(Optional.of(author));

        mockMvc.perform(get("/library-api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Тест"))
                .andExpect(jsonPath("$.lastName").value("Тестов"));
    }

    @Test
    void findAuthorById_ShouldReturn404_WhenAuthorNotExist() throws Exception {
        when(authorService.findAuthorById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/library-api/authors/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAuthor_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        Author author = new Author(1, "Тест", "Тестов", null, LocalDate.of(1980, 1, 1));
        when(authorService.createAuthor(any(), any(), any(), any())).thenReturn(author);

        String requestBody = """
                {
                    "firstName": "Тест",
                    "lastName": "Тестов",
                    "birthDate": "1980-01-01"
                }
                """;

        mockMvc.perform(post("/library-api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/library-api/authors/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Тест"))
                .andExpect(jsonPath("$.lastName").value("Тестов"))
                .andExpect(jsonPath("$.birthDate").value("1980-01-01"));
    }

    @Test
    void createAuthor_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                    "firstName": "",
                    "lastName": "Тестов",
                    "birthDate": "1980-01-01"
                }
                """;

        mockMvc.perform(post("/library-api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    "errors": [
                                        "Имя не указано",
                                        "Имя ограничено 100 символами",
                                        "Имя неккоректно"
                                    ]
                                }"""));
    }

    @Test
    void deleteAuthor_ShouldReturnNoContent_WhenAuthorIsDeleted() throws Exception {
        mockMvc.perform(delete("/library-api/authors/1"))
                .andExpect(status().isNoContent());
        verify(authorService).deleteAuthor(1);
    }

    @Test
    void deleteAuthor_ShouldReturnNotFound_WhenAuthorNotExist() throws Exception {
        doThrow(new NotExistsException("library.errors.author.not_found"))
                .when(authorService).deleteAuthor(1);

        mockMvc.perform(delete("/library-api/authors/1"))
                .andExpect(status().isNotFound());
    }
}
