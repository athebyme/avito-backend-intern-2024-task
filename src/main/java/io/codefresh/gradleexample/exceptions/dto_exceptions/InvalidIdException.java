package io.codefresh.gradleexample.exceptions.dto_exceptions;

public class InvalidIdException extends RuntimeException {
    public InvalidIdException(String message) {
        super(message);
    }
}
