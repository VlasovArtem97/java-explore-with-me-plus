package error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {
    private error.ApiError build(HttpStatus status, String reason, String message) {
        return error.ApiError.builder()
                .errors(List.of())
                .message(message)
                .reason(reason)
                .status(status.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public error.ApiError handle400(Exception e) {
        return build(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage());
    }

    @ExceptionHandler(error.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public error.ApiError handle404(error.NotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "The required object was not found.", e.getMessage());
    }

    @ExceptionHandler({error.ConflictException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public error.ApiError handle409(Exception e) {
        return build(HttpStatus.CONFLICT, "Integrity constraint has been violated.", e.getMessage());
    }
}