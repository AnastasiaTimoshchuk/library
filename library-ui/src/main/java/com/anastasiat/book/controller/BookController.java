package com.anastasiat.book.controller;

import com.anastasiat.author.client.AuthorRestClient;
import com.anastasiat.book.client.BookRestClient;
import com.anastasiat.book.controller.payload.NewBookPayload;
import com.anastasiat.book.entity.BookDTO;
import com.anastasiat.book.entity.BookPageDTO;
import com.anastasiat.exception.BadRequestException;
import com.anastasiat.reader.client.ReaderRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("library/books")
public class BookController {

    private final BookRestClient bookRestClient;
    private final AuthorRestClient authorRestClient;
    private final ReaderRestClient readerRestClient;

    @GetMapping("list")
    public String getBooksList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        BookPageDTO bookPage = bookRestClient.findAllBooks(page, size);
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        return "library/books/list";
    }

    @GetMapping("create")
    public String getNewBookPage(Model model) {
        model.addAttribute("authors", authorRestClient.findAllAuthors());
        return "library/books/new_book";
    }

    @PostMapping("create")
    public String createBook(NewBookPayload payload, Model model) {
        try {
            BookDTO book = bookRestClient.createBook(payload.title(), payload.authorId());
            model.addAttribute("book", book);
            model.addAttribute("readers", readerRestClient.findAllReaders());
            return "library/books/book";
        } catch (BadRequestException exception) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());
            return "library/books/new_book";
        }
    }

    @GetMapping("/{bookId}")
    public String getBook(@PathVariable("bookId") Integer bookId, Model model) {
        BookDTO book = bookRestClient.findBook(bookId);
        model.addAttribute("book", book);
        model.addAttribute("readers", readerRestClient.findAllReaders());
        return "library/books/book";
    }

    @PostMapping("borrow")
    public String borrowBook(Integer readerId, Integer bookId) {
        bookRestClient.borrowBook(readerId, bookId);
        return "redirect:/library/books/%d".formatted(bookId);
    }

    @PostMapping("return")
    public String returnBook(Integer readerId, Integer bookId) {
        bookRestClient.returnBook(readerId, bookId);
        return "redirect:/library/books/%d".formatted(bookId);
    }

    @PostMapping("/{bookId}/delete")
    public String deleteBook(@PathVariable("bookId") Integer bookId) {
        bookRestClient.deleteBook(bookId);
        return "redirect:/library/books/list";
    }
}
