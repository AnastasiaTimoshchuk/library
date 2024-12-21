package com.anastasiat.book.entity;

import com.anastasiat.author.entity.Author;
import com.anastasiat.reader.entity.Reader;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "library", name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @Column(name = "title")
    private String title;

    @Column(name = "is_borrowed")
    private Boolean isBorrowed = false;

    @Column(name = "borrow_date")
    private LocalDate borrowDate;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;
}
