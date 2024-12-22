package com.anastasiat.reader.controller;

import com.anastasiat.exception.BadRequestException;
import com.anastasiat.reader.client.ReaderRestClient;
import com.anastasiat.reader.entity.ReaderDTO;
import com.anastasiat.reader.entity.ReaderPageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReaderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReaderRestClient readerRestClient;

    @InjectMocks
    private ReaderController readerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(readerController).build();
    }

    @Test
    void createReader_Success() throws Exception {
        ReaderDTO readerDTO = new ReaderDTO(1, "Тест", "Тестов", "Отчетсво", "test@example.com");

        when(readerRestClient.createReader(any(), any(), any(), any()))
                .thenReturn(readerDTO);

        mockMvc.perform(post("/library/readers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"firstName\": \"Тест\", \"lastName\": \"Тестов\", \"LastName\": \"Отчетсво\", \"email\": \"test@example.com\" }"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/readers/reader"))
                .andExpect(model().attributeExists("reader"));
    }

    @Test
    void createReader_Fail() throws Exception {
        BadRequestException badRequestException = new BadRequestException(List.of("Ошибка"));

        when(readerRestClient.createReader(any(), any(), any(), any()))
                .thenThrow(badRequestException);

        mockMvc.perform(post("/library/readers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"firstName\": \"Тест\", \"lastName\": \"Тестов\", \"LastName\": \"Отчетсво\", \"email\": \"invalid-email\" }"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/readers/new_reader"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    void getReadersList_Success() throws Exception {
        ReaderPageDTO readerPageDTO = new ReaderPageDTO();
        readerPageDTO.setContent(Collections.singletonList(new ReaderDTO(1, "Тест", "Тестов", "Отчетсво", "test@example.com")));
        readerPageDTO.setTotalPages(1);

        when(readerRestClient.findAllReaders(0, 10)).thenReturn(readerPageDTO);

        mockMvc.perform(get("/library/readers/list")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/readers/list"))
                .andExpect(model().attributeExists("readers"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"));
    }

    @Test
    void getReader_Success() throws Exception {
        ReaderDTO readerDTO = new ReaderDTO(1, "Тест", "Тестов", "Отчетсво", "test@example.com");

        when(readerRestClient.findReader(1)).thenReturn(readerDTO);

        mockMvc.perform(get("/library/readers/{readerId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("library/readers/reader"))
                .andExpect(model().attributeExists("reader"));
    }

    @Test
    void deleteReader_Success() throws Exception {
        doNothing().when(readerRestClient).deleteReader(1);

        mockMvc.perform(post("/library/readers/{readerId}/delete", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/readers/list"));
    }

    @Test
    void deleteReader_Fail() throws Exception {
        BadRequestException badRequestException = new BadRequestException(List.of("Ошибка"));

        doThrow(badRequestException).when(readerRestClient).deleteReader(1);

        mockMvc.perform(post("/library/readers/{readerId}/delete", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("library/readers/reader"))
                .andExpect(model().attributeExists("errors"));
    }
}
