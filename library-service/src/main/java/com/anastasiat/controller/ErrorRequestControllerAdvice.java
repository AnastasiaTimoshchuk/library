package com.anastasiat.controller;

import com.anastasiat.exception.AlreadyExistsException;
import com.anastasiat.exception.NotExistsException;
import com.anastasiat.exception.UnavailableOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class ErrorRequestControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty(
                "errors",
                exception.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .toList()
        );

        return ResponseEntity.badRequest()
                .body(problemDetail);
    }

    @ExceptionHandler({AlreadyExistsException.class, UnavailableOperationException.class})
    public ResponseEntity<ProblemDetail> handleAlreadyExistsAndUnavailableOperationException(
            RuntimeException exception,
            Locale locale
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty(
                "errors",
                List.of(messageSource.getMessage(exception.getMessage(), new Object[0], exception.getMessage(), locale))
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(problemDetail);
    }

    @ExceptionHandler(NotExistsException.class)
    public ResponseEntity<ProblemDetail> handleNotExistsException(NotExistsException exception, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setProperty(
                "errors",
                List.of(messageSource.getMessage(exception.getMessage(), new Object[0], exception.getMessage(), locale))
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(problemDetail);
    }
}
