package io.codefresh.gradleexample.exceptions.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEnumException extends RuntimeException {
    public InvalidEnumException(String message) {
        super(message);
    }
}
