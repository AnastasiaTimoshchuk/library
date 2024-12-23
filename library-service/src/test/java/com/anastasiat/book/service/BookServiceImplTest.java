package com.anastasiat.book.service;

import com.anastasiat.author.entity.Author;
import com.anastasiat.book.entity.Book;
import com.anastasiat.book.entity.BookPage;
import com.anastasiat.book.repository.BookRepository;
import com.anastasiat.reader.entity.Reader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.zone.ZoneRules;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ZoneId zoneId;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private Author author;
    private Reader reader;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1);
        author.setFirstName("Имя");
        author.setLastName("Фамилия");

        reader = new Reader();
        reader.setId(1);
        reader.setFirstName("Имя имя");
        reader.setLastName("Фамилия-фамилия");

        book = new Book();
        book.setId(1);
        book.setTitle("Название книги");
        book.setAuthor(author);
    }

    @Test
    void testCreateBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book createdBook = bookService.createBook("Название книги", author);

        assertNotNull(createdBook);
        assertEquals("Название книги", createdBook.getTitle());
        assertEquals(author, createdBook.getAuthor());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(zoneId.getRules()).thenReturn(ZoneRules.of(ZoneOffset.UTC));

        bookService.updateBook(book, true, reader);

        assertTrue(book.getIsBorrowed());
        assertNotNull(book.getBorrowDate());
        assertEquals(reader, book.getReader());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testFindBookById() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.findBookById(1);

        assertTrue(foundBook.isPresent());
        assertEquals(book, foundBook.get());
        verify(bookRepository, times(1)).findById(1);
    }

    @Test
    void testFindAllBooks() {
        Page<Book> bookPage = mock(Page.class);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(bookPage.getContent()).thenReturn(List.of(book));
        when(bookPage.getTotalElements()).thenReturn(1L);
        when(bookPage.getTotalPages()).thenReturn(1);
        when(bookPage.isLast()).thenReturn(true);

        BookPage result = bookService.findAllBooks(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.isLast());
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testFindBookByIdForUpdate() {
        when(bookRepository.findByIdForUpdate(1)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.findBookByIdForUpdate(1);

        assertTrue(foundBook.isPresent());
        assertEquals(book, foundBook.get());
        verify(bookRepository, times(1)).findByIdForUpdate(1);
    }

    @Test
    void testFindBookByTitleAndAuthorId() {
        when(bookRepository.findByTitleAndAuthorId("Название книги", 1)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.findBookByTitleAndAuthorId("Название книги", 1);

        assertTrue(foundBook.isPresent());
        assertEquals(book, foundBook.get());
        verify(bookRepository, times(1)).findByTitleAndAuthorId("Название книги", 1);
    }

    @Test
    void testExistBooksByReaderId() {
        when(bookRepository.existsByReaderId(1)).thenReturn(true);

        boolean hasBook = bookService.existBooksByReaderId(1);

        assertTrue(hasBook);
        verify(bookRepository, times(1)).existsByReaderId(1);
    }

    @Test
    void testExistBooksByAuthorId() {
        when(bookRepository.existsByAuthorId(1)).thenReturn(true);

        boolean hasBook = bookService.existBooksByAuthorId(1);

        assertTrue(hasBook);
        verify(bookRepository, times(1)).existsByAuthorId(1);
    }

    @Test
    void testDeleteBookById() {
        doNothing().when(bookRepository).deleteById(1);

        bookService.deleteBookById(1);

        verify(bookRepository, times(1)).deleteById(1);
    }
}
