package com.anastasiat.reader.controller;

import com.anastasiat.reader.controller.request.CreateReaderRequest;
import com.anastasiat.reader.entity.Reader;
import com.anastasiat.reader.entity.ReaderPage;
import com.anastasiat.reader.service.ReaderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReaderController.class)
class ReaderControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReaderService readerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findReaderById_ShouldReturnReader_WhenExists() throws Exception {
        Reader reader = new Reader();
        reader.setId(1);
        reader.setFirstName("Тест");
        reader.setLastName("Тестов");
        when(readerService.findReaderById(1)).thenReturn(Optional.of(reader));

        mockMvc.perform(get("/library-api/readers/{readerId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Тест")))
                .andExpect(jsonPath("$.lastName", is("Тестов")));
    }

    @Test
    void findReaderById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(readerService.findReaderById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/library-api/readers/{readerId}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllReaders_ShouldReturnAllReaders() throws Exception {
        Reader reader1 = new Reader();
        reader1.setId(1);
        Reader reader2 = new Reader();
        reader2.setId(2);
        when(readerService.findAllReaders()).thenReturn(List.of(reader1, reader2));

        mockMvc.perform(get("/library-api/readers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void findReaders_ShouldReturnPagedReaders() throws Exception {
        ReaderPage readerPage = new ReaderPage(List.of(new Reader(), new Reader()), 2, 1, true);
        when(readerService.findAllReaders(0, 10)).thenReturn(readerPage);

        mockMvc.perform(get("/library-api/readers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.last", is(true)));
    }

    @Test
    void createReader_ShouldReturnCreated_WhenValidRequest() throws Exception {
        CreateReaderRequest request = new CreateReaderRequest("Test", "Тестов", null, "test.test@example.com");
        Reader reader = new Reader();
        reader.setId(1);
        reader.setFirstName("Test");
        when(readerService.createReader("Test", "Тестов", null, "test.test@example.com")).thenReturn(reader);

        mockMvc.perform(post("/library-api/readers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/library-api/readers/1")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Test")));
    }

    @Test
    void createReader_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        CreateReaderRequest request = new CreateReaderRequest("", "Тестов", null, "invalid-email");

        mockMvc.perform(post("/library-api/readers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(4)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    "errors": [
                                        "Имя не указано",
                                        "Имя ограничено 100 символами",
                                        "Имя неккоректно",
                                        "Email некорректный"
                                    ]
                                }"""));
    }

    @Test
    void deleteReader_ShouldReturnNoContent_WhenSuccess() throws Exception {
        doNothing().when(readerService).deleteReaderById(1);

        mockMvc.perform(delete("/library-api/readers/{readerId}", 1))
                .andExpect(status().isNoContent());
        verify(readerService, times(1)).deleteReaderById(1);
    }
}
