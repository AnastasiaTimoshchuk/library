package com.anastasiat.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BadRequestException extends RuntimeException {

    private final List<String> errors;

    public BadRequestException(List<String> errors) {
        this.errors = errors;
    }
}
