package com.anastasiat.book.controller;

import com.anastasiat.book.controller.request.CreateBookRequest;
import com.anastasiat.book.entity.Book;
import com.anastasiat.book.entity.BookPage;
import com.anastasiat.book.service.BookService;
import com.anastasiat.exception.NotExistsException;
import com.anastasiat.library.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private BookService bookService;

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private BookController bookController;

    private Book book;
    private CreateBookRequest createBookRequest;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1);
        book.setTitle("Название книги");

        createBookRequest = new CreateBookRequest("Название книги", 1);
    }

    @Test
    void testFindBookById() {
        when(bookService.findBookById(1)).thenReturn(Optional.of(book));

        Book foundBook = bookController.findBookById(1);

        assertNotNull(foundBook);
        assertEquals("Название книги", foundBook.getTitle());
        verify(bookService, times(1)).findBookById(1);
    }

    @Test
    void testFindBookByIdNotFound() {
        when(bookService.findBookById(1)).thenReturn(Optional.empty());

        assertThrows(NotExistsException.class, () -> bookController.findBookById(1));
    }

    @Test
    void testFindBooks() {
        BookPage bookPage = new BookPage(List.of(book), 1L, 1, true);
        when(bookService.findAllBooks(0, 20)).thenReturn(bookPage);

        BookPage result = bookController.findBooks(0, 20);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookService, times(1)).findAllBooks(0, 20);
    }

    @Test
    void testCreateBookSuccess() throws Exception {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
        BindingResult bindingResult = new MapBindingResult(Map.of(), "payload");
        when(libraryService.createBook("Название книги", 1)).thenReturn(book);

        ResponseEntity<?> response = bookController.createBook(createBookRequest, bindingResult, uriBuilder);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(libraryService, times(1)).createBook("Название книги", 1);
    }

    @Test
    void testBorrowBook() {
        doNothing().when(libraryService).borrowBook(1, 1);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Книга успешно взята читателем");

        ResponseEntity<?> response = bookController.borrowBook(1, 1, Locale.ENGLISH);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Книга успешно взята читателем", response.getBody());
        verify(libraryService, times(1)).borrowBook(1, 1);
    }

    @Test
    void testReturnBook() {
        doNothing().when(libraryService).returnBook(1, 1);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Книга успешно возвращена читателем");

        ResponseEntity<?> response = bookController.returnBook(1, 1, Locale.ENGLISH);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Книга успешно возвращена читателем", response.getBody());
        verify(libraryService, times(1)).returnBook(1, 1);
    }

    @Test
    void testDeleteBook() {
        doNothing().when(libraryService).deleteBook(1);

        ResponseEntity<Void> response = bookController.deleteBook(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(libraryService, times(1)).deleteBook(1);
    }
}
