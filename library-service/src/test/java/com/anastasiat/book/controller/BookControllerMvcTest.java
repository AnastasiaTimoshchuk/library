package com.anastasiat.book.controller;

import com.anastasiat.book.controller.request.CreateBookRequest;
import com.anastasiat.book.entity.Book;
import com.anastasiat.book.entity.BookPage;
import com.anastasiat.book.service.BookService;
import com.anastasiat.library.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerMvcTest {

    private MockMvc mockMvc;

    @Mock
    private MessageSource messageSource;

    @Mock
    private BookService bookService;

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private BookController bookController;

    private Book book;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();

        book = new Book();
        book.setId(1);
        book.setTitle("Название книги");
    }

    @Test
    void testCreateBookValid() throws Exception {
        when(libraryService.createBook("Название книги", 1)).thenReturn(book);

        mockMvc.perform(post("/library-api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Название книги\", \"authorId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Название книги"));
    }

    @Test
    void testCreateBookInvalid() throws Exception {
        String requestBody = """
                {
                    "title": "",
                    "authorId": "1"
                }
                """;

        mockMvc.perform(post("/library-api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    void testFindBookById() throws Exception {
        when(bookService.findBookById(1)).thenReturn(java.util.Optional.of(book));

        mockMvc.perform(get("/library-api/books/{bookId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Название книги"));
    }

    @Test
    void testFindBooks() throws Exception {
        when(bookService.findAllBooks(0, 20)).thenReturn(new BookPage(
                Collections.emptyList(),
                0,
                0,
                true
        ));

        mockMvc.perform(get("/library-api/books")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void testBorrowBook() throws Exception {
        doNothing().when(libraryService).borrowBook(1, 1);

        mockMvc.perform(post("/library-api/books/borrow")
                        .param("readerId", "1")
                        .param("bookId", "1"))
                .andExpect(status().isOk());

        verify(libraryService, times(1)).borrowBook(1, 1);
    }

    @Test
    void testReturnBook() throws Exception {
        doNothing().when(libraryService).returnBook(1, 1);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Книга успешно возвращена читателем");

        mockMvc.perform(post("/library-api/books/return")
                        .param("readerId", "1")
                        .param("bookId", "1"))
                .andExpect(status().isOk());

        verify(libraryService, times(1)).returnBook(1, 1);
    }

    @Test
    void testDeleteBook() throws Exception {
        doNothing().when(libraryService).deleteBook(1);

        mockMvc.perform(delete("/library-api/books/{bookId}", 1))
                .andExpect(status().isNoContent());

        verify(libraryService, times(1)).deleteBook(1);
    }
}

