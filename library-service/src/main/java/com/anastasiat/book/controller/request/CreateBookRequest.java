package com.anastasiat.book.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBookRequest(
        @NotBlank(message = "{library.book.create.errors.title_is_null}")
        @Size(min = 1, max = 200, message = "{library.book.create.errors.title_size_is_invalid}")
        String title,

        @NotNull(message = "{library.book.create.errors.authorId_is_null}")
        Integer authorId
) {
}
