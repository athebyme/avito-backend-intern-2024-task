package io.codefresh.gradleexample.exceptions.dto_exceptions;

public class InvalidVersionException extends RuntimeException {
    public InvalidVersionException(String message) {
        super(message);
    }
}
