package uk.co.glamoor.customers.exception;

import lombok.Getter;

@Getter
public class APIException extends RuntimeException {

    private final int priority;

    public APIException(String message, Exception ex, int priority) {
        super(message, ex);
        this.priority = priority;
    }

    public APIException(String message, int priority) {
        super(message);
        this.priority = priority;
    }

}
