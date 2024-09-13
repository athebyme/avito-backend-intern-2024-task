package io.codefresh.gradleexample.exceptions.service.tenders;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TenderNotFoundException extends RuntimeException {
    public TenderNotFoundException(String message) {
        super(message);
    }
}
