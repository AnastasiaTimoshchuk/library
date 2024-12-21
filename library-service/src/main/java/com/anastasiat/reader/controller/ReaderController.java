package com.anastasiat.reader.controller;

import com.anastasiat.exception.NotExistsException;
import com.anastasiat.reader.controller.request.CreateReaderRequest;
import com.anastasiat.reader.entity.Reader;
import com.anastasiat.reader.entity.ReaderPage;
import com.anastasiat.reader.service.ReaderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("library-api/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    @GetMapping("/{readerId}")
    public Reader findReaderById(@PathVariable("readerId") Integer readerId) {
        return readerService.findReaderById(readerId)
                .orElseThrow(() -> new NotExistsException("library.errors.reader.not_found"));
    }

    @GetMapping("all")
    public Iterable<Reader> findAllReaders() {
        return readerService.findAllReaders();
    }

    @GetMapping
    public ReaderPage findReaders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return readerService.findAllReaders(page, size);
    }

    @PostMapping
    public ResponseEntity<?> createReader(
            @Valid @RequestBody CreateReaderRequest createReaderRequest,
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
            Reader reader = readerService.createReader(
                    createReaderRequest.firstName(),
                    createReaderRequest.lastName(),
                    createReaderRequest.middleName(),
                    createReaderRequest.email()
            );
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/library/readers/{readerId}")
                            .build(Map.of("readerId", reader.getId())))
                    .body(reader);
        }
    }

    @DeleteMapping("/{readerId}")
    public ResponseEntity<Void> deleteReader(@PathVariable("readerId") Integer readerId) {
        readerService.deleteReaderById(readerId);
        return ResponseEntity.noContent()
                .build();
    }
}
