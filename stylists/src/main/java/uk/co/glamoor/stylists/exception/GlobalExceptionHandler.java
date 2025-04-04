package uk.co.glamoor.stylists.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mongodb.MongoException;

import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        logger.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> 
            errors.put(cv.getPropertyPath().toString(), cv.getMessage())
        );
        logger.warn("Constraint violation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    	logger.error("IllegalArgumentException: {}", ex.getMessage());
        ex.printStackTrace();
    	return ResponseEntity.badRequest().body(ex.getMessage());
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
    	logger.error("ResourceNotFoundException: {}", ex.getMessage());
    	return ResponseEntity.notFound().build();
    }
    
    @ExceptionHandler(MongoException.class)
    public ResponseEntity<String> handleMongoException(MongoException ex) {
    	logger.error("MongoDB error: {}", ex.getMessage(), ex);
    	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("MongoDB error: " + ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
    	logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
    }
}


