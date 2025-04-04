package uk.co.glamoor.customers.exception;

import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mongodb.MongoException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Reactive validation exception
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<String>> handleWebExchangeBindException(WebExchangeBindException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));

        logger.warn("Validation error: {} - {}", ex.getMessage(), errors);
        return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
    }

    // For @Validated at controller class level
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        cv -> cv.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        logger.warn("Constraint violation: {} - {}", ex.getMessage(), errors);
        return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
    }

    // Reactive input exceptions
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<String>> handleServerWebInputException(ServerWebInputException ex) {
        logger.warn("Input error: {}", ex.getMessage());
        return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
    }

    // Business exceptions
    @ExceptionHandler({
            IllegalArgumentException.class,
            EntityNotFoundException.class,
            APIException.class
    })
    public Mono<ResponseEntity<String>> handleBusinessExceptions(RuntimeException ex) {
        logger.error("Business error: {}", ex.getMessage());
        return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
    }

    // MongoDB exceptions
    @ExceptionHandler(MongoException.class)
    public Mono<ResponseEntity<String>> handleMongoException(MongoException ex) {
        logger.error("MongoDB error: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity.internalServerError().body(ex.getMessage()));
    }

    // Fallback
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity.internalServerError()
                .body("An unexpected error occurred"));
    }
}

