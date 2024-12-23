package com.anastasiat.author.service;

import com.anastasiat.author.entity.Author;
import com.anastasiat.author.entity.AuthorPage;
import com.anastasiat.author.repository.AuthorRepository;
import com.anastasiat.book.entity.Book;
import com.anastasiat.book.service.BookService;
import com.anastasiat.exception.AlreadyExistsException;
import com.anastasiat.exception.NotExistsException;
import com.anastasiat.exception.UnavailableOperationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void findAuthorById_ShouldReturnAuthor_WhenAuthorExists() {
        Integer authorId = 1;
        Author author = new Author();
        author.setId(authorId);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        Optional<Author> result = authorService.findAuthorById(authorId);

        assertTrue(result.isPresent());
        assertEquals(author, result.get());
        verify(authorRepository, times(1)).findById(authorId);
    }

    @Test
    void findAuthorById_ShouldReturnEmptyOptional_WhenAuthorТестовsNotExist() {
        Integer authorId = 1;

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        Optional<Author> result = authorService.findAuthorById(authorId);

        assertTrue(result.isEmpty());
        verify(authorRepository, times(1)).findById(authorId);
    }

    @Test
    void findAllAuthors_ShouldReturnAllAuthors() {
        List<Author> authors = List.of(new Author(), new Author());

        when(authorRepository.findAll()).thenReturn(authors);

        Iterable<Author> result = authorService.findAllAuthors();

        assertNotNull(result);
        assertEquals(authors.size(), ((List<Author>) result).size());
        verify(authorRepository, times(1)).findAll();
    }

    @Test
    void findAllAuthors_ShouldReturnPagedAuthors() {
        List<Author> authors = List.of(new Author(), new Author());
        Page<Author> authorPage = new PageImpl<>(authors);

        when(authorRepository.findAll(PageRequest.of(0, 10))).thenReturn(authorPage);

        AuthorPage result = authorService.findAllAuthors(0, 10);

        assertNotNull(result);
        assertEquals(authors.size(), result.getContent().size());
        verify(authorRepository, times(1)).findAll(PageRequest.of(0, 10));
    }

    @Test
    void createAuthor_ShouldCreateAuthor_WhenValidRequest() {
        String firstName = "Тест";
        String lastName = "Тестов";
        String middleName = "Тестович";
        LocalDate birthDate = LocalDate.of(1980, 1, 1);
        Author author = new Author();
        author.setId(1);
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setMiddleName(middleName);
        author.setBirthDate(birthDate);

        when(authorRepository.findByFirstNameAndLastNameAndMiddleNameAndBirthDate(firstName, lastName, middleName, birthDate))
                .thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        Author result = authorService.createAuthor(firstName, lastName, middleName, birthDate);

        assertNotNull(result);
        assertEquals(author.getId(), result.getId());
        verify(authorRepository, times(1)).findByFirstNameAndLastNameAndMiddleNameAndBirthDate(firstName, lastName, middleName, birthDate);
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void createAuthor_ShouldThrowException_WhenAuthorAlreadyExists() {
        String firstName = "Тест";
        String lastName = "Тестов";
        String middleName = "Тестович";
        LocalDate birthDate = LocalDate.of(1980, 1, 1);
        Author author = new Author();

        when(authorRepository.findByFirstNameAndLastNameAndMiddleNameAndBirthDate(firstName, lastName, middleName, birthDate))
                .thenReturn(Optional.of(author));

        assertThrows(AlreadyExistsException.class, () -> authorService.createAuthor(firstName, lastName, middleName, birthDate));

        verify(authorRepository, times(1)).findByFirstNameAndLastNameAndMiddleNameAndBirthDate(firstName, lastName, middleName, birthDate);
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void deleteAuthor_ShouldDeleteAuthor_WhenNoBooksExist() {
        Integer authorId = 1;
        Author author = new Author();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookService.existBooksByAuthorId(authorId)).thenReturn(false);

        assertDoesNotThrow(() -> authorService.deleteAuthor(authorId));

        verify(authorRepository, times(1)).findById(authorId);
        verify(bookService, times(1)).existBooksByAuthorId(authorId);
        verify(authorRepository, times(1)).deleteById(authorId);
    }

    @Test
    void deleteAuthor_ShouldThrowException_WhenBooksExist() {
        Integer authorId = 1;
        Author author = new Author();
        Book book = new Book();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookService.existBooksByAuthorId(authorId)).thenReturn(true);

        assertThrows(UnavailableOperationException.class, () -> authorService.deleteAuthor(authorId));

        verify(authorRepository, times(1)).findById(authorId);
        verify(bookService, times(1)).existBooksByAuthorId(authorId);
        verify(authorRepository, never()).deleteById(authorId);
    }

    @Test
    void deleteAuthor_ShouldThrowException_WhenAuthorТестовsNotExist() {
        Integer authorId = 1;

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(NotExistsException.class, () -> authorService.deleteAuthor(authorId));

        verify(authorRepository, times(1)).findById(authorId);
        verify(bookService, never()).existBooksByAuthorId(authorId);
        verify(authorRepository, never()).deleteById(authorId);
    }
}