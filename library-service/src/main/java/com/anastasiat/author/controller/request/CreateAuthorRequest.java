package com.anastasiat.author.controller.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateAuthorRequest(
        @NotBlank(message = "{library.author.create.errors.firstName_is_null}")
        @Size(min = 1, max = 100, message = "{library.author.create.errors.firstName_size_is_invalid}")
        @Pattern(
                regexp = "^[A-Za-zА-Яа-я](?:[A-Za-zА-Яа-я\\s-]*[A-Za-zА-Яа-я])?$",
                message = "{library.author.create.errors.firstName_is_invalid}"
        )
        String firstName,

        @NotBlank(message = "{library.author.create.errors.lastName_is_null}")
        @Size(min = 1, max = 100, message = "{library.author.create.errors.lastName_size_is_invalid}")
        @Pattern(
                regexp = "^[A-Za-zА-Яа-я](?:[A-Za-zА-Яа-я\\s-]*[A-Za-zА-Яа-я])?$",
                message = "{library.author.create.errors.lastName_is_invalid}"
        )
        String lastName,

        @Size(max = 100, message = "{library.author.create.errors.middleName_size_is_invalid}")
        @Pattern(
                regexp = "^[A-Za-zА-Яа-я](?:[A-Za-zА-Яа-я\\s-]*[A-Za-zА-Яа-я])?$|^$",
                message = "{library.author.create.errors.middleName_is_invalid}"
        )
        String middleName,

        @NotNull(message = "{library.author.create.errors.birthDate_is_null}")
        @PastOrPresent(message = "{library.author.create.errors.birthDate_in_future}")
        LocalDate birthDate
) {
}
