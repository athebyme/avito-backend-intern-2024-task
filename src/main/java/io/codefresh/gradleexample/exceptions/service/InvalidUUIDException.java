package io.codefresh.gradleexample.exceptions.service;

public class InvalidUUIDException extends RuntimeException {
    public InvalidUUIDException(String message) {
        super(message);
    }
}
