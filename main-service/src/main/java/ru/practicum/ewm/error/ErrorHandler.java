package ru.practicum.ewm.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {
    private ApiError build(HttpStatus status, String reason, String message) {
        return ApiError.builder()
                .errors(Collections.emptyList())
                .message(message)
                .reason(reason)
                .status(status.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

//    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiError handle400(Exception e) {
//        return build(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage());
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        String allError = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s",
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "Incorrectly made request", allError);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolation(final ConstraintViolationException e) {
        String allError = e.getConstraintViolations().stream()
                .map(violation -> String.format("Field: %s. Error: %s",
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "Incorrectly made request", allError);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle404(NotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "The required object was not found.", e.getMessage());
    }

    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle409(Exception e) {
        return build(HttpStatus.CONFLICT, "Integrity constraint has been violated.", e.getMessage());
    }
}