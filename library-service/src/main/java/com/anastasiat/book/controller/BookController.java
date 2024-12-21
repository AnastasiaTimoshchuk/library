package com.anastasiat.book.controller;

import com.anastasiat.book.controller.request.CreateBookRequest;
import com.anastasiat.book.entity.Book;
import com.anastasiat.book.entity.BookPage;
import com.anastasiat.book.service.BookService;
import com.anastasiat.exception.NotExistsException;
import com.anastasiat.library.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("library-api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final LibraryService libraryService;

    @GetMapping("/{bookId}")
    public Book findBookById(@PathVariable("bookId") Integer bookId) {
        return bookService.findBookById(bookId)
                .orElseThrow(() -> new NotExistsException("library.errors.book.not_found"));
    }

    @GetMapping
    public BookPage findBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return bookService.findAllBooks(page, size);
    }

    @PostMapping
    public ResponseEntity<?> createBook(
            @Valid @RequestBody CreateBookRequest createBookRequest,
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
            Book book = libraryService.createBook(
                    createBookRequest.title(),
                    createBookRequest.authorId()
            );
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/library/books/{bookId}")
                            .build(Map.of("bookId", book.getId())))
                    .body(book);
        }
    }

    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestParam Integer readerId, @RequestParam Integer bookId) {
        libraryService.borrowBook(readerId, bookId);
        return ResponseEntity.ok("Книга успешно взята читателем");
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestParam Integer readerId, @RequestParam Integer bookId) {
        libraryService.returnBook(readerId, bookId);
        return ResponseEntity.ok("Книга успешно возвращена читателем");
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable("bookId") Integer bookId) {
        libraryService.deleteBook(bookId);
        return ResponseEntity.noContent()
                .build();
    }
}
