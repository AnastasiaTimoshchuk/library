package com.anastasiat.author.client;

import com.anastasiat.author.controller.payload.NewAuthorPayload;
import com.anastasiat.author.entity.AuthorDTO;
import com.anastasiat.author.entity.AuthorPageDTO;
import com.anastasiat.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class AuthorRestClientImpl implements AuthorRestClient {

    private static final ParameterizedTypeReference<List<AuthorDTO>> AUTHORS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private static final ParameterizedTypeReference<AuthorPageDTO> AUTHOR_PAGE_DTO_PARAMETERIZED_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient libraryRestClient;

    @Override
    public List<AuthorDTO> findAllAuthors() {
        return libraryRestClient
                .get()
                .uri("library-api/authors/all")
                .retrieve()
                .body(AUTHORS_TYPE_REFERENCE);
    }

    @Override
    public AuthorPageDTO findAllAuthors(int page, int size) {
        return libraryRestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("library-api/authors")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(AUTHOR_PAGE_DTO_PARAMETERIZED_TYPE_REFERENCE);
    }

    @Override
    public AuthorDTO createAuthor(String firstName, String lastName, String middleName, LocalDate birthDate) {
        try {
            return libraryRestClient
                    .post()
                    .uri("/library-api/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new NewAuthorPayload(firstName, lastName, middleName, birthDate))
                    .retrieve()
                    .body(AuthorDTO.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }
    
    @Override
    public AuthorDTO findAuthor(Integer authorId) {
        try {
            return libraryRestClient.get()
                    .uri("/library-api/authors/{authorId}", authorId)
                    .retrieve()
                    .body(AuthorDTO.class);
        } catch (HttpClientErrorException.NotFound exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new NoSuchElementException(problemDetail.getProperties().get("errors").toString());
        }
    }

    @Override
    public void deleteAuthor(Integer authorId) {
        try {
            libraryRestClient
                    .delete()
                    .uri("/library-api/authors/{authorId}", authorId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new NoSuchElementException(problemDetail.getProperties().get("errors").toString());
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }
}
