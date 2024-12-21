package com.anastasiat.author.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AuthorDTO {

    private Integer id;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthDate;
}
