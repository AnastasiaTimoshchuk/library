package com.anastasiat.book.entity;

import com.anastasiat.author.entity.AuthorDTO;
import com.anastasiat.reader.entity.ReaderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookDTO {

    private Integer id;
    private String title;
    private AuthorDTO author;
    private Boolean isBorrowed;
    private LocalDate borrowDate;
    private ReaderDTO reader;
}
