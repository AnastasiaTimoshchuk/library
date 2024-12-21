package com.anastasiat.reader.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReaderPage {
    private List<Reader> content;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
