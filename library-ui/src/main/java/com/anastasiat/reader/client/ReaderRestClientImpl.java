package com.anastasiat.reader.client;

import com.anastasiat.exception.BadRequestException;
import com.anastasiat.reader.controller.payload.NewReaderPayload;
import com.anastasiat.reader.entity.ReaderDTO;
import com.anastasiat.reader.entity.ReaderPageDTO;
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
public class ReaderRestClientImpl implements ReaderRestClient {

    private static final ParameterizedTypeReference<List<ReaderDTO>> READERS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private static final ParameterizedTypeReference<ReaderPageDTO> READER_PAGE_DTO_PARAMETERIZED_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient libraryRestClient;

    @Override
    public List<ReaderDTO> findAllReaders() {
        return libraryRestClient
                .get()
                .uri("library-api/readers/all")
                .retrieve()
                .body(READERS_TYPE_REFERENCE);
    }

    @Override
    public ReaderPageDTO findAllReaders(int page, int size) {
        return libraryRestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("library-api/readers")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(READER_PAGE_DTO_PARAMETERIZED_TYPE_REFERENCE);
    }

    @Override
    public ReaderDTO createReader(String firstName, String lastName, String middleName, String email) {
        try {
            return libraryRestClient
                    .post()
                    .uri("/library-api/readers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new NewReaderPayload(firstName, lastName, middleName, email))
                    .retrieve()
                    .body(ReaderDTO.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public ReaderDTO findReader(Integer readerId) {
        try {
            return libraryRestClient.get()
                    .uri("/library-api/readers/{readerId}", readerId)
                    .retrieve()
                    .body(ReaderDTO.class);
        } catch (HttpClientErrorException.NotFound exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new NoSuchElementException(problemDetail.getProperties().get("errors").toString());
        }
    }

    @Override
    public void deleteReader(Integer readerId) {
        try {
            libraryRestClient
                    .delete()
                    .uri("/library-api/readers/{readerId}", readerId)
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
