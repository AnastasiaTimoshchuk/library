package com.anastasiat.book.entity;

import lombok.Data;

import java.util.List;

@Data
public class BookPageDTO {
    private List<BookDTO> content;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
