package com.anastasiat.author.client;

import com.anastasiat.author.entity.AuthorDTO;
import com.anastasiat.author.entity.AuthorPageDTO;

import java.time.LocalDate;
import java.util.List;

public interface AuthorRestClient {

    List<AuthorDTO> findAllAuthors();

    AuthorPageDTO findAllAuthors(int page, int size);

    AuthorDTO createAuthor(String firstName, String lastName, String middleName, LocalDate birthDate);

    AuthorDTO findAuthor(Integer authorId);

    void deleteAuthor(Integer authorId);
}
