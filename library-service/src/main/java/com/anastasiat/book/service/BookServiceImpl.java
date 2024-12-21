package com.anastasiat.book.service;

import com.anastasiat.author.entity.Author;
import com.anastasiat.book.entity.Book;
import com.anastasiat.book.entity.BookPage;
import com.anastasiat.book.repository.BookRepository;
import com.anastasiat.reader.entity.Reader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final ZoneId zoneId;
    private final BookRepository bookRepository;

    @Override
    public Book createBook(String title, Author author) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);

        return bookRepository.save(book);
    }

    @Override
    public void updateBook(Book book, boolean isBorrowed, Reader reader) {
        book.setIsBorrowed(isBorrowed);
        book.setBorrowDate(isBorrowed ? LocalDate.now(zoneId) : null);
        book.setReader(reader);

        bookRepository.save(book);
    }

    @Override
    public Optional<Book> findBookById(Integer bookId) {
        return bookRepository.findById(bookId);
    }

    @Override
    public BookPage findAllBooks(int page, int size) {
        Page<Book> bookPage = bookRepository.findAll(PageRequest.of(page, size));
        return new BookPage(
                bookPage.getContent(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isLast()
        );
    }

    @Override
    public Optional<Book> findBookByIdForUpdate(Integer bookId) {
        return bookRepository.findByIdForUpdate(bookId);
    }

    @Override
    public Optional<Book> findBookByTitleAndAuthorId(String title, Integer authorId) {
        return bookRepository.findByTitleAndAuthorId(title, authorId);
    }

    @Override
    public List<Book> findBooksByReaderId(Integer readerId) {
        return bookRepository.findByReaderId(readerId);
    }

    @Override
    public List<Book> findBooksByAuthorId(Integer authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    @Override
    public void deleteBookById(Integer bookId) {
        bookRepository.deleteById(bookId);
    }
}
