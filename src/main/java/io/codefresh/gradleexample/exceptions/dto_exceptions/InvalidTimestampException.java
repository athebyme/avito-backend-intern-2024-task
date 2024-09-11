package io.codefresh.gradleexample.exceptions.dto_exceptions;

public class InvalidTimestampException extends RuntimeException {
    public InvalidTimestampException(String message) {
        super(message);
    }
}
