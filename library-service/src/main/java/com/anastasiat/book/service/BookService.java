package com.anastasiat.book.service;

import com.anastasiat.author.entity.Author;
import com.anastasiat.book.entity.Book;
import com.anastasiat.book.entity.BookPage;
import com.anastasiat.reader.entity.Reader;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Book createBook(String title, Author author);

    void updateBook(Book book, boolean isBorrowed, Reader reader);

    Optional<Book> findBookById(Integer bookId);

    BookPage findAllBooks(int page, int size);

    Optional<Book> findBookByIdForUpdate(Integer bookId);

    Optional<Book> findBookByTitleAndAuthorId(String title, Integer authorId);

    List<Book> findBooksByReaderId(Integer readerId);

    List<Book> findBooksByAuthorId(Integer authorId);

    void deleteBookById(Integer bookId);
}
