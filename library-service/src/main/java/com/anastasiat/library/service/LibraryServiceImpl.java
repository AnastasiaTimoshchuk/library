package com.anastasiat.library.service;

import com.anastasiat.author.entity.Author;
import com.anastasiat.author.service.AuthorService;
import com.anastasiat.book.entity.Book;
import com.anastasiat.book.service.BookService;
import com.anastasiat.exception.AlreadyExistsException;
import com.anastasiat.exception.NotExistsException;
import com.anastasiat.exception.UnavailableOperationException;
import com.anastasiat.reader.entity.Reader;
import com.anastasiat.reader.service.ReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

    private final BookService bookService;
    private final AuthorService authorService;
    private final ReaderService readerService;

    @Override
    @Transactional
    public Book createBook(String title, Integer authorId) {

        Author author = authorService.findAuthorById(authorId)
                .orElseThrow(() -> new AlreadyExistsException("library.errors.author.not_found"));

        if (bookService.findBookByTitleAndAuthorId(title, authorId).isPresent()) {
            throw new AlreadyExistsException("library.errors.book.already_exists");
        }

        Book book = bookService.createBook(title, author);
        log.info("Книга создана id {}", book.getId());
        return book;
    }

    @Override
    @Transactional
    public void deleteBook(Integer bookId) {
        Book book = bookService.findBookById(bookId)
                .orElseThrow(() -> new NotExistsException("library.errors.book.not_found"));
        if (book.getIsBorrowed()) {
            throw new UnavailableOperationException("library.errors.book.delete_unavailable");
        }
        bookService.deleteBookById(bookId);
        log.info("Книга удалена id {}", bookId);
    }

    @Override
    @Transactional
    public void borrowBook(Integer readerId, Integer bookId) {
        Reader reader = readerService.findReaderById(readerId)
                .orElseThrow(() -> new NotExistsException("library.errors.reader.not_found"));

        Book book = bookService.findBookByIdForUpdate(bookId)
                .orElseThrow(() -> new NotExistsException("library.errors.book.not_found"));

        if (book.getIsBorrowed()) {
            throw new UnavailableOperationException("library.errors.book.not_available");
        }

        bookService.updateBook(book, true, reader);
        log.info("Книга id {} взята читателем id {}", bookId, readerId);
    }

    @Override
    @Transactional
    public void returnBook(Integer readerId, Integer bookId) {
        Book book = bookService.findBookByIdForUpdate(bookId)
                .orElseThrow(() -> new NotExistsException("library.errors.book.not_found"));

        if (!book.getIsBorrowed()) {
            throw new UnavailableOperationException("library.errors.book.available");
        }

        if (!readerId.equals(book.getReader().getId())) {
            throw new UnavailableOperationException("library.errors.book.not_available");
        }

        bookService.updateBook(book, false, null);
        log.info("Книга id {} возвращена читателем id {}", bookId, readerId);
    }
}
