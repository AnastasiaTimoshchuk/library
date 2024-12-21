package com.anastasiat.reader.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateReaderRequest(
        @NotBlank(message = "{library.reader.create.errors.firstName_is_null}")
        @Size(min = 1, max = 100, message = "{library.reader.create.errors.firstName_size_is_invalid}")
        @Pattern(
                regexp = "^[A-Za-zА-Яа-я](?:[A-Za-zА-Яа-я\\s-]*[A-Za-zА-Яа-я])?$",
                message = "{library.author.create.errors.firstName_is_invalid}"
        )
        String firstName,

        @NotBlank(message = "{library.reader.create.errors.lastName_is_null}")
        @Size(min = 1, max = 100, message = "{library.reader.create.errors.lastName_size_is_invalid}")
        @Pattern(
                regexp = "^[A-Za-zА-Яа-я](?:[A-Za-zА-Яа-я\\s-]*[A-Za-zА-Яа-я])?$",
                message = "{library.author.create.errors.lastName_is_invalid}"
        )
        String lastName,

        @Size(max = 100, message = "{library.reader.create.errors.middleName_size_is_invalid}")
        @Pattern(
                regexp = "^[A-Za-zА-Яа-я](?:[A-Za-zА-Яа-я\\s-]*[A-Za-zА-Яа-я])?$|^$",
                message = "{library.author.create.errors.middleName_is_invalid}"
        )
        String middleName,

        @NotBlank(message = "{library.reader.create.errors.email_is_null}")
        @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "{library.reader.create.errors.email_is_invalid}")
        String email
) {
}
