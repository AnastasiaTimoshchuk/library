package com.anastasiat.author.entity;

import lombok.Data;

import java.util.List;

@Data
public class AuthorPageDTO {
    private List<AuthorDTO> content;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
