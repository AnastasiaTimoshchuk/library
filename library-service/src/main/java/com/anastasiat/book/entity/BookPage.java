package com.anastasiat.book.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookPage {
    private List<Book> content;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
