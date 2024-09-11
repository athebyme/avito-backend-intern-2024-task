package io.codefresh.gradleexample.exceptions.dto_exceptions;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
