package com.anastasiat.library.service;

import com.anastasiat.book.entity.Book;

public interface LibraryService {

    Book createBook(String title, Integer authorId);

    void deleteBook(Integer bookId);

    void borrowBook(Integer readerId, Integer bookId);

    void returnBook(Integer readerId, Integer bookId);
}
