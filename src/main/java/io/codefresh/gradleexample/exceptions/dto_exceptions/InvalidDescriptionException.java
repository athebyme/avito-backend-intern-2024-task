package io.codefresh.gradleexample.exceptions.dto_exceptions;

public class InvalidDescriptionException extends RuntimeException {
    public InvalidDescriptionException(String message) {
        super(message);
    }
}
