package io.codefresh.gradleexample.exceptions.service;

public class TenderNotFoundException extends RuntimeException {
    public TenderNotFoundException(String message) {
        super(message);
    }
}
