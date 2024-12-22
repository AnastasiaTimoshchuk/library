package com.anastasiat.author.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthorPageDTO {
    private List<AuthorDTO> content;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
