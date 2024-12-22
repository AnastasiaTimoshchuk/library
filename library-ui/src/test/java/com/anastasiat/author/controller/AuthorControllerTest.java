package com.anastasiat.author.controller;

import com.anastasiat.author.client.AuthorRestClient;
import com.anastasiat.author.controller.payload.NewAuthorPayload;
import com.anastasiat.author.entity.AuthorDTO;
import com.anastasiat.author.entity.AuthorPageDTO;
import com.anastasiat.exception.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AuthorRestClient authorRestClient;

    @InjectMocks
    private AuthorController authorController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAuthorsList() throws Exception {
        AuthorPageDTO authorPageDTO = new AuthorPageDTO(List.of(new AuthorDTO(1, "Тест", "Тестов", null, LocalDate.now())), 10, 1, true);
        when(authorRestClient.findAllAuthors(0, 10)).thenReturn(authorPageDTO);

        mockMvc.perform(get("/library/authors/list?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/authors/list"))
                .andExpect(model().attributeExists("authors", "currentPage", "totalPages"));
    }

    @Test
    void getNewAuthorPage_Success() throws Exception {
        mockMvc.perform(get("/library/authors/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/authors/new_author"));
    }

    @Test
    void createAuthor_Success() throws Exception {
        NewAuthorPayload payload = new NewAuthorPayload("Тест", "Тестов", "Отчество", LocalDate.now());
        AuthorDTO authorDTO = new AuthorDTO(1, "Тест", "Тестов", "Отчество", LocalDate.now());
        when(authorRestClient.createAuthor(any(), any(), any(), any())).thenReturn(authorDTO);

        mockMvc.perform(post("/library/authors/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(view().name("library/authors/author"))
                .andExpect(model().attributeExists("author"));
    }

    @Test
    void createAuthor_BadRequest() throws Exception {
        NewAuthorPayload payload = new NewAuthorPayload("", "", "", null);
        String payloadJson = objectMapper.writeValueAsString(payload);
        BadRequestException badRequestException = new BadRequestException(List.of("Ошибка"));

        when(authorRestClient.createAuthor(any(), any(), any(), any()))
                .thenThrow(badRequestException);

        mockMvc.perform(post("/library/authors/create")
                        .content(payloadJson))
                .andExpect(status().isOk())
                .andExpect(view().name("library/authors/new_author"))
                .andExpect(model().attributeExists("errors"))
                .andExpect(model().attributeExists("payload"));
    }

    @Test
    void getAuthor_Success() throws Exception {
        AuthorDTO authorDTO = new AuthorDTO(1, "Тест", "Тестов", "Отчество", LocalDate.now());
        when(authorRestClient.findAuthor(1)).thenReturn(authorDTO);

        mockMvc.perform(get("/library/authors/{authorId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("library/authors/author"))
                .andExpect(model().attributeExists("author"));
    }

    @Test
    void deleteAuthor_Success() throws Exception {
        AuthorDTO authorDTO = new AuthorDTO(1, "Тест", "Тестов", "Отчество", LocalDate.now());

        mockMvc.perform(post("/library/authors/{authorId}/delete", 1)
                        .flashAttr("author", authorDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/library/authors/list"));
    }

    @Test
    void deleteAuthor_BadRequest() throws Exception {
        AuthorDTO authorDTO = new AuthorDTO(1, "Тест", "Тестов", "Отчество", LocalDate.now());
        BadRequestException badRequestException = new BadRequestException(List.of("Unable to delete author"));

        doThrow(badRequestException).when(authorRestClient).deleteAuthor(1);

        mockMvc.perform(post("/library/authors/{authorId}/delete", 1)
                        .flashAttr("author", authorDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("library/authors/author"))
                .andExpect(model().attributeExists("errors"))
                .andExpect(model().attributeExists("author"));
    }
}
