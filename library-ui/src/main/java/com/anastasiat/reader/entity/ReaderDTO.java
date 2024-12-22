package com.anastasiat.reader.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReaderDTO {

    private Integer id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
}
