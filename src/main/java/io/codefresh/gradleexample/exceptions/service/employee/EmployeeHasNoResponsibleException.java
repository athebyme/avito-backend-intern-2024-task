package io.codefresh.gradleexample.exceptions.service.employee;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EmployeeHasNoResponsibleException extends RuntimeException {
    public EmployeeHasNoResponsibleException(String message) {
        super(message);
    }
}
