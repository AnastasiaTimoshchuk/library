package com.anastasiat.reader.entity;

import lombok.Data;

import java.util.List;

@Data
public class ReaderPageDTO {
    private List<ReaderDTO> content;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
