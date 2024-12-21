package com.anastasiat.book.client;

import com.anastasiat.book.entity.BookDTO;
import com.anastasiat.book.entity.BookPageDTO;

public interface BookRestClient {

    BookPageDTO findAllBooks(int page, int size);

    BookDTO createBook(String title, Integer authorId);

    BookDTO findBook(Integer bookId);

    void borrowBook(Integer readerId, Integer bookId);

    void returnBook(Integer readerId, Integer bookId);

    void deleteBook(Integer bookId);
}
