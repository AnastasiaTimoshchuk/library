package com.anastasiat.book.controller;

import com.anastasiat.author.client.AuthorRestClient;
import com.anastasiat.author.entity.AuthorDTO;
import com.anastasiat.book.client.BookRestClient;
import com.anastasiat.book.controller.payload.NewBookPayload;
import com.anastasiat.book.entity.BookDTO;
import com.anastasiat.book.entity.BookPageDTO;
import com.anastasiat.exception.BadRequestException;
import com.anastasiat.reader.client.ReaderRestClient;
import com.anastasiat.reader.entity.ReaderDTO;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookRestClient bookRestClient;

    @Mock
    private AuthorRestClient authorRestClient;

    @Mock
    private ReaderRestClient readerRestClient;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void getBooksList_Success() throws Exception {
        BookPageDTO bookPageDTO = new BookPageDTO();
        bookPageDTO.setContent(Collections.singletonList(new BookDTO(1, "Название", null, true, null, null)));
        bookPageDTO.setTotalPages(1);

        when(bookRestClient.findAllBooks(0, 20)).thenReturn(bookPageDTO);

        mockMvc.perform(get("/library/books/list")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/books/list"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"));
    }

    @Test
    void getNewBookPage_Success() throws Exception {
        AuthorDTO authorDTO = new AuthorDTO(1, "Тест", "Тестов", "Отчество", LocalDate.now());
        when(authorRestClient.findAllAuthors()).thenReturn(Collections.singletonList(authorDTO));

        mockMvc.perform(get("/library/books/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/books/new_book"))
                .andExpect(model().attributeExists("authors"));
    }

    @Test
    void createBook_Success() throws Exception {
        NewBookPayload payload = new NewBookPayload("Название", 1);
        BookDTO bookDTO = new BookDTO(1, "Название", null, true, null, null);
        ReaderDTO readerDTO = new ReaderDTO(1, "Тест", "Тестов", null, "test@example.com");

        doReturn(bookDTO).when(bookRestClient).createBook(any(), any());
        doReturn(Collections.singletonList(readerDTO)).when(readerRestClient).findAllReaders();

        mockMvc.perform(post("/library/books/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Название\", \"authorId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/books/book"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("readers"));
    }

    @Test
    void createBook_Fail() throws Exception {
        NewBookPayload payload = new NewBookPayload("Название", 1);
        BadRequestException badRequestException = new BadRequestException(List.of("Ошибка"));

        when(bookRestClient.createBook(any(), any())).thenThrow(badRequestException);

        mockMvc.perform(post("/library/books/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Название\", \"authorId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/books/new_book"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    void getBook_Success() throws Exception {
        BookDTO bookDTO = new BookDTO(1, "Название", null, true, null, null);
        when(bookRestClient.findBook(1)).thenReturn(bookDTO);
        when(readerRestClient.findAllReaders()).thenReturn(Collections.singletonList(any()));

        mockMvc.perform(get("/library/books/{bookId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("library/books/book"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("readers"));
    }

    @Test
    void borrowBook_Success() throws Exception {
        doNothing().when(bookRestClient).borrowBook(1, 1);

        mockMvc.perform(post("/library/books/borrow")
                        .param("readerId", "1")
                        .param("bookId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/books/1"));
    }

    @Test
    void returnBook_Success() throws Exception {
        doNothing().when(bookRestClient).returnBook(1, 1);

        mockMvc.perform(post("/library/books/return")
                        .param("readerId", "1")
                        .param("bookId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/books/1"));
    }

    @Test
    void deleteBook_Success() throws Exception {
        doNothing().when(bookRestClient).deleteBook(1);

        mockMvc.perform(post("/library/books/{bookId}/delete", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/books/list"));
    }
}
