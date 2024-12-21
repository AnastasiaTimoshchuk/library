package com.anastasiat.book.client;

import com.anastasiat.book.controller.payload.NewBookPayload;
import com.anastasiat.book.entity.BookDTO;
import com.anastasiat.book.entity.BookPageDTO;
import com.anastasiat.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class BookRestClientImpl implements BookRestClient {

    private static final ParameterizedTypeReference<BookPageDTO> BOOKS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient libraryRestClient;

    @Override
    public BookPageDTO findAllBooks(int page, int size) {
        return libraryRestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("library-api/books")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(BOOKS_TYPE_REFERENCE);
    }

    @Override
    public BookDTO createBook(String title, Integer authorId) {
        try {
            return libraryRestClient
                    .post()
                    .uri("/library-api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new NewBookPayload(title, authorId))
                    .retrieve()
                    .body(BookDTO.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public BookDTO findBook(Integer bookId) {
        try {
            return libraryRestClient.get()
                    .uri("/library-api/books/{bookId}", bookId)
                    .retrieve()
                    .body(BookDTO.class);
        } catch (HttpClientErrorException.NotFound exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new NoSuchElementException(problemDetail.getProperties().get("errors").toString());
        }
    }

    @Override
    public void borrowBook(Integer readerId, Integer bookId) {
        libraryRestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/library-api/books/borrow")
                        .queryParam("readerId", readerId)
                        .queryParam("bookId", bookId)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    @Override
    public void returnBook(Integer readerId, Integer bookId) {
        libraryRestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/library-api/books/return")
                        .queryParam("readerId", readerId)
                        .queryParam("bookId", bookId)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve();
    }

    @Override
    public void deleteBook(Integer bookId) {
        try {
            libraryRestClient
                    .delete()
                    .uri("/library-api/books/{bookId}", bookId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new NoSuchElementException(problemDetail.getProperties().get("errors").toString());
        }
    }
}
