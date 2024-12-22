package com.anastasiat.author.controller;

import com.anastasiat.author.controller.request.CreateAuthorRequest;
import com.anastasiat.author.entity.Author;
import com.anastasiat.author.entity.AuthorPage;
import com.anastasiat.author.service.AuthorService;
import com.anastasiat.exception.NotExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("library-api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/{authorId}")
    public Author findAuthorById(@PathVariable("authorId") Integer authorId) {
        return authorService.findAuthorById(authorId)
                .orElseThrow(() -> new NotExistsException("library.errors.author.not_found"));
    }

    @GetMapping("all")
    public Iterable<Author> findAllAuthors() {
        return authorService.findAllAuthors();
    }

    @GetMapping
    public AuthorPage findAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return authorService.findAllAuthors(page, size);
    }

    @PostMapping
    public ResponseEntity<?> createAuthor(
            @Valid @RequestBody CreateAuthorRequest createAuthorRequest,
            BindingResult bindingResult,
            UriComponentsBuilder uriComponentsBuilder
    ) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Author author = authorService.createAuthor(
                    createAuthorRequest.firstName(),
                    createAuthorRequest.lastName(),
                    createAuthorRequest.middleName(),
                    createAuthorRequest.birthDate()
            );
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/library-api/authors/{authorId}")
                            .build(Map.of("authorId", author.getId())))
                    .body(author);
        }
    }

    @DeleteMapping("/{authorId}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable("authorId") Integer authorId) {
        authorService.deleteAuthor(authorId);
        return ResponseEntity.noContent()
                .build();
    }
}
