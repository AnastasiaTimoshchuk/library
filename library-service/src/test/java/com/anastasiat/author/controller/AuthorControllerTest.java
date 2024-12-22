package com.anastasiat.author.controller;

import com.anastasiat.author.controller.request.CreateAuthorRequest;
import com.anastasiat.author.entity.Author;
import com.anastasiat.author.service.AuthorService;
import com.anastasiat.exception.NotExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    @Test
    void findAuthorById_ShouldReturnAuthor_WhenAuthorExists() {
        Author mockAuthor = new Author(1, "Test", "Testov", null, LocalDate.of(2000, 1, 1));
        when(authorService.findAuthorById(1)).thenReturn(Optional.of(mockAuthor));

        Author author = authorController.findAuthorById(1);

        assertNotNull(author);
        assertEquals(1, author.getId());
        assertEquals("Test", author.getFirstName());
        assertEquals("Testov", author.getLastName());
        assertNull(author.getMiddleName());
        verify(authorService, times(1)).findAuthorById(1);
    }

    @Test
    void findAuthorById_ShouldThrowException_WhenAuthorNotExist() {
        when(authorService.findAuthorById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotExistsException.class, () -> authorController.findAuthorById(1));
        verify(authorService, times(1)).findAuthorById(1);
    }

    @Test
    void findAllAuthors_ShouldReturnIterableOfAuthors() {
        Iterable<Author> mockAuthors = List.of(
                new Author(1, "Test", "Testov", "", LocalDate.of(2000, 1, 1)),
                new Author(2, "Test", "Testov", "", LocalDate.of(2000, 2, 2))
        );
        when(authorService.findAllAuthors()).thenReturn(mockAuthors);

        Iterable<Author> authors = authorController.findAllAuthors();

        assertNotNull(authors);
        assertEquals(2, ((List<Author>) authors).size());
        verify(authorService, times(1)).findAllAuthors();
    }

    @Test
    void createAuthor_ShouldReturnCreatedAuthor() throws Exception {
        CreateAuthorRequest request = new CreateAuthorRequest("Test", "Testov", null, LocalDate.of(1970, 1, 1));
        Author mockAuthor = new Author(1, "Test", "Testov", "", LocalDate.of(1970, 1, 1));
        BindingResult bindingResult = new MapBindingResult(Map.of(), "payload");
        when(authorService.createAuthor(anyString(), anyString(), any(), any())).thenReturn(mockAuthor);

        ResponseEntity<?> response = authorController.createAuthor(request, bindingResult, UriComponentsBuilder.newInstance());

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        verify(authorService, times(1)).createAuthor(anyString(), anyString(), any(), any());
    }

    @Test
    void deleteAuthor_ShouldInvokeServiceMethod() {
        doNothing().when(authorService).deleteAuthor(1);

        ResponseEntity<Void> response = authorController.deleteAuthor(1);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(authorService, times(1)).deleteAuthor(1);
    }
}