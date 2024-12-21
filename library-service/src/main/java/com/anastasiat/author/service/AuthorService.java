package com.anastasiat.author.service;

import com.anastasiat.author.entity.Author;
import com.anastasiat.author.entity.AuthorPage;

import java.time.LocalDate;
import java.util.Optional;

public interface AuthorService {

    Optional<Author> findAuthorById(Integer authorId);

    Iterable<Author> findAllAuthors();

    AuthorPage findAllAuthors(int page, int size);

    Author createAuthor(String firstName, String lastName, String middleName, LocalDate birthDate);

    void deleteAuthor(Integer authorId);
}
